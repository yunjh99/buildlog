# CI/CD와 운영 배포

## 전체 흐름

`main` 브랜치에 push하거나 GitHub Actions에서 수동 실행하면 `.github/workflows/deploy.yml`이 동작한다.

```text
main push
  ├─ backend-test ── Gradle 테스트
  ├─ frontend-test ─ npm ci → lint → build
  └─ deploy          두 검사가 성공한 경우에만 실행
       └─ GitHub OIDC → AWS IAM Role 임시 권한
            └─ SSM SendCommand → EC2에서 Docker Compose 재배포
```

## 지속적 통합(CI)

백엔드는 Temurin Java 21을 설치하고 Gradle 캐시를 사용한 뒤 `clean test`를 실행한다. 프런트엔드는 Node.js 24와 npm 캐시를 사용하며 다음을 실행한다.

```text
npm ci          package-lock.json과 정확히 같은 의존성 설치
npm run lint    정적 검사
npm run build   실제 배포 빌드 가능 여부 확인
```

두 작업은 서로 독립적이므로 병렬 실행된다. `deploy.needs`에 두 작업이 선언되어 있어 하나라도 실패하면 배포하지 않는다.

## GitHub가 AWS에 인증하는 방식

장기 AWS Access Key를 GitHub에 저장하지 않는다. GitHub Actions가 OIDC 토큰을 발급받고 AWS IAM Role을 일시적으로 Assume한다.

1. AWS에 GitHub OIDC Provider 등록
2. 특정 저장소와 `main` 브랜치만 신뢰하는 IAM Role 생성
3. Role에 대상 EC2의 SSM 명령 실행 권한 부여
4. GitHub Variables에 Role ARN과 Instance ID 등록

짧은 수명의 임시 자격 증명을 사용하므로 고정 Access Key보다 유출과 교체 부담이 적다.

## SSH 대신 SSM을 사용하는 이유

- GitHub에 EC2 개인 키를 저장하지 않아도 된다.
- 서버의 SSH 포트 22를 배포 목적으로 공개할 필요가 없다.
- IAM으로 명령 실행 대상과 주체를 제한할 수 있다.
- 명령 상태와 표준 출력·오류를 AWS에서 조회할 수 있다.

EC2에는 `AmazonSSMManagedInstanceCore` 권한을 가진 인스턴스 역할과 실행 중인 SSM Agent가 필요하다.

## EC2에서 실행되는 배포

SSM 명령은 `/home/ubuntu/buildlog`에서 다음 작업을 수행한다.

1. `flock`으로 동시에 두 배포가 실행되는 것을 방지
2. `git pull --ff-only origin main`으로 이력 분기 없이 최신 코드 반영
3. 백엔드와 웹 Docker 이미지 빌드
4. `docker compose up -d`로 변경된 컨테이너 교체
5. `docker compose ps`로 상태 출력

GitHub Actions는 최대 90회, 10초 간격으로 SSM 상태를 확인한다. 성공 시 출력을 보여 주고, 실패·취소·시간 초과 상태면 워크플로도 실패한다. `concurrency`는 앞선 배포를 임의로 취소하지 않으며 서버의 `flock`도 중복 실행을 한 번 더 막는다.

## Docker 이미지와 런타임

백엔드는 Java 21 JDK 이미지에서 Gradle로 JAR을 만들고 최종 JRE 이미지에 결과물만 복사한다. 애플리케이션은 root가 아닌 `buildlog` 사용자로 실행된다.

프런트엔드는 Node 이미지에서 Vite 빌드를 수행하고 최종 Caddy 이미지에는 정적 결과물만 복사한다. 개발 도구와 소스 전체가 운영 이미지에 남지 않아 크기와 공격 표면을 줄인다.

## Caddy의 역할

- React 정적 파일 제공
- `/api/*` 요청을 Spring Boot로 리버스 프록시
- 도메인의 TLS 인증서 자동 발급·갱신
- 존재하지 않는 SPA 경로를 `/index.html`로 연결

인증서 발급을 위해 서버의 80, 443 포트와 올바른 DNS A 레코드가 필요하다.

## 현재 방식의 개선점

- 서버에서 직접 이미지를 빌드하므로 배포 중 자원을 사용하고 결과물 재현·롤백이 어렵다. CI에서 이미지를 빌드해 ECR에 push하고 EC2가 지정 digest를 pull하도록 개선할 수 있다.
- 배포 후 HTTP 상태 확인이 없다. 백엔드 health endpoint와 smoke test를 추가하는 것이 좋다.
- 자동 데이터베이스 백업과 복구 훈련, 이미지 태그 기반 롤백 절차가 필요하다.

