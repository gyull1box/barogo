## 1. 개요
- 과제 목표: JWT 기반 인증·주문 CRUD 구현
- REST 원칙 적용: 리소스 중심 URL 사용

## 2. URL 네이밍 관련
이번 실무 과제에서는 RESTful 원칙에 최대한 부합하도록 리소스 중심의 네이밍을 적용했습니다.

예를 들어 주문 조회는 /api/orders, 수정은 /api/orders/{id}/modify처럼 구성했습니다.

만약 팀에서는 searchOrder, modifyUser처럼 동사 기반 규칙을 사용한다면 
실제 협업에 참여하게 되었을때는 팀의 네이밍 컨벤션에 맞춰 유연하게 구조를 변경할 수 있습니다.

현재는 과제의 피드백을 받는 입장으로 RESTful 원칙을 기본으로 하여 설계한 점을 감안해주시면 감사하겠습니다.
> **협업 시** 팀의 네이밍 규칙을 우선 적용하며, 필요한 경우 구조 변경에 유연하게 대응할 계획입니다.

## 3. 미구현 기능 - Refresh Token 다중 세션
### 3-1. 설계
- **테이블**: `refresh_token`  
  - PK : `user_id + device_id`
  - Columns : `refresh_token`, `expires_at`, `ip_address`, `created_at`
- **플로우**
  1. 로그인 성공 → access·refresh 발급 & INSERT
  2. 기기별 로그인 → 별도 row 생성
  3. 재발급 요청 → DB 검증 후 새 access 발급
  4. 로그아웃/만료/강제 종료 → 해당 row DELETE

### 3-2. 미구현 사유
- 필수 요구가 아니라서 과제 기간 내 핵심 기능 완성을 우선하였습니다.

## 4. API 명세서
https://denim-healer-391.notion.site/22d685e68bd680c78135f47b18681509?v=22d685e68bd68094bcb7000ce6255730&source=copy_link

## 5. DDL Script 위치
src/main/resources/sql/*
