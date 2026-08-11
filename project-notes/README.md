# BuildLog 프로젝트 학습 노트

이 폴더는 BuildLog를 구현하면서 알아야 했던 개념과 현재 코드의 동작 방식을 설명한다. 설정 절차만 나열하기보다 요청이 어떤 파일을 거쳐 처리되는지, 왜 이런 구성을 선택했는지, 다음에는 무엇을 개선해야 하는지를 함께 기록했다.

## 문서 순서

1. [프로젝트 구조와 요청 흐름](01-architecture.md)
2. [JWT 인증과 인가](02-jwt-authentication.md)
3. [CI/CD와 운영 배포](03-cicd-deployment.md)
4. [이 프로젝트를 위해 알아야 할 핵심 개념](04-lessons-learned.md)
5. [운영 점검표와 개선 과제](05-operations-checklist.md)

## 한눈에 보는 구성

```text
사용자 브라우저
  └─ Caddy :80/:443
       ├─ 정적 요청 ──> React 빌드 파일
       └─ /api/* ─────> Spring Boot :8080
                            └─ JPA ──> MySQL :3306

GitHub main push
  ├─ 백엔드 테스트
  ├─ 프런트엔드 검사·빌드
  └─ AWS OIDC → SSM → EC2에서 Docker Compose 재배포
```

## 주요 기술

- 프런트엔드: React, Vite, JavaScript
- 백엔드: Java 21, Spring Boot, Spring Security, Spring Data JPA
- 데이터베이스: MySQL 8.4
- 인증: JWT Access Token + Refresh Token
- 배포: Docker Compose, Caddy, AWS EC2, AWS Systems Manager
- 자동화: GitHub Actions, AWS OIDC

상세 실행 명령은 루트의 `README.md`, 실제 배포 설정은 `DEPLOYMENT.md`, AWS 권한 생성 절차는 `CICD.md`도 함께 참고한다.

