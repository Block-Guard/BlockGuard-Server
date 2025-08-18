# BlockGuard-server

본 레포지토리는 **BlockGuard**의 서버 애플리케이션입니다.  
Spring Boot 기반으로 개발되었으며, AI 서버와 연동하여 사기 유형을 분석하고, 사용자 신고 과정을 단계별로 관리합니다.

---

## 📌 ERD (Entity Relationship Diagram)

서비스에서 사용하는 주요 도메인 구조를 ERD로 정리했습니다.

- [ERD Diagram Link](https://dbdiagram.io/d/BlockGuard_ERD-686d1997f413ba3508d99080)
  <img width="1920" height="994" alt="image" src="https://github.com/user-attachments/assets/54a18ee7-f831-45a4-b4bb-173986c8708c" />

### 테이블별 용도

- **User**: 서비스의 기본 사용자 정보를 저장 (계정, 개인정보, 상태 관리).
- **Guardian**: 사용자의 보호자(연락망) 정보를 저장, 다수 가능.
- **NewsArticle**: 보이스피싱/사기 관련 뉴스 기사 수집 및 조회용 데이터.
- **FraudPhoneNumber**: 사기 의심 전화번호 저장 및 API 검증 기록.
- **FraudUrl**: 사기 의심 URL 저장 및 감지 시각 관리.
- **UserReportRecord**: 사용자가 진행 중인 신고 프로세스의 전체 레코드.
- **ReportStepProgress**: 신고 프로세스 각 단계의 진행 상황 기록.
- **ReportStepCheckbox**: 단계별 필수/권장 체크박스 항목의 완료 여부 기록.
- **FraudAnalysisRecord**: AI 분석 결과(사기 유형, 점수, 위험도)를 저장.


### 관계 요약

- 한 명의 **User**는 여러 **Guardian**(보호자)을 가질 수 있고, 여러 **UserReportRecord**(신고 진행)를 생성할 수 있으며, 여러 **FraudAnalysisRecord**(사기 분석 결과)와 연결된다.
- **UserReportRecord**는 여러 **ReportStepProgress**를 포함하며, 각 **ReportStepProgress**는 여러 **ReportStepCheckbox**를 가진다 → 즉, User → ReportRecord → StepProgress → Checkbox 구조로 계층화된다.
- **NewsArticle**, **FraudPhoneNumber**, **FraudUrl**은 외부 수집/분석 데이터 저장소로, User와 직접 FK 관계는 없지만 분석/검색 서비스와 연계된다.

---

## 📌 API 명세서

REST API 엔드포인트 및 스펙은 아래 문서를 참고하세요.

- [API 명세서 (Swagger 링크)](https://www.blockguard.shop/swagger-ui/index.html)
- 여기 API 명세서 노션 문서 추가

---

## 📌 아키텍처 다이어그램

서비스 전체 아키텍처 다이어그램입니다.

<img width="1368" height="1002" alt="image" src="https://github.com/user-attachments/assets/a2ab4818-0240-48ef-b9b5-ffb099068bc8" />


### 구성 요소:
- **AWS EC2**: 서버 애플리케이션 컨테이너 실행 (Docker)
- **AWS RDS (MySQL)**: 영속성 데이터 저장
- **AWS S3**: 사용자 업로드 파일 및 리소스 저장
- **AI Server**: GPT 모델 기반 사기 유형 분석
- **CI/CD (Docker, GitHub Actions)**: 자동 빌드 및 배포 파이프라인