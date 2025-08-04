package com.blockguard.server.infra.number;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CheckFraudNumberResponse {
    private DataBlock data;
    private ApiBlock api;

    @Getter
    @Builder
    public static class DataBlock {
        private String number;
        private String spam;

        @JsonProperty("spam_count")
        private String spamCount;

        @JsonProperty("registed_date")
        private String registedDate;

        @JsonProperty("cyber_crime")
        private String cyberCrime;

        /** 1=성공, 0=실패, 3: 실패(timeout) */
        private int success;
    }

    @Getter
    @Builder
    public static class ApiBlock {
        private boolean success;
        private int cost;
        private int ms;

        @JsonProperty("pl_id")
        private int plId;
    }

}
