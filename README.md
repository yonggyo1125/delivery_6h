# 🚀 Delivery Service Platform: Backend Project

> **확장성과 안정성을 동시에 고려한 CQRS 및 이벤트 기반 배달 플랫폼 백엔드 시스템**

본 프로젝트는 대규모 트래픽 환경에서도 안정적으로 동작할 수 있는 배달 서비스의 핵심 로직(주문 상태 관리, 권한 검증, 실시간 평점 집계 등)을 구현한 백엔드 플랫폼입니다. **레이어드 아키텍처**를 기반으로 **CQRS**와 <b>이벤트 기반 아키텍처(EDA)</b>를 도입하여 시스템 간 결합도를 낮추고 유지보수성을 극대화했습니다.

---

## 🛠 Tech Stack

- **Language & Framework**: Java 25, Spring Boot 3.x
- **Persistence**: Spring Data JPA, **QueryDSL** (동적 쿼리 및 성능 최적화)
- **Database**: PostgreSQL (pgrouting), Redis
- **Security**: Spring Security, **Keycloak** (OIDC 기반 인증/인가)
- **Reliability**: Spring Retry, Spring Async
- **Documentation**: Swagger (OpenAPI 3.0)
- **Infrastructure**: Docker, GitHub Actions (CI/CD)

---

## 🏗 System Architecture

시스템은 명령(Command)과 조회(Query)의 책임을 분리하는 **CQRS 패턴**을 적용하여 구현되었습니다.

- **Presentation Layer**: RESTful API 설계 및 전용 Request/Response DTO를 통해 계층 간 의존성을 완전히 분리.
- **Application Layer**: 비즈니스 흐름 제어 및 응용 로직 수행, 서비스 간 결합도 완화를 위한 서비스 DTO 활용.
- **Domain Layer**: 핵심 비즈니스 규칙이 응집된 Rich Domain Model 지향 및 VO(Value Object) 적극 활용.
- **Infrastructure Layer**: 외부 API 연동(Toss Payment, Kakao Local), QueryDSL을 활용한 복잡한 데이터 조회 로직 구현.

---

## 🔥 Key Technical Implementation

### 1. CQRS 기반의 고성능 리뷰 시스템
리뷰 도메인의 복잡한 검색 요구사항과 대량 조회를 처리하기 위해 명령과 조회를 분리했습니다.
- **동적 검색 최적화**: QueryDSL을 도입하여 제목, 내용, 작성자 등 다양한 조건의 동적 검색을 0.1초 내외로 처리하도록 최적화.
- **페이징 성능 개선**: `PageableExecutionUtils`를 적용하여 데이터가 적거나 마지막 페이지일 경우 불필요한 **Count 쿼리를 생략**하여 DB 부하 경감.
- **Soft Delete 정합성**: Repository 레벨에서 삭제되지 않은 데이터만 조회하도록 필터링을 강제하여 비즈니스 데이터 무결성 확보.

### 2. 이벤트 기반 상점 평점 자동 갱신 (EDA)
사용자 경험을 해치지 않으면서 데이터의 최종 일관성을 보장하는 이벤트 핸들링 구조를 구축했습니다.
- **비동기 처리**: 리뷰 작성/수정/삭제 시 메인 트랜잭션과 별도로 평점 업데이트 로직을 `@Async`로 분리하여 응답 속도 최적화.
- **트랜잭션 분리**: `@TransactionalEventListener(phase = AFTER_COMMIT)`와 `Propagation.REQUIRES_NEW`를 조합하여 리뷰 저장 성공 시에만 평점 업데이트가 수행되도록 안전하게 격리.
- **장애 복구 로직**: `@Retryable`과 `Exponential Backoff`를 적용하여 일시적인 장애 시 자동 재시도하며, 최종 실패 시 `@Recover`를 통한 로그 기록 및 관리자 알림 구성.

### 3. 데이터 접근 최적화 및 유니크 제약
- **복합 인덱스 설계**: `Order` 엔티티에 사용자/매장별 최신순 조회를 위한 인덱스(`(store_id, status, created_at desc)`)를 적용하여 대량 데이터 상황에서의 조회 성능 방어.
- **데이터 무결성 강제**: 한 주문당 하나의 리뷰만 작성 가능하도록 DB 레벨에서 `(order_id, deleted_at)` 복합 유니크 인덱스를 설정하여 동시성 이슈 원천 차단.

### 4. 비동기 보안 컨텍스트 전파
- **문제 해결**: 별도 스레드에서 동작하는 `@Async` 로직에서도 현재 사용자의 권한 정보를 활용할 수 있도록 `DelegatingSecurityContextAsyncTaskExecutor`를 설정하여 비동기 환경에서의 보안성 유지.

---

## 📊 Data Modeling & Indexing

| 테이블명 | 주요 인덱스 및 제약 조건 | 목적 |
| :--- | :--- | :--- |
| **P_ORDER** | `orderer_id, created_at desc` | 사용자 주문 내역 최신순 조회 최적화 |
| **P_ORDER** | `store_id, status, created_at desc` | 점주용 주문 관리 페이지 조회 성능 개선 |
| **P_REVIEW** | `order_id, deleted_at` (Unique) | 1주문 1리뷰 정책 강제 및 Soft Delete 대응 |

---

## 📖 API Documentation (V1)

모든 API 스펙은 Swagger를 통해 자동 문서화되며, Bean Validation을 통한 요청 데이터 검증을 지원합니다.

- **Base Endpoint**: `/v1/reviews`
- **Auth**: Keycloak Bearer Token (JWT)

---

## 🚀 Getting Started

### Prerequisites
- Docker & Docker Compose
- JDK 25
- Gradle

### Installation & Run
1. 저장소 클론: `git clone https://github.com/yonggyo1125/delivery_6h.git`
2. 인프라 실행: `docker-compose up -d`
3. 애플리케이션 실행: `./gradlew bootRun`