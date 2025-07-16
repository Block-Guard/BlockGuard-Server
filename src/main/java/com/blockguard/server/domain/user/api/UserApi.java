package com.blockguard.server.domain.user.api;

import com.blockguard.server.domain.user.application.UserService;
import com.blockguard.server.domain.user.domain.User;
import com.blockguard.server.domain.user.dto.request.UpdatePasswordRequest;
import com.blockguard.server.domain.user.dto.request.UpdateUserInfo;
import com.blockguard.server.domain.user.dto.response.MyPageResponse;
import com.blockguard.server.global.common.codes.SuccessCode;
import com.blockguard.server.global.common.response.BaseResponse;
import com.blockguard.server.global.config.resolver.CurrentUser;
import com.blockguard.server.global.config.swagger.CustomExceptionDescription;
import com.blockguard.server.global.config.swagger.SwaggerResponseDescription;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserApi {
    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "마이페이지 조회")
    public BaseResponse<MyPageResponse> getMyPageInfo(@Parameter(hidden = true) @CurrentUser User user){
        MyPageResponse myPageResponse = userService.getMyPageInfo(user);
        return BaseResponse.of(SuccessCode.GET_MY_PAGE_SUCCESS, myPageResponse);
    }

    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @CustomExceptionDescription(SwaggerResponseDescription.UPDATE_MY_PAGE_INFO_FAIL)
    @Operation(summary = "회원 정보 수정", description = "아이디를 제외하고 수정가능합니다. 생년월일 형식은 'yyyyMMDD'로 입력해야합니다.")
    public BaseResponse<Void> updateUserInfo(@Parameter(hidden = true) @CurrentUser User user,
                                             @ModelAttribute UpdateUserInfo updateUserInfo){
        userService.updateUserInfo(user.getId(), updateUserInfo);
        return BaseResponse.of(SuccessCode.UPDATE_USER_INFO_SUCCESS);

    }

    @PutMapping(value = "/me/password")
    @CustomExceptionDescription(SwaggerResponseDescription.UPDATE_MY_PAGE_INFO_FAIL)
    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호를 확인하고, 새 비밀번호로 변경합니다.")
    public BaseResponse<Void> updatePassword(@Parameter(hidden = true) @CurrentUser User user,
    @RequestBody UpdatePasswordRequest updatePasswordRequest){
       userService.updatePassword(user, updatePasswordRequest);
       return BaseResponse.of(SuccessCode.UPDATE_PASSWORD_SUCCESS);

    }

    @DeleteMapping("/withdraw")
    @Operation(summary = "회원 탈퇴")
    public BaseResponse<Void> withdraw(@Parameter(hidden = true) @CurrentUser User user){
        userService.withdraw(user.getId());
        return BaseResponse.of(SuccessCode.WITHDRAW_SUCCESS);
    }



}
