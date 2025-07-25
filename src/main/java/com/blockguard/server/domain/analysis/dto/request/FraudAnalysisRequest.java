package com.blockguard.server.domain.analysis.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class FraudAnalysisRequest {
    private String contactMethod;
    private String counterpart;
    private List<String> requestedAction;
    private List<String> requestedInfo;
    private String appType; // 선택
    private Boolean atmGuided; // 선택
    private String suspiciousLinks; // 선택
    private String suspiciousPhoneNumbers; // 선택
    private List<String> imageUrls; // 선택
    private String messageContent; // 선택
    private String additionalDescription;

}
