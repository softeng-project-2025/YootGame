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
├── src/                # Java 소스코드
│   ├── model/          # 게임 로직, 말, 보드, 상태 등
│   ├── <view>          # Swing 및 JavaFX UI
│   ├── <controller>    # 사용자 입력 및 게임 흐름 제어
│   └── app.Main.java       # 게임 실행 시작점
├── test/               # JUnit 테스트 코드
├── docs/               # 요구사항, 다이어그램 등
├── README.md
├── .gitignore
└── LICENSE
</code></pre>

## 실행 방법
```bash
javac src/app.Main.java
java app.Main
```

## 팀원

- 지민: 게임 로직, 모델, 보드 전략 
- 도엽: Swing UI 및 버튼 이벤트 처리
- 범준: JavaFX UI, 테스트
- 희수: 다이어그램 작성 및 문서
- 나영: 다이어그램 작성 및 문서


---

## LICENSE

MIT License 내용 생략 (LICENSE 파일에 별도 포함)