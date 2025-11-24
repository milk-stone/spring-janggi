# 🚀 스프링 부트로 구현하는 장기 게임

> 부문: "고난도 문제 해커톤"

## 1. 서론 (Introduction)

평소 장기 게임을 좋아하며, 장기라는 게임을 통해 논리적인 사고력을 기를 수 있었습니다. 혼자 수읽기를 해보거나, 대국 후 복기를 통해 패인을 찾아보는 과정은 깊이 있는 사고를 가능하게 했습니다.

이러한 장기의 매력을 웹 환경에서 더 많은 사람이 간단하게 즐길 수 있도록, Spring Boot를 활용한 서버 사이드 렌더링 기반의 장기 게임 프로그램을 만들고자 합니다.

---

## 2. 시스템 설계 (System Design)

### 2.1. 아키텍처

본 프로젝트는 **계층형 아키텍처(Layered Architecture)**를 기반으로 합니다.

* **Controller:** 웹 요청(Request) 및 응답(Response)을 처리하며, Thymeleaf를 사용한 SSR을 담당합니다.
* **Service:** 비즈니스 로직을 수행합니다.
* **Domain:**
    * **domain.core (POJO):** 장기 게임의 모든 규칙과 상태를 관리하는 순수 Java 객체 (게임 엔진) 영역입니다.
    * **domain.entity (JPA):** 데이터베이스에 저장될 영속성 객체 영역입니다.
    * **domain.repository (JPA):** 데이터베이스에 접근하는 JPA Repository 인터페이스 영역입니다.

### 2.2. ERD (Entity-Relationship Diagram)

* **Member:** 사용자 정보
* **Game:** 하나의 대국 정보
* **MoveHistory:** 특정 대국의 기보(이동 내역)

<img width="619" height="410" alt="image" src="https://github.com/user-attachments/assets/7b6e7ea1-4037-4696-8a03-83cf9eb9cad9" />

### 2.3. Use Case / Activity Diagram

> (TBD)

---

## 3. 기술 스택 (Tech Stack)

* **Backend:** Java 21, Spring Boot
* **View:** Thymeleaf (Server-Side Rendering)
* **Database:** MySQL
* **ORM:** Spring Data JPA
* **Build:** Gradle
* **Others:** Lombok

---

## 4. 구현할 기능 목록 (To-Do List)

### 1. 프로젝트 초기 설정

- [x] Spring Boot 프로젝트 생성 (Spring Initializr)
- [x] `build.gradle` 의존성 설정 (Web, JPA, MySQL, Thymeleaf, Lombok)
- [x] `application.yaml` 설정 (MySQL, JPA, Thymeleaf)
- [x] Layered Architecture 기반 패키지 구조 정립

### 2. 핵심 도메인 로직 (`domain.core`)

- [x] `Position.java` (좌표 `A1`, `B2` 등을 표현하는 객체)
- [x] `PieceType.java` (차, 포, 마, 상 등 말의 종류 Enum)
- [x] `Team.java` (초, 한 Enum)
- [x] `Piece.java` (말 객체, `PieceType`과 `Team` 보유)
- [x] `Board.java` (게임판 로직)
    - [x] 2차원 배열 또는 `Map`으로 말의 배치 관리
    - [x] `move(from, to)`: 말 이동 메서드
    - [x] `calculateMovablePositions(position)`: 특정 말의 이동 가능 위치 계산
    - [x] `isCheckmate(team)`: 외통수(체크메이트) 판단 로직
    - [x] `isCheck(team)`: '장군' 판단 로직
- [x] `BoardMapper.java` : `Board` 상태를 `JSON`으로 직렬화/역직렬화하는 유틸리티 작성

### 3. 엔티티 및 리포지토리 (`domain.entity`, `domain.repository`)

- [x] `Member` 엔티티 작성
    - [x] `validate()` 메서드를 통한 유효성 검증 로직 추가
- [x] `Game` 엔티티 작성
    - [x] `boardState` (TEXT/JSON): 현재 게임판 상태 스냅샷
    - [x] `currentTurn` (Enum: CHO, HAN)
    - [x] `status` (Enum: WAITING, IN_PROGRESS, FINISHED)
- [x] `MoveHistory` 엔티티 작성 (기보 저장)
- [x] `MemberRepository` 인터페이스 작성
- [x] `GameRepository` 인터페이스 작성
- [x] `MoveHistoryRepository` 인터페이스 작성

### 4. 핵심 게임 엔진 및 웹 서비스 구현 (`Core Logic`, `Basic Web`)

- [x] **장기 도메인 로직 구현 (Domain)**
    - [x] **게임 상태 관리**: `Board` 객체를 통한 기물 배치 및 상태 관리, JSON 직렬화를 통한 DB 저장/복원.
    - [x] **이동 유효성 검증 (Move Validation)**:
        - 기물별 행마법 완벽 구현 (마/상의 멱, 포의 다리 조건, 궁성 라인 이동 등).
        - **장군(Check)** 감지 및 **자살수(Suicide Move)** 방지 알고리즘 적용.
        - **외통수(Checkmate)** 판단 및 게임 종료 처리.
    - [x] **기보 관리**: 이동 경로(`MoveHistory`) 저장 및 턴(`Turn`) 관리.

- [x] **웹 요청 처리 및 뷰 (Controller, View)**
    - [x] **SSR 기반 렌더링**: Thymeleaf를 활용한 초기 장기판 및 게임 정보(점수, 턴) 렌더링.
    - [x] **API 기반 이동 처리**:
        - `REST API`를 통해 기물 이동 요청을 비동기적으로 처리.
        - 이동 실패 시 예외 메시지를 JSON으로 응답하여 `Alert` 처리.
    - [x] **CSS Grid 장기판**: `div`와 `CSS Grid`를 활용하여 반응형 장기판 및 궁성 대각선 구현.

### 5. 사용자 시스템 및 멀티플레이 고도화 (`User`, `Multiplayer`, `Advanced UX`)

- [x] **사용자 인증 시스템 (User Auth)**
    - [x] **회원 관리**: 이메일/비밀번호 기반의 회원가입 및 중복 검증 로직.
    - [x] **세션 로그인**: `HttpSession`을 활용한 로그인 상태 유지 및 사용자 식별.

- [x] **로비 및 매칭 시스템 (Lobby)**
    - [x] **대기실(Lobby)**: 생성된 게임 방 목록 조회 및 입장 가능한 방 표시.
    - [x] **방 만들기/참가**: 로그인한 사용자가 방장(Host)이 되어 방을 생성하거나, 도전자(Guest)로 참가.
    - [x] **플레이어 매칭**: `Game` 엔티티와 `Member` 엔티티를 연동하여 초(Cho)/한(Han) 플레이어 배정.

- [x] **게임 수행 시 권한 검증**
    - [x] **턴 & 소유권 검증**: 현재 턴의 플레이어인지, 본인의 기물을 조작하는지 서버 측 검증 강화.
    - [x] **관전자 모드**: 게임에 참가하지 않은 사용자는 기물 조작을 차단하고 관전만 가능하도록 처리.

- [x] **실시간성 및 UX 고도화 (Real-time & UX)**
    - [x] **실시간 동기화 (Polling)**: `AJAX Polling` 기법을 도입하여 상대방의 수를 실시간으로 화면에 반영.
    - [x] **시각적 피드백 강화**:
        - **이동 가이드(Guide)**: 기물 선택 시 이동 가능한 위치를 마커(Dot)로 표시.
        - **장군 알림(Effect)**: 장군 상태 시 왕(Gung) 기물에 붉은 점멸 애니메이션 및 경고창 출력.
        - **기물 디자인**: 텍스트 대신 한자(漢字) 적용 및 계급별 크기 차등화로 리얼리티 향상.

### 6. 추가 고도화

- [x] **대국 복기 시스템 (Replay)**
    - [x] **기보 저장**: 대국 중 발생한 모든 수를 DB에 기록.
    - [x] **복기 뷰어**: 종료된 대국을 한 수씩 다시 보는 기능 (이전/다음).
    - [x] **전적 조회**: 로비에서 나의 지난 대국 기록 열람.

- [x] **단위 테스트 (Unit Test)**
    - [x] **행마법 검증**: 마/상(멱), 포(다리) 등 복잡한 이동 규칙 테스트.
    - [x] **승패 로직 검증**: 장군, 멍군, 자살수 방지 및 외통수 판단 로직 테스트.

---

## 5. 프로젝트 사용 메뉴얼

## 6. 느낀 점
