package model.yut;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// 윷 결과를 나타내는 열거형입니다.
// 한글 이름(name)과 이동 칸 수(step)를 관리하며,
// name 기반 조회와 UI 편의 메서드를 제공합니다.
public enum YutResult {
    BACK_DO("빽도", -1),
    DO("도", 1),
    GAE("개", 2),
    GEOL("걸", 3),
    YUT("윷", 4),
    MO("모", 5);

    private final String name;
    private final int step;

    private static final Map<String, YutResult> NAME_MAP;
    static {
        // name을 소문자로 변환하여 대소문자 구분 없이 매칭
        NAME_MAP = Stream.of(values())
                .collect(Collectors.toUnmodifiableMap(
                        r -> r.name.toLowerCase(),
                        r -> r
                ));
    }

    YutResult(String name, int step) {
        this.name = name;
        this.step = step;
    }

    // 한글 이름
    public String getName() {
        return name;
    }

    // 이동 칸 수
    public int getStep() {
        return step;
    }

    @Override
    public String toString() {
        return name;
    }

    // 한글 이름으로부터 YutResult 열거형 반환 (대소문자 구분 없이)
    public static YutResult fromName(String name) {
        if (name == null) throw new IllegalArgumentException("Name must not be null");
        YutResult result = NAME_MAP.get(name.toLowerCase());
        if (result == null) {
            throw new IllegalArgumentException("Invalid 윷 result: " + name);
        }
        return result;
    }

    // UI 콤보박스 등에 사용할 한글 이름 배열 반환
    public static String[] getNames() {
        return Stream.of(values())
                .map(YutResult::getName)
                .toArray(String[]::new);
    }
}