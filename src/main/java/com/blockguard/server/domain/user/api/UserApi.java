package com.blockguard.server.domain.user.api;

import com.blockguard.server.domain.user.application.UserService;
import com.blockguard.server.domain.user.domain.User;
import com.blockguard.server.domain.user.dto.response.MyPageResponse;
import com.blockguard.server.global.common.codes.SuccessCode;
import com.blockguard.server.global.common.response.BaseResponse;
import com.blockguard.server.global.config.resolver.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserApi {
    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "마이페이지 조회")
    public BaseResponse<MyPageResponse> getMyPageInfo(@Parameter(hidden = true) @CurrentUser User user){
        MyPageResponse myPageResponse = MyPageResponse.from(user);
        return BaseResponse.of(SuccessCode.GET_MY_PAGE_SUCCESS, myPageResponse);
    }

    @DeleteMapping("/withdraw")
    @Operation(summary = "회원 탈퇴")
    public BaseResponse<Void> withdraw(@Parameter(hidden = true) @CurrentUser User user){
        userService.withdraw(user.getId());
        return BaseResponse.of(SuccessCode.WITHDRAW_SUCCESS);
    }

}
