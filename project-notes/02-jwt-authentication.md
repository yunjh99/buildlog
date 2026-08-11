# JWT 인증과 인가

## 인증과 인가의 차이

- 인증(Authentication): 로그인한 사용자가 누구인지 확인
- 인가(Authorization): 해당 사용자가 요청한 작업을 할 수 있는지 확인

BuildLog에서는 로그인 시 사용자를 인증하고, 변경 API 호출 시 `ADMIN` 역할인지 검사해 인가한다.

## 현재 토큰 저장 방식

| 토큰 | 유효 시간 기본값 | 저장 위치 | 전송 방법 |
| --- | ---: | --- | --- |
| Access Token | 1시간 | 브라우저 `sessionStorage` | `Authorization: Bearer ...` |
| Refresh Token | 14일 | HttpOnly 쿠키 | `/api/auth` 요청에 자동 포함 |

Access Token은 JavaScript에서 읽을 수 있어 API 헤더에 넣기 쉽지만 XSS 공격에는 노출될 수 있다. Refresh Token은 HttpOnly 쿠키라 JavaScript로 읽을 수 없으며, `SameSite=Strict`로 교차 사이트 요청 위험을 줄인다. 운영 HTTPS에서는 반드시 `JWT_COOKIE_SECURE=true`여야 한다.

## JWT 내용

`JwtProvider`는 두 토큰에 다음 정보를 넣는다.

```text
sub       = 관리자 로그인 ID
role      = ADMIN
tokenType = access 또는 refresh
iat       = 발급 시각
exp       = 만료 시각
```

`tokenType`을 별도 claim으로 두기 때문에 Refresh Token을 Access Token 자리에 사용하는 실수를 막을 수 있다. 서버는 서명을 확인한 후 기대한 토큰 종류와 일치하는지도 검사한다.

## 로그인 흐름

```text
1. React → POST /api/auth/login { loginId, password }
2. AuthService가 DB에서 사용자를 조회
3. BCrypt로 입력 비밀번호와 저장된 해시를 비교
4. Access Token과 Refresh Token 발급
5. Access Token은 JSON 응답으로 전달
6. Refresh Token은 HttpOnly 쿠키로 전달
7. React가 Access Token을 sessionStorage에 저장
```

애플리케이션이 처음 실행될 때 `AdminInitializer`는 환경 변수의 관리자 정보로 계정을 한 번 생성한다. 비밀번호는 BCrypt 해시로 저장된다. 이미 같은 로그인 ID가 있으면 환경 변수를 바꾸어도 기존 비밀번호는 자동 변경되지 않는다.

## 인증된 요청 흐름

`apiClient.js`는 저장된 Access Token이 있으면 API 요청에 Bearer 헤더를 붙인다. `JwtAuthenticationFilter`는 요청마다 다음 순서로 처리한다.

1. `Authorization` 헤더가 `Bearer `로 시작하는지 확인
2. JWT 서명, 만료 시각, `tokenType=access` 확인
3. 토큰의 `sub`, `role`로 `CustomUserDetails` 생성
4. `Authentication`을 `SecurityContextHolder`에 저장
5. Spring Security가 URL 권한 규칙 검사

현재 권한 규칙은 다음과 같다.

```text
/api/auth/login, /refresh, /logout → 모두 허용
GET /api/**                        → 모두 허용
그 외 /api/**                     → ROLE_ADMIN 필요
```

`hasRole("ADMIN")`는 내부적으로 `ROLE_ADMIN` 권한을 확인한다.

## 만료와 자동 재발급

일반 API가 401 또는 403을 반환하면 프런트엔드는 `/api/auth/refresh`를 한 번 호출한다. Refresh Token 쿠키가 유효하면 서버가 두 토큰을 새로 발급하고, 원래 요청을 새 Access Token으로 한 번 재시도한다. 재발급도 실패하면 토큰을 지우고 로그인 만료 이벤트를 발생시킨다.

## 로그아웃

로그아웃은 브라우저의 Access Token을 삭제하고 Refresh Token 쿠키의 만료 시간을 0으로 설정한다. JWT 자체는 서버 세션이 아니므로 발급된 토큰을 서버에서 즉시 무효화하는 처리는 현재 없다.

## 현재 구현의 한계와 개선 방향

1. **Refresh Token 서버 저장소가 없다.** 탈취된 토큰은 만료 전까지 사용할 수 있다. DB나 Redis에 토큰 식별자와 상태를 저장하고 회전 시 이전 토큰을 폐기하는 방식이 더 안전하다.
2. **Access Token이 sessionStorage에 있다.** XSS 방어를 위해 CSP, 입력값 처리, 의존성 점검이 중요하다. 모든 토큰을 HttpOnly 쿠키로 옮긴다면 CSRF 방어도 함께 설계해야 한다.
3. **401과 403을 모두 재발급 대상으로 본다.** 403은 권한 부족일 수도 있으므로 일반적으로 만료를 나타내는 401에서만 재발급하는 편이 의미가 정확하다.
4. **동시 요청 재발급 잠금이 없다.** 여러 요청이 동시에 실패하면 refresh가 중복 호출될 수 있다. 프런트엔드에서 하나의 refresh Promise를 공유할 수 있다.
5. **키 설명과 실제 처리를 일치시켜야 한다.** 현재 코드는 문자열의 UTF-8 바이트를 HMAC 키로 사용한다. Base64 문자열을 넣는다면 디코딩해서 쓸지, 충분히 긴 일반 문자열을 쓸지 규칙을 명확히 해야 한다.

