# BlockGuard-server

본 레포지토리는 **BlockGuard**의 서버 애플리케이션입니다.  
Spring Boot 기반으로 개발되었으며, AI 서버와 연동하여 사기 유형을 분석하고, 사용자 신고 과정을 단계별로 관리합니다.

---

## 📌 ERD (Entity Relationship Diagram)

서비스에서 사용하는 주요 도메인 구조를 ERD로 정리했습니다.

- [ERD Diagram Link](https://dbdiagram.io/d/BlockGuard_ERD-686d1997f413ba3508d99080)
  <img width="1920" height="994" alt="image" src="https://github.com/user-attachments/assets/54a18ee7-f831-45a4-b4bb-173986c8708c" />


---

## 📌 API 명세서

REST API 엔드포인트 및 스펙은 아래 문서를 참고하세요.

- [API 명세서 (Swagger 링크)](https://www.blockguard.shop/swagger-ui/index.html)
- 여기 API 명세서 노션 문서 추가

---

## 📌 아키텍처 다이어그램

서비스 전체 아키텍처 다이어그램입니다.

<img width="1368" height="1002" alt="image" src="https://github.com/user-attachments/assets/a2ab4818-0240-48ef-b9b5-ffb099068bc8" />


구성 요소:
- AWS EC2: 서버 애플리케이션 컨테이너 실행 (Docker)
- AWS RDS (MySQL): 영속성 데이터 저장
- AWS S3: 사용자 업로드 파일 및 리소스 저장
- AI Server: GPT 모델 기반 사기 유형 분석
- CI/CD (Docker, GitHub Actions): 자동 빌드 및 배포 파이프라인