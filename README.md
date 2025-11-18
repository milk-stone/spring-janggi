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

- [ ] `Position.java` (좌표 `A1`, `B2` 등을 표현하는 객체)
- [ ] `PieceType.java` (차, 포, 마, 상 등 말의 종류 Enum)
- [ ] `Team.java` (초, 한 Enum)
- [ ] `Piece.java` (말 객체, `PieceType`과 `Team` 보유)
- [ ] `Board.java` (게임판 로직)
    - [ ] 2차원 배열 또는 `Map`으로 말의 배치 관리
    - [ ] `move(from, to)`: 말 이동 메서드
    - [ ] `calculateMovablePositions(position)`: 특정 말의 이동 가능 위치 계산
    - [ ] `isCheckmate(team)`: 외통수(체크메이트) 판단 로직
    - [ ] `isCheck(team)`: '장군' 판단 로직
- [ ] `Board` 상태를 `JSON`으로 직렬화/역직렬화하는 유틸리티 작성

### 3. 엔티티 및 리포지토리 (`domain.entity`, `domain.repository`)

- [x] `Member` 엔티티 작성
    - [x] `validate()` 메서드를 통한 유효성 검증 로직 추가
- [ ] `Game` 엔티티 작성
    - [ ] `boardState` (TEXT/JSON): 현재 게임판 상태 스냅샷
    - [ ] `currentTurn` (Enum: CHO, HAN)
    - [ ] `status` (Enum: WAITING, IN_PROGRESS, FINISHED)
- [ ] `MoveHistory` 엔티티 작성 (기보 저장)
- [ ] `MemberRepository` 인터페이스 작성
- [ ] `GameRepository` 인터페이스 작성
- [ ] `MoveHistoryRepository` 인터페이스 작성

### 4. 서비스 및 SSR 웹 구현 (`service`, `controller`)

- [ ] `JanggiService` 작성
    - [ ] `createGame()`: 새 게임 생성
    - [ ] `loadGame(gameId)`: DB에서 `Game`을 로드하고 `Board` 객체로 변환
    - [ ] `movePiece(gameId, from, to)`:
        - [ ] `Game` 로드 및 `Board` 객체 생성
        - [ ] `Board` 로직으로 이동 유효성 검사
        - [ ] `Board.move()` 실행
        - [ ] `Board` 상태를 `Game` 엔티티(`boardState`)에 업데이트 (JSON)
        - [ ] `MoveHistory`에 이동 기록 `save()`
- [ ] `JanggiController` 작성
    - [ ] `GET /game/{gameId}`: 게임 방 뷰 렌더링 (Thymeleaf)
    - [ ] `POST /game/{gameId}/move`: 말 이동 요청 처리
        - [ ] `JanggiService.movePiece()` 호출
        - [ ] 처리 후 `redirect:/game/{gameId}` (페이지 새로고침)
- [ ] Thymeleaf 뷰 템플릿 작성
    - [ ] `game.html`
    - [ ] `Board` 객체를 받아 장기판을 `<table>` 등으로 동적 렌더링
    - [ ] 말 이동을 위한 간단한 폼(Form) 또는 링크(GET) 구현

### 5. 사용자 및 1:1 대결 (고도화)

- [ ] (TBD) 회원 가입 및 로그인 기능
- [ ] (TBD) `Game` 엔티티에 `choPlayer`, `hanPlayer` (Member) 매핑
- [ ] (TBD) 대기방 및 1:1 매칭 로직 구현

### 6. 실시간성 개선 (고도화)

---

## 5. 프로젝트 사용 메뉴얼

## 6. 느낀 점
