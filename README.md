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

- **Use Case Diagram**
<img width="542" height="558" alt="스크린샷 2025-11-24 오후 10 20 51" src="https://github.com/user-attachments/assets/7c18cb89-c88f-4001-8c27-871e297d50c5" />

- **Activity Diagram**
<img width="607" height="1063" alt="스크린샷 2025-11-24 오후 10 19 50" src="https://github.com/user-attachments/assets/47b8b6a0-6e75-4651-b1bd-117cc590116f" />


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

## 5. 구현 스크린샷

1. 로그인/회원가입
<img width="1246" height="827" alt="image" src="https://github.com/user-attachments/assets/630ae18a-35c0-490b-8c1b-38e5b8f2b281" />
이메일과 비밀번호를 이용한 간편 회원가입 및 로그인 기능을 제공합니다. 세션 기반 인증을 통해 로그인된 사용자만 대국 및 전적 조회가 가능합니다.<br>
<br>
2. 로비
<img width="1290" height="1195" alt="image" src="https://github.com/user-attachments/assets/4a936a08-ef38-4ed8-86b4-78dc5211c50d" />
게임의 대기실 역할을 하는 로비 화면입니다. 상단에서는 현재 대기 중이거나 진행 중인 방 목록을 실시간으로 확인할 수 있으며, 하단에서는 본인의 지난 대국 기록(승/패 결과 및 기보)을 조회할 수 있습니다.<br>
<br>
3. 장기 방 입장
<img width="1290" height="1195" alt="image" src="https://github.com/user-attachments/assets/7f45c4b9-e77a-4edb-915c-b9939cb0a7a6" />
대기 상태(Waiting)인 방에 '참가하기' 버튼을 눌러 입장할 수 있습니다. 호스트(Host)와 게스트(Guest)가 모두 입장하면 게임 상태가 진행 중(In Progress)으로 변경되며 대국이 시작됩니다.<br>
<br>
- 대국 진행
<img width="1290" height="1195" alt="image" src="https://github.com/user-attachments/assets/a1f94e69-d68c-4747-985a-88f2102d42ce" />
상대방의 수로 인해 왕이 위협받는 '장군' 상태가 되면, 왕 기물에 붉은색 점멸 효과와 함께 경고 알림이 표시됩니다. 이때 시스템은 왕이 잡히게 되는 수(자살수)를 원천적으로 차단하여, 사용자가 반드시 장군을 멍군하도록 유도합니다.
<br>
<img width="1290" height="1195" alt="image" src="https://github.com/user-attachments/assets/64e670c1-3363-4d42-8e9b-c0f17901fafc" />
장군 상태에서 사용자가 더 이상 방어할 수 있는 수가 없다면 '외통수'로 판정되어 게임이 즉시 종료됩니다. 종료 시 승리한 플레이어 정보와 총 진행 수(Move Count)가 포함된 결과 화면이 나타납니다.<br>
<br>
4. 복기
<img width="1290" height="1195" alt="image" src="https://github.com/user-attachments/assets/4ee33248-cf5f-49ac-aa3f-091015143057" />
대국이 끝나고 로비로 돌아가면, 이전 대국의 기록이 저장되고 로비에 보여집니다.
<br>
<img width="1290" height="1195" alt="스크린샷 2025-11-24 오후 10 49 28" src="https://github.com/user-attachments/assets/971c90ae-0894-4db9-8f07-d588272ec7ec" />
종료된 대국은 자동으로 저장되어 로비의 전적 리스트에 반영됩니다. '복기 하기' 기능을 통해 당시의 대국 내용을 처음부터 끝까지 한 수씩 되돌려보며 경기 내용을 분석할 수 있습니다.<br>
<br>


## 6. 프로젝트 사용 메뉴얼 (Installation & Run)

이 프로젝트를 로컬 환경에서 실행하기 위한 단계입니다.

### 1️⃣ 프로젝트 클론 (Clone)

터미널(Git Bash, Cmd 등)을 열고 프로젝트를 다운로드합니다.

```bash
git clone https://github.com/milk-stone/spring-janggi.git
cd spring-janggi 
```

### 2️⃣ 환경 변수 설정 (`dev.env`)

데이터베이스 접속 정보와 같은 민감한 정보는 Git에 포함되지 않도록 환경 변수로 관리합니다.
프로젝트 루트 경로에 있는 dev.env.template 파일을 복사하여 dev.env 파일을 생성하고, 본인의 로컬 MySQL 설정에 맞게 수정해 주세요.
1. `dev.env.template` 파일 복사 -> `dev.env`로 이름 변경
2. `dev.env` 파일 내용 수정 (아래 예시 참고)

```txt
DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver
RDS_HOST=localhost
RDS_PORT=3306

# DB 이름 뒤에 파라미터는 그대로 유지해 주세요.
# { } 부분만 본인의 설정으로 변경하면 됩니다.
RDS_DB_NAME={데이터베이스_이름}}?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Seoul

RDS_USERNAME={MySQL_사용자_ID}
RDS_PASSWORD={MySQL_사용자_비밀번호}
```

### 3️⃣ 프로젝트 빌드 및 실행

환경 변수 설정을 완료하였다면, 프로젝트 루트 경로에서 다음 명령어를 순서대로 입력하세요.

- **방법 A: Gradle로 바로 실행하기** : 가장 간편하게 서버를 띄울 수 있습니다.

```bash
# Windows
./gradlew.bat bootRun

# Mac/Linux
./gradlew bootRun
```

- **방법 B: Jar 파일 빌드 후 실행하기** : 실행 가능한 Jar 파일을 만든 후 실행합니다.

```bash
# 1. 빌드 (테스트 포함)
./gradlew build

# 2. 실행 (build/libs 폴더 안에 생성된 jar 파일 실행)
# 파일명은 버전 설정에 따라 다를 수 있습니다.
java -jar build/libs/spring-janggi-0.0.1-SNAPSHOT.jar
```

### 4️⃣ 접속 확인

서버가 정상적으로 실행되었다면, 브라우저를 열고 아래 주소로 접속합니다.

- **URL**: http://localhost:8080

## 7. 사용자 이용 흐름 (User Flow)

본 장기 게임은 **실시간 1:1 대전** 게임이므로, 원활한 테스트를 위해 **두 개의 브라우저**가 필요합니다.

### 1️⃣ 접속 및 회원가입
1. **[브라우저 A]** 를 열고 `http://localhost:8080`에 접속합니다.
2. 로그인 화면 하단의 **'회원가입'**을 통해 계정을 생성합니다. (예: `user1`)
3. 생성한 계정으로 **로그인**하여 대기실(Lobby)로 입장합니다.

### 2️⃣ 대국 상대 접속
같은 브라우저의 새 탭은 세션이 공유되므로 로그인이 유지됩니다. **반드시 다른 환경**으로 접속해 주세요.
1. **[브라우저 B]** (크롬 시크릿 모드, 엣지, 웨일 등)를 엽니다.
2. `http://localhost:8080`에 접속하여 **다른 계정**으로 회원가입 및 로그인합니다. (예: `user2`)

### 3️⃣ 방 만들기 (Host)
1. **[브라우저 A]** 의 대기실 상단에서 **방 제목**을 입력합니다. (예: "한 수 배우겠습니다")
2. **[방 만들기]** 버튼을 클릭합니다.
3. `user1`은 **초나라(초록색)** 플레이어가 되어 게임방에 입장하고 대기합니다.

### 4️⃣ 방 참가하기 (Guest)
1. **[브라우저 B]** 에서 새로고침을 하거나 잠시 기다리면 방 목록에 방금 생성된 방이 나타납니다.
2. **[참가하기]** 버튼을 클릭합니다.
3. `user2`는 **한나라(빨간색)** 플레이어가 되어 게임방에 입장합니다.
4. 두 플레이어가 모두 입장하여 게임 상태가 `IN_PROGRESS`로 변경되고 대국이 시작됩니다.

### 5️⃣ 장기 대국 진행
* **초나라(`user1`)** 가 먼저 둡니다.
* 자신의 턴에 알맞은 기물을 선택하면 **이동 가능한 위치에 초록색 점**이 표시됩니다.
* 상대방이 수를 두면 화면이 자동으로 갱신됩니다. (AJAX Polling)
* **장군(Check)** 상태가 되면 왕이 붉게 빛나며 알림이 뜹니다.

### 6️⃣ 게임 종료 및 복기
* **외통수(Checkmate)**가 발생하면 승리 메시지와 함께 게임이 종료됩니다.
* 종료 모달창에서 **[복기 하기]**를 누르면, 방금 둔 대국을 처음부터 다시 돌려볼 수 있습니다.
* **[로비로 나가기]** 를 눌러 대기실로 돌아가면, 하단 **'내 지난 대국'** 섹션에 전적과 함께 기록이 남습니다.

## 8. 느낀 점

