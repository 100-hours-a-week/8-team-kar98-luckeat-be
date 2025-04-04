# API Rate Limit 구현 가이드

## 개요

이 프로젝트는 DDoS 방어를 위해 Spring 기반 애플리케이션에 IP 주소 기반 API 요청 제한(Rate Limit)을 구현했습니다. 사용자의 IP 주소를 기반으로 시간당 요청 수를 제한하여 서비스 안정성을 높입니다.

## 기능

- **IP 주소 기반 식별**: IP 주소와 User-Agent를 조합한 고유 식별자를 통해 클라이언트 식별
- **유연한 시간 윈도우**: 구성 가능한 시간 윈도우(기본 1분) 동안 요청 제한
- **자동 정리**: Bucket4j와 Caffeine 캐시를 통한 자동 정리 및 메모리 최적화
- **환경별 설정**: 개발, 테스트, 프로덕션 환경에 따른 별도 설정
- **특정 엔드포인트 제한**: 로그인, 회원가입 등 중요 API에 대한 개별 제한

## 구현 아키텍처

### 핵심 컴포넌트

1. **RateLimitConfig**

   - Bucket4j 라이브러리를 사용한 토큰 버킷 알고리즘 구현
   - 토큰 버킷별 사용자 요청 제한 관리
   - Caffeine 캐시를 통한 메모리 최적화

2. **RateLimitFilter**

   - 모든 API 요청에 적용되는 서블릿 필터
   - IP 주소를 기반으로 요청자 식별
   - RateLimitConfig를 통한 버킷 처리

3. **IpAddressFilter**

   - 요청에서 IP 주소와 User-Agent를 조합하여 고유 식별자 생성
   - 생성된 식별자를 요청 속성으로 설정

4. **WebConfig**

   - 필터 등록 및 URL 패턴 지정
   - CORS 설정

5. **SecurityConfig**
   - 보안 설정 및 JWT 인증 설정

## 설정 가이드

### 기본 설정 (application.yml)

```yaml
rate-limit:
  default-limit: 60 # 분당 기본 요청 제한 (60개/분)
  window-minutes: 1 # 시간 윈도우 (1분)
  endpoints:
    - path: /api/v1/users/login
      limit: 10 # 로그인 요청은 10개/분으로 제한
    - path: /api/v1/users/register
      limit: 5 # 회원가입 요청은 5개/분으로 제한
```

### 개발 환경 설정 (application-development.yml)

```yaml
server:
  port: 8081 # 개발 환경에서는 8081 포트 사용

rate-limit:
  default-limit: 30 # 개발 시에는 테스트 용이성을 위해 30개/분으로 설정
```

### 프로덕션 환경 설정 (application-production.yml)

```yaml
server:
  port: ${PORT:8080} # 환경 변수 또는 기본값 8080 사용

rate-limit:
  default-limit: 60 # 프로덕션에서는 기본 제한 (60개/분)
  endpoints:
    - path: /api/v1/users/login
      limit: 5 # 보안을 위해 로그인 시도 제한
    - path: /api/v1/users/register
      limit: 3 # 회원가입은 3개/분으로 제한
```

## 실행 방법

### 환경별 실행

1. **개발 환경**:

   ```bash
   ./gradlew bootRun --args='--spring.profiles.active=development'
   ```

2. **프로덕션 환경**:
   ```bash
   ./gradlew bootRun --args='--spring.profiles.active=production'
   ```

### 클라이언트 연동 방법

클라이언트의 IP 주소는 자동으로 처리되므로 특별한 헤더 설정이 필요하지 않습니다:

```javascript
// JavaScript (브라우저) 예시
async function fetchAPI(url) {
  const response = await fetch(url, {
    headers: {
      "Content-Type": "application/json",
    },
  });

  // 응답 헤더에서 남은 요청 수 확인 가능
  const remainingRequests = response.headers.get("X-Rate-Limit-Remaining");

  return response.json();
}
```

### 테스트 방법

제공된 테스트 스크립트를 사용하여 Rate Limit 동작을 확인할 수 있습니다:

```bash
./test-rate-limit.sh
```

## 모니터링 및 관리

서버 로그에서 Rate Limit 관련 이벤트를 모니터링할 수 있습니다:

- Rate Limit 초과 이벤트
- 새로운 버킷 생성

## 문제 해결

1. **포트 충돌**: 8080 포트가 이미 사용 중인 경우 development 프로필로 실행
2. **메모리 사용량**: Caffeine 캐시 설정을 조정하여 메모리 사용량 최적화
3. **오류 처리**: Rate Limit 필터에서 예외 발생 시에도 요청 처리 진행
