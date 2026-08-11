# 운영 점검표와 개선 과제

## 배포 전

- [ ] `.env`의 모든 `replace_with_...` 값을 교체했는가?
- [ ] DB root와 애플리케이션 사용자의 비밀번호가 서로 다른가?
- [ ] JWT 키가 충분히 길고 저장소 이력에 포함되지 않았는가?
- [ ] HTTPS 운영 환경에서 `JWT_COOKIE_SECURE=true`인가?
- [ ] 관리자 초기 비밀번호가 강력한 값인가?
- [ ] 도메인의 A 레코드가 EC2 공인 IP를 가리키는가?
- [ ] 보안 그룹은 필요한 80/443 포트만 공개했는가?
- [ ] EC2가 SSM 관리 노드로 정상 표시되는가?
- [ ] OIDC 신뢰 정책이 해당 저장소와 `main` 브랜치로 제한됐는가?

## 배포 후

- [ ] 공개 포트폴리오의 모든 조회 API가 동작하는가?
- [ ] `/admin` 로그인과 로그아웃이 동작하는가?
- [ ] 생성·수정·삭제 API가 비로그인 사용자에게 거부되는가?
- [ ] Access Token 만료 후 자동 재발급이 동작하는가?
- [ ] HTTPS 인증서가 정상인가?
- [ ] `docker compose ps`에서 세 컨테이너가 실행 중인가?
- [ ] 백엔드와 Caddy 로그에 반복 오류가 없는가?
- [ ] MySQL과 Caddy 볼륨이 유지되는가?

## 백업과 복구

`mysql-data` 볼륨은 컨테이너 삭제로부터 데이터를 보호하지만 백업 자체는 아니다. 서버나 디스크가 손상되면 함께 잃을 수 있으므로 주기적으로 외부 저장소에 dump를 보관해야 한다.

```bash
docker compose exec -T db mysqldump \
  -uroot -p"$DB_ROOT_PASSWORD" buildlog > buildlog-backup.sql
```

백업 파일을 만드는 것만큼 실제 복원이 되는지 별도 환경에서 검증하는 것이 중요하다. `docker compose down -v`는 데이터 볼륨을 제거하므로 데이터 삭제 의도가 없으면 실행하지 않는다.

## 우선순위별 개선 과제

### 우선순위 높음

- Refresh Token 저장·회전·폐기 전략 추가
- 운영 환경의 Secure 쿠키 설정 확인
- 자동 DB 백업과 복구 절차 마련
- Spring Boot health endpoint와 배포 후 상태 검사 추가
- `ddl-auto=update`를 마이그레이션 도구로 교체

### 우선순위 중간

- 401과 403의 프런트엔드 처리 분리
- 동시 Access Token 재발급 요청을 하나로 합치기
- 로그인 시도 횟수 제한과 보안 감사 로그 추가
- Content Security Policy 등 보안 헤더 적용
- ECR을 통한 버전 배포·롤백 구현

### 유지보수 품질

- API 통합 테스트와 프런트엔드 테스트 확대
- OpenAPI 문서 또는 API 명세 추가
- 구조화 로그와 모니터링·알림 도입
- 비밀 값을 Parameter Store 또는 Secrets Manager로 관리
- 의존성과 이미지 취약점 자동 검사 추가

