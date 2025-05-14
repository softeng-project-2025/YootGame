# YootGame (윷놀이 게임 프로젝트)

**YootGame**은 객체지향 설계 원칙과 MVC 아키텍처를 기반으로 Java로 구현된 윷놀이 게임입니다.  
이 프로젝트는 중앙대학교 SE 2025 봄학기 텀프로젝트로 진행됩니다.

## 프로젝트 목표
- Java 기반 윷놀이 게임 구현 (팀 기반 협업 및 GitHub 기록 중심)
- MVC 아키텍처 설계
- Swing / JavaFX UI 교체 가능 구조
- 다양한 윷판 커스터마이징 기능 (사각형, 오각형, 육각형)
- JUnit 기반 테스트 지원

## 디렉토리 구조
<pre><code>
YootGame/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── app/                 # Main 실행 클래스
│   │   │   ├── controller/          # GameController
│   │   │   ├── model/
│   │   │   │   ├── dto/              # MoveResult, NextStateHint, MessageType 등
│   │   │   │   ├── service/          # GameService, StateTransitioner, StateFactory
│   │   │   │   ├── states/           # WaitingForThrowState, SelectingPieceState, GameOverState 등
│   │   │   │   ├── board/            # Board
│   │   │   │   ├── piece/            # Piece
│   │   │   │   ├── player/           # Player
│   │   │   │   ├── position/         # Position
│   │   │   │   ├── yut/              # YutResult, YutThrower
│   │   │   │   ├── strategy/         # SquarePathStrategy, PentagonPathStrategy, HexPathStrategy
│   │   │   │   ├── turn/             # TurnResult
│   │   │   │   ├── yut/              # YutResult, YutThrower
│   │   │   │   └── manager/          # PieceUtil, GroupManager, CaptureManager
│   │   │   └── view/                 # SwingView, DrawBoard, View 인터페이스
│   │   └── resources/
│   └── test/
│       ├── java/                    # JUnit 테스트 케이스
│       └── resources/
├── docs/                            # 설계 문서, 다이어그램 등
├── pom.xml                          # Maven 설정
├── LICENSE                          # MIT License
└── README.md                        # 프로젝트 소개 (이 파일)
</code></pre>

## 실행 방법

### 개발 환경
- Java JDK 17
- Maven 3.8+
- IntelliJ IDEA 2022 이상 권장

### 실행 명령어

1. 본 프로젝트는 Maven 기반입니다. 클론 후 아래 순서로 설정하세요:

```bash
git clone https://github.com/softeng-project-2025/YootGame.git
cd YootGame
```

2.	IntelliJ에서 pom.xml을 열고, 오른쪽 상단 Maven 탭에서 동기화 버튼을 클릭하세요. 
3. JDK 17이 설치돼 있어야 합니다. 없다면 설치 후 JAVA_HOME을 설정하거나 IntelliJ에서 수동 지정하세요.
4. 실행 방법 :
```bash
# 프로젝트 컴파일
mvn compile

# 실행
mvn exec:java
```

## 주요기능

- 사용자 수 / 말 개수 / 윷판 형태 설정 가능 
- 랜덤 윷 던지기 및 지정 던지기 지원 
- 말 이동, 겹침, 잡기, 업기 처리 
- 상태 패턴(State) 기반 턴 전환 로직 (StateFactory 이용)
- 게임 종료 및 승자 출력 기능 
- Swing 기반 UI와 JavaFX UI 교체 구조 준비

## 적용 패턴

- MVC: 모델-뷰-컨트롤러 분리 
- Strategy: 윷판 경로 전략 교체 (Square, Pentagon, Hexagon)
- State: 게임 상태 전환 관리 (WaitingForThrowState, SelectingPieceState, GameOverState)
- Factory: 상태 전이용 StateFactory 구현 완료
- Observer (예정): 모델-뷰 자동 동기화



## 팀원

- 지민: 게임 로직, 모델, 보드 전략 
- 도엽: Swing UI 및 버튼 이벤트 처리
- 범준: JavaFX UI, 테스트
- 희수: 다이어그램 작성 및 문서
- 나영: 다이어그램 작성 및 문서


---

## LICENSE

MIT License 내용 생략 (LICENSE 파일에 별도 포함)