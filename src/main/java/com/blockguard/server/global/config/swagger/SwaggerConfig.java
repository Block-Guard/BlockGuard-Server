package com.blockguard.server.global.config.swagger;

import com.blockguard.server.global.common.codes.ErrorCode;
import com.blockguard.server.global.common.response.ErrorResponse;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.groupingBy;

@OpenAPIDefinition(
        info = @Info(
                title = "API 명세서",
                description = "BlockGuard API 명세서",
                version = "v2"
        )
)
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        SecurityScheme apiKey = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .scheme("bearer")
                .bearerFormat("JWT");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Bearer Token");

        Server prodServer = new Server();
        prodServer.setUrl("https://www.blockguard.shop");
        prodServer.setDescription("AWS EC2 서버");

        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("Local server for testing");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("Bearer Token", apiKey))
                .addSecurityItem(securityRequirement)
                .servers(List.of(localServer, prodServer));
    }

    @Bean
    public OperationCustomizer customize() {
        return (operation, handlerMethod) -> {
            CustomExceptionDescription customExceptionDescription = handlerMethod.
                    getMethodAnnotation(CustomExceptionDescription.class);
            // addGlobalErrorResponses(operation);

            if (customExceptionDescription != null) {
                generateErrorCodeResponseExample(operation, customExceptionDescription.value());
            }
            return operation;
        };
    }

    // SwaggerResponseDescription에서 ErrorCode에 대해 ExampleHodler 생성
    private void generateErrorCodeResponseExample(Operation operation, SwaggerResponseDescription type) {
        ApiResponses responses = operation.getResponses();

        Set<ErrorCode> errorCodeList = type.getErrorCodeList();

        Map<Integer, List<ExampleHolder>> statusWithExampleHolders =
                errorCodeList.stream()
                        .map(
                                errorCode -> {
                                    return ExampleHolder.builder()
                                            .holder(
                                                    getSwaggerExample(errorCode))
                                            .code(errorCode.getCode())
                                            .name(errorCode.toString())
                                            .build();
                                }
                        ).collect(groupingBy(ExampleHolder::getCode));
        addExamplesToResponses(responses, statusWithExampleHolders);
    }


    // 주어진 ErrorCode로부터 Swagger에 넣을 예시 객체 생성
    private Example getSwaggerExample(ErrorCode errorCode) {
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);
        Example example = new Example();
        example.description(errorCode.getMsg());
        example.setValue(errorResponse);
        return example;
    }

    // 상태코드에 대해 Swagger의 ApiResponse 생성 & application/json 타입에 여라 개의 예시 추가
    private void addExamplesToResponses(ApiResponses responses, Map<Integer, List<ExampleHolder>> statusWithExampleHolders) {
        statusWithExampleHolders.forEach((status, holders) -> {
            Content content = new Content();
            MediaType mediaType = new MediaType();
            ApiResponse apiResponse = new ApiResponse();

            holders.forEach(holder -> mediaType.addExamples(holder.getName(), holder.getHolder()));
            content.addMediaType("application/json", mediaType);
            apiResponse.setContent(content);
            responses.addApiResponse(status.toString(), apiResponse);
        });
    }

    private void addGlobalErrorResponses(Operation operation) {
        ApiResponses responses = operation.getResponses();

        for (ErrorCode errorCode : ErrorCode.values()) {
            Example example = getSwaggerExample(errorCode);
            MediaType mediaType = new MediaType().addExamples(errorCode.name(), example);
            Content content = new Content().addMediaType("application/json", mediaType);

            ApiResponse apiResponse = new ApiResponse()
                    .description(errorCode.getMsg())
                    .content(content);

            responses.addApiResponse(String.valueOf(errorCode.getCode()), apiResponse);
        }
    }
}

