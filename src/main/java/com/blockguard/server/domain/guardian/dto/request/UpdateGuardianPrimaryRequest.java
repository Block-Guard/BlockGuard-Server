package com.blockguard.server.domain.guardian.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties("primary")
public class UpdateGuardianPrimaryRequest {
    @JsonProperty("isPrimary")
    private boolean isPrimary;
}
