# 프로젝트 구조와 요청 흐름

## 서비스의 역할

BuildLog는 공개 포트폴리오와 관리자용 콘텐츠 관리 기능을 한 애플리케이션에 제공한다. 프로젝트, 경력, 교육, 자격증, 기술 스택, 프로필은 누구나 조회할 수 있지만 생성·수정·삭제는 `ADMIN` 권한이 있어야 한다.

## 계층 구조

```text
Controller → Service → Repository → Database
     ↓          ↓
   DTO       Domain(Entity)
```

- Controller: HTTP 요청과 응답, 입력값 검증
- Service: 업무 규칙과 트랜잭션 경계
- Repository: JPA를 통한 데이터 접근
- Domain: 데이터 구조와 연관관계
- DTO: 외부 요청·응답과 엔티티를 분리
- global: 보안 필터, 설정, 공통 응답, 예외 처리

엔티티를 API 응답으로 직접 반환하지 않는 것이 중요하다. 엔티티를 그대로 노출하면 지연 로딩, 순환 참조, 원하지 않는 필드 노출과 API 변경 전파 문제가 생길 수 있기 때문이다.

## 조회 요청의 흐름

```text
PortfolioPage → projectApi.js → GET /api/projects
→ ProjectController → ProjectService → ProjectRepository → MySQL
→ ProjectSliceResponse → SuccessResponse → React 화면 갱신
```

`SecurityConfig`는 `GET /api/**`를 공개한다. 따라서 방문자는 토큰 없이 포트폴리오를 조회할 수 있다.

## 변경 요청의 흐름

관리자가 저장하면 `apiClient`가 `Authorization: Bearer {Access Token}` 헤더를 붙인다. `JwtAuthenticationFilter`가 토큰을 검증하고 인증 정보를 `SecurityContext`에 넣는다. 이후 Spring Security가 `ROLE_ADMIN`인지 확인한 뒤 Controller를 호출한다.

```text
관리 화면 → POST/PUT/DELETE → JwtAuthenticationFilter
→ SecurityContext에 Authentication 저장 → ADMIN 권한 검사
→ Controller → Service → Repository
```

## 프런트엔드와 백엔드 연결

개발 환경에서는 Vite가 `/api` 요청을 `localhost:8080`으로 프록시한다. 운영 환경에서는 Caddy가 같은 역할을 한다. 운영에서는 화면과 API가 같은 출처이므로 별도의 CORS 구성이 필요하지 않다.

- 개발: `localhost:5173/api/*` → Vite → `localhost:8080`
- 운영: `https://yunjh.kr/api/*` → Caddy → `backend:8080`

`WebConfig`의 CORS 허용 출처는 직접 백엔드에 접근하는 로컬 개발을 위해 `http://localhost:5173`으로 제한되어 있다.

## 데이터와 컨테이너

Docker Compose는 세 서비스를 실행한다.

- `web`: React 정적 파일을 제공하는 Caddy
- `backend`: Spring Boot 실행 JAR
- `db`: MySQL 8.4

MySQL 데이터는 `mysql-data` 볼륨에, Caddy 인증서와 설정 상태는 `caddy-data`, `caddy-config` 볼륨에 저장된다. 컨테이너를 다시 만들어도 볼륨을 삭제하지 않는 한 데이터는 유지된다.

