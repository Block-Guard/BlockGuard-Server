package com.blockguard.server.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckEmailDuplicatedResponse {
    private boolean isDuplicated;
    private boolean isDeleted;
}
