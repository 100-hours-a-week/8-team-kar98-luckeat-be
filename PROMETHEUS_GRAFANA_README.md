# LuckEat 모니터링 시스템 설정 가이드

LuckEat 백엔드 애플리케이션에 Prometheus와 Grafana를 연동하여 모니터링 시스템을 구축하는 방법을 안내합니다.

## 목차

1. [개요](#개요)
2. [주요 기능](#주요-기능)
3. [설정 방법](#설정-방법)
4. [커스텀 메트릭](#커스텀-메트릭)
5. [대시보드 설정](#대시보드-설정)
6. [알림 설정](#알림-설정)
7. [로그 모니터링](#로그-모니터링)
8. [MySQL 모니터링](#mysql-모니터링)

## 개요

Prometheus와 Grafana를 활용하여 LuckEat 백엔드 애플리케이션의 상태와 성능을 실시간으로 모니터링할 수 있습니다. 주요 메트릭과 알림을 설정하여 시스템의 안정성을 보장합니다.

## 주요 기능

- **실시간 모니터링**: 애플리케이션의 상태와 성능을 실시간으로 모니터링
- **커스텀 메트릭**: 비즈니스 로직에 맞는 커스텀 메트릭 수집 (예약, 리뷰, API 응답 시간 등)
- **시각화 대시보드**: Grafana를 통한 직관적인 데이터 시각화
- **알림 설정**: 시스템 문제 발생 시 알림 설정
- **로그 수집 및 분석**: Loki를 통한 애플리케이션 로그 중앙 집중화 및 분석
- **데이터베이스 모니터링**: MySQL Exporter를 통한 데이터베이스 성능 모니터링

## 설정 방법

### 1. 의존성 추가

`build.gradle` 파일에 다음 의존성을 추가합니다:

```gradle
dependencies {
    // ... 기존 의존성 ...

    // Prometheus & Micrometer 의존성 추가
    implementation 'io.micrometer:micrometer-registry-prometheus'
    implementation 'io.micrometer:micrometer-core'
}
```

### 2. 설정 파일 수정

`application.yml` 파일에 다음 설정을 추가합니다:

```yaml
management:
  endpoints:
    web:
      base-path: /api/actuator
      exposure:
        include: health,info,prometheus,metrics
  endpoint:
    health:
      show-details: always
    prometheus:
      enabled: true
  prometheus:
    metrics:
      export:
        enabled: true
  metrics:
    tags:
      application: ${spring.application.name}
```

### 3. Docker Compose 설정

프로젝트 루트 디렉토리에 `docker-compose-monitoring.yml` 파일을 생성합니다:

```yaml
version: "3.8"

services:
  prometheus:
    image: prom/prometheus:latest
    container_name: prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus_data:/prometheus
    command:
      - "--config.file=/etc/prometheus/prometheus.yml"
      - "--storage.tsdb.path=/prometheus"
      - "--web.console.libraries=/etc/prometheus/console_libraries"
      - "--web.console.templates=/etc/prometheus/consoles"
      - "--web.enable-lifecycle"
    restart: unless-stopped
    networks:
      - monitoring

  grafana:
    image: grafana/grafana:latest
    container_name: grafana
    ports:
      - "3001:3000"
    volumes:
      - grafana_data:/var/lib/grafana
      - ./grafana/provisioning:/etc/grafana/provisioning
      - ./grafana/dashboards:/etc/grafana/provisioning/dashboards/json
    environment:
      - GF_SECURITY_ADMIN_USER=admin
      - GF_SECURITY_ADMIN_PASSWORD=admin
      - GF_USERS_ALLOW_SIGN_UP=false
    restart: unless-stopped
    depends_on:
      - prometheus
      - loki
    networks:
      - monitoring

  loki:
    image: grafana/loki:latest
    container_name: loki
    ports:
      - "3100:3100"
    volumes:
      - ./loki-config.yml:/etc/loki/local-config.yaml
      - loki_data:/loki
    command: -config.file=/etc/loki/local-config.yaml
    restart: unless-stopped
    networks:
      - monitoring

  promtail:
    image: grafana/promtail:latest
    container_name: promtail
    volumes:
      - ./promtail-config.yml:/etc/promtail/config.yml
      - ./logs:/var/log/luckeat
    command: -config.file=/etc/promtail/config.yml
    restart: unless-stopped
    depends_on:
      - loki
    networks:
      - monitoring

  mysql-exporter:
    image: prom/mysqld-exporter:latest
    container_name: mysql-exporter
    ports:
      - "9104:9104"
    environment:
      - DATA_SOURCE_NAME=admin:password@(mysql-host:3306)/dbname
    restart: unless-stopped
    networks:
      - monitoring

volumes:
  prometheus_data:
  grafana_data:
  loki_data:

networks:
  monitoring:
    driver: bridge
```

### 4. Prometheus 설정 파일

프로젝트 루트 디렉토리에 `prometheus.yml` 파일을 생성합니다:

```yaml
global:
  scrape_interval: 15s
  scrape_timeout: 10s
  evaluation_interval: 15s

alerting:
  alertmanagers:
    - static_configs:
        - targets: []

scrape_configs:
  - job_name: "prometheus"
    static_configs:
      - targets: ["localhost:9090"]

  - job_name: "spring-boot"
    metrics_path: "/api/actuator/prometheus"
    scrape_interval: 5s
    static_configs:
      - targets: ["host.docker.internal:8080"]

  - job_name: "mysql"
    static_configs:
      - targets: ["mysql-exporter:9104"]
```

### 5. Loki 설정 파일

프로젝트 루트 디렉토리에 `loki-config.yml` 파일을 생성합니다:

```yaml
auth_enabled: false

server:
  http_listen_port: 3100

ingester:
  lifecycler:
    address: 127.0.0.1
    ring:
      kvstore:
        store: inmemory
      replication_factor: 1
    final_sleep: 0s
  chunk_idle_period: 5m
  chunk_retain_period: 30s

schema_config:
  configs:
    - from: 2020-10-24
      store: boltdb-shipper
      object_store: filesystem
      schema: v11
      index:
        prefix: index_
        period: 24h

storage_config:
  boltdb_shipper:
    active_index_directory: /loki/boltdb-shipper-active
    cache_location: /loki/boltdb-shipper-cache
    cache_ttl: 24h
    shared_store: filesystem
  filesystem:
    directory: /loki/chunks

limits_config:
  enforce_metric_name: false
  reject_old_samples: true
  reject_old_samples_max_age: 168h

chunk_store_config:
  max_look_back_period: 0s

table_manager:
  retention_deletes_enabled: false
  retention_period: 0s
```

### 6. Promtail 설정 파일

프로젝트 루트 디렉토리에 `promtail-config.yml` 파일을 생성합니다:

```yaml
server:
  http_listen_port: 9080
  grpc_listen_port: 0

positions:
  filename: /tmp/positions.yaml

clients:
  - url: http://loki:3100/loki/api/v1/push

scrape_configs:
  - job_name: system
    static_configs:
      - targets:
          - localhost
        labels:
          job: luckeat-logs
          __path__: /var/log/luckeat/*.log
```

### 7. Grafana 데이터 소스 설정

`grafana/provisioning/datasources/datasource.yml` 파일을 생성합니다:

```yaml
apiVersion: 1

datasources:
  - name: Prometheus
    type: prometheus
    access: proxy
    url: http://prometheus:9090
    isDefault: true
    editable: true

  - name: Loki
    type: loki
    access: proxy
    url: http://loki:3100
    editable: true
```

### 8. 시스템 실행

다음 명령어로 모니터링 시스템을 실행합니다:

```bash
# Spring Boot 애플리케이션 실행
./gradlew bootRun

# 로그 디렉토리 생성
mkdir -p logs

# 모니터링 시스템 실행 (다른 터미널에서)
docker-compose -f docker-compose-monitoring.yml up -d
```

## 커스텀 메트릭

LuckEat 애플리케이션은 다음과 같은 커스텀 메트릭을 수집합니다:

1. **예약 카운터**: 예약 건수를 추적하는 카운터 메트릭

   - 메트릭 이름: `luckeat_reservations_count_total`

2. **리뷰 카운터**: 리뷰 건수를 추적하는 카운터 메트릭

   - 메트릭 이름: `luckeat_reviews_count_total`

3. **API 요청 타이머**: API 요청 처리 시간을 측정하는 타이머 메트릭
   - 메트릭 이름: `luckeat_api_request_duration_seconds`

### 커스텀 메트릭 사용 예시

```java
// 컨트롤러나 서비스에서 메트릭 주입
@Autowired
private Counter reservationCounter;

// 메트릭 증가
reservationCounter.increment();
```

## 대시보드 설정

1. 웹 브라우저에서 Grafana에 접속합니다: http://localhost:3001
2. 기본 계정으로 로그인합니다 (admin/admin)
3. 대시보드 만들기 → 새 대시보드 → 패널 추가
4. 데이터 소스로 "Prometheus"를 선택하고 필요한 메트릭을 쿼리합니다.

### 추천 대시보드

1. JVM (메모리, GC, 스레드)
2. API 성능 (응답 시간, 요청 수)
3. 비즈니스 메트릭 (예약, 리뷰 등)
4. 시스템 리소스 (CPU, 메모리)
5. 로그 분석 (에러, 경고, 정보 로그)
6. MySQL 성능 (쿼리 수, 연결 수, 네트워크 트래픽)

## 알림 설정

Grafana에서 알림을 설정하는 방법:

1. 알림 → 알림 규칙 → 새 알림 규칙
2. 조건 설정 (예: 메모리 사용량이 80% 이상일 때)
3. 알림 채널 설정 (이메일, Slack 등)

### 추천 알림 조건

1. 메모리 사용량이 80% 이상일 때
2. API 응답 시간이 1초 이상일 때
3. 에러 로그가 일정 횟수 이상 발생할 때
4. MySQL 연결 수가 임계값을 초과할 때
5. 데이터베이스 쿼리 지연 시간이 0.5초 이상일 때

## 로그 모니터링

Loki와 Promtail을 사용하여 애플리케이션 로그를 중앙에서 수집하고 분석할 수 있습니다.

### 로그 설정 방법

1. Spring Boot 로깅 설정 (`application.yml`):

```yaml
logging:
  file:
    name: ./logs/luckeat.log
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  level:
    root: INFO
    org.springframework: WARN
    com.luckeat: DEBUG
```

2. 로그 대시보드 접근:
   - Grafana에서 "Explore" 메뉴 클릭
   - 데이터 소스로 "Loki" 선택
   - 로그 쿼리 작성 (예: `{job="luckeat-logs"}`)

### 로그 대시보드 기능

- **실시간 로그 스트리밍**: 애플리케이션 로그 실시간 조회
- **로그 필터링**: 로그 레벨, 서비스, 시간대 등으로 필터링
- **로그 패턴 분석**: 로그 패턴을 기반으로 문제 원인 분석
- **로그 통계**: 특정 에러나 경고 발생 빈도 분석

## MySQL 모니터링

MySQL Exporter를 사용하여 데이터베이스 성능을 모니터링할 수 있습니다.

### MySQL 모니터링 설정 방법

1. MySQL 연결 정보 설정:

   - `docker-compose-monitoring.yml`의 `mysql-exporter` 서비스 환경 변수 설정
   - 연결 문자열: `DATA_SOURCE_NAME=username:password@(hostname:port)/database`

2. MySQL 대시보드 접근:
   - Grafana에서 "Dashboards" 메뉴 클릭
   - "MySQL Overview" 대시보드 선택

### MySQL 대시보드 주요 지표

- **쿼리 초당 실행 수**: 초당 처리되는 쿼리 수
- **연결 수**: 활성 및 대기 상태의 연결 수
- **네트워크 트래픽**: 송수신 데이터 양
- **명령어 유형별 통계**: SELECT, INSERT, UPDATE, DELETE 등의 명령어 통계
- **디스크 사용량**: 테이블스페이스 및 임시 파일 사용량
- **버퍼 사용량**: InnoDB 버퍼 풀 사용량
- **락 통계**: 테이블 및 행 락 통계
