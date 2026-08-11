# BuildLog

개발자의 경험과 성장 과정을 한곳에 기록하고 보여 주는 포트폴리오 관리 서비스입니다. 방문자는 프로젝트, 경력, 교육, 자격증, 기술 스택, 소개 정보를 열람할 수 있고, 관리자는 별도의 관리 화면에서 각 콘텐츠를 추가·수정·삭제할 수 있습니다.

## 주요 기능

- **포트폴리오 공개**: 프로필과 프로젝트, 경력, 교육, 자격증, 기술 스택을 한 화면에서 제공
- **콘텐츠 관리**: `/admin`에서 포트폴리오 항목 생성·수정·삭제
- **프로젝트 기록**: 프로젝트 유형, 기간, 설명, 기여 내용, 사용 기술을 함께 관리
- **관리자 인증**: JWT Access Token과 Refresh Token을 이용한 로그인 및 토큰 갱신
- **권한 분리**: 조회 API는 공개하고 콘텐츠 변경 API는 관리자에게만 허용
- **반응형 UI**: 데스크톱과 모바일 환경을 고려한 React 기반 화면
- **운영 배포**: Docker Compose로 React/Caddy, Spring Boot, MySQL을 함께 구성하고 HTTPS 및 API 리버스 프록시 지원

## 기술 스택

| 영역 | 기술 |
| --- | --- |
| Frontend | React 19, Vite 8, JavaScript, CSS |
| Backend | Java 21, Spring Boot 4, Spring Web, Spring Data JPA, Spring Security |
| 인증 | JWT, BCrypt, HttpOnly Refresh Token Cookie |
| Database | MySQL 8.4, H2(Test) |
| Infrastructure | Docker, Docker Compose, Caddy, AWS EC2, AWS Systems Manager |
| CI/CD | GitHub Actions, AWS OIDC |

## 프로젝트 구조

```text
BuildLog/
├── frontend/                  # React 애플리케이션
│   └── src/
│       ├── features/          # 도메인별 UI와 API 모듈
│       ├── pages/             # 포트폴리오 페이지
│       └── shared/            # 공통 컴포넌트와 API 클라이언트
├── src/
│   ├── main/java/.../buildlog # Spring Boot 애플리케이션
│   │   ├── auth              # 로그인·토큰 처리
│   │   ├── profile           # 프로필 소개
│   │   ├── project           # 프로젝트
│   │   ├── career            # 경력
│   │   ├── education         # 교육
│   │   ├── certification     # 자격증
│   │   ├── techstack         # 기술 스택
│   │   └── global            # 보안·예외·공통 응답
│   └── test                  # 백엔드 테스트
├── compose.yaml              # 운영 서비스 구성
├── Dockerfile                # 백엔드 이미지
└── .github/workflows/         # CI/CD 워크플로
```

## 실행 방법

### Docker Compose로 전체 실행

Docker Engine과 Docker Compose 플러그인이 필요합니다.

1. 환경 변수 파일을 준비합니다.

   ```bash
   cp .env.example .env
   ```

2. `.env`의 `replace_with_...` 값을 모두 변경합니다. JWT 키는 다음과 같이 생성할 수 있습니다.

   ```bash
   openssl rand -base64 64
   ```

3. 전체 서비스를 빌드하고 실행합니다.

   ```bash
   docker compose up -d --build
   docker compose ps
   ```

현재 Caddy 설정은 `yunjh.kr`과 `www.yunjh.kr`을 대상으로 HTTPS 인증서를 자동 발급합니다. 다른 도메인이나 로컬 환경에서 실행하려면 `frontend/Caddyfile`의 사이트 주소를 먼저 변경해야 합니다.

### 개발 환경에서 실행

필수 요구 사항은 Java 21, Node.js 24 이상, MySQL입니다.

1. MySQL에 데이터베이스와 사용자를 생성합니다.
2. 백엔드에 필요한 환경 변수를 설정한 후 실행합니다.

   ```powershell
   $env:DB_URL = "jdbc:mysql://localhost:3306/buildlog?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF-8&serverTimezone=Asia/Seoul"
   $env:DB_USERNAME = "buildlog_user"
   $env:DB_PASSWORD = "your_database_password"
   $env:JWT_SECRET_KEY = "your_base64_encoded_secret"
   $env:ADMIN_LOGIN_ID = "admin"
   $env:ADMIN_PASSWORD = "your_admin_password"
   .\gradlew.bat bootRun
   ```

3. 새 터미널에서 프런트엔드를 실행합니다.

   ```powershell
   Set-Location frontend
   npm ci
   npm run dev
   ```

4. Vite가 안내하는 주소(기본값 `http://localhost:5173`)로 접속합니다. 개발 서버는 `/api` 요청을 `http://localhost:8080`으로 전달합니다.

## 환경 변수

| 변수 | 설명 | 기본값 |
| --- | --- | --- |
| `DB_NAME` | Docker Compose에서 생성할 데이터베이스명 | 없음 |
| `DB_URL` | 백엔드 JDBC 연결 주소 | 없음 |
| `DB_USERNAME` | 데이터베이스 사용자 | 없음 |
| `DB_PASSWORD` | 데이터베이스 비밀번호 | 없음 |
| `DB_ROOT_PASSWORD` | Docker MySQL root 비밀번호 | 없음 |
| `JWT_SECRET_KEY` | JWT 서명용 Base64 비밀 키 | 없음 |
| `JWT_ACCESS_TOKEN_EXPIRATION` | Access Token 유효 시간(ms) | `3600000` |
| `JWT_REFRESH_TOKEN_EXPIRATION` | Refresh Token 유효 시간(ms) | `1209600000` |
| `JWT_COOKIE_SECURE` | Refresh Token 쿠키의 HTTPS 전용 여부 | `false` |
| `ADMIN_LOGIN_ID` | 초기 관리자 로그인 ID | 없음 |
| `ADMIN_PASSWORD` | 초기 관리자 비밀번호 | 없음 |

운영 HTTPS 환경에서는 `JWT_COOKIE_SECURE=true`를 사용하고 `.env` 파일과 비밀 값은 저장소에 커밋하지 마세요.

## 주요 API

| 도메인 | 경로 | 공개 범위 |
| --- | --- | --- |
| 인증 | `/api/auth` | 로그인·갱신·로그아웃 공개 |
| 프로필 | `/api/profile` | 조회 공개, 수정은 관리자 |
| 프로젝트 | `/api/projects` | 조회 공개, 변경은 관리자 |
| 경력 | `/api/careers` | 조회 공개, 변경은 관리자 |
| 교육 | `/api/educations` | 조회 공개, 변경은 관리자 |
| 자격증 | `/api/certifications` | 조회 공개, 변경은 관리자 |
| 기술 스택 | `/api/tech-stacks` | 조회 공개, 변경은 관리자 |

## 테스트와 빌드

```powershell
# 백엔드 테스트
.\gradlew.bat test

# 프런트엔드 정적 검사 및 빌드
Set-Location frontend
npm run lint
npm run build
```

## 배포 문서

- [Docker 배포 가이드](DEPLOYMENT.md)
- [GitHub Actions CI/CD 설정](CICD.md)
- [JWT 인증 설계](JWT.md)

