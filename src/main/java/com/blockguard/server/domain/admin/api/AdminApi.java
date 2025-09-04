package com.blockguard.server.domain.admin.api;

import com.blockguard.server.domain.auth.domain.JwtToken;
import com.blockguard.server.domain.auth.enums.JwtGrantType;
import com.blockguard.server.domain.auth.infra.JwtTokenGenerator;
import com.blockguard.server.domain.news.scheduler.NewsSaveScheduler;
import com.blockguard.server.global.common.codes.ErrorCode;
import com.blockguard.server.global.common.codes.SuccessCode;
import com.blockguard.server.global.common.response.BaseResponse;
import com.blockguard.server.global.exception.BusinessExceptionHandler;
import com.blockguard.server.infra.importer.FraudUrlImporter;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Slf4j
public class AdminApi {

    private final FraudUrlImporter fraudUrlImporter;
    private final NewsSaveScheduler newsSaveScheduler;
    private final JwtTokenGenerator jwtTokenGenerator;

    @Value("${jwt.admin-secret}")
    private String adminSecret;

    @PostMapping("/login")
    @Operation(summary = "관리자 로그인")
    public BaseResponse<JwtToken> loginAdmin(@RequestParam String key) {
        if (!adminSecret.equals(key)) {
            throw new BusinessExceptionHandler(ErrorCode.INVALID_TOKEN);
        }

        JwtToken token = jwtTokenGenerator.generateToken("admin", JwtGrantType.GRANT_TYPE_ADMIN.getValue());
        return BaseResponse.of(SuccessCode.ADMIN_TOKEN_SUCCESS, token);
    }

    @PostMapping("/update/fraud-url")
    @Operation(summary = "공공 api 데이터 호출")
    public BaseResponse<Void> syncFraudUrls() {
        fraudUrlImporter.syncFraudUrlsFromOpenApi();
        return BaseResponse.of(SuccessCode.IMPORT_OPEN_API_SUCCESS);
    }


    @PostMapping("/crawl")
    @Operation(summary = "뉴스 크롤링")
    @CacheEvict(value = "news:list:v2", allEntries = true)
    public BaseResponse<Void> crawlNewsManually() {
        newsSaveScheduler.crawlingForAdmin();
        return BaseResponse.of(SuccessCode.CRWAL_DAUM_NEWS_SUCCESS);
    }
}


