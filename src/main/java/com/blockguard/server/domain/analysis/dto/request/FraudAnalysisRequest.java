package com.blockguard.server.domain.analysis.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FraudAnalysisRequest {
    // (필수) 연락수단
    @NotBlank
    private String contactMethod;

    // (필수) 상대방
    @NotBlank
    private String counterpart;

    // (필수/복수가능) 요청받은 행동
    @NotEmpty
    private List<String> requestedAction;

    // (필수/복수가능) 언급된 내용
    @NotEmpty
    private List<String> requestedInfo;

    // (선택) 링크 종류
    private String linkType;

    // (선택) 개인정보 유출/범죄업무 언급 등 심리적 압박 여부
    private Boolean pressuredInfo;

    // (선택) 보안점검·금융거래 이유로 앱 설치/링크 접속 유도
    private Boolean appOrLinkRequest;

    // (선택) 다른 사람에게 연결
    private Boolean thirdPartyConnect;

    // (선택) 직책을 강조하며 권위적 태도
    private Boolean authorityPressure;

    // (선택) 범죄 연루·대출 조건 이유로 계좌/링크 입력 유도
    private Boolean accountOrLinkRequest;

    // (선택) 의심 링크(텍스트)
    private String suspiciousLinks;

    // (선택) 의심 전화번호(텍스트)
    private String suspiciousPhoneNumbers;

    // (선택) 문자 내용 입력
    private String messageContent;

    // (필수) 기타/상황 상세 설명
    @NotBlank
    private String additionalDescription;
}
