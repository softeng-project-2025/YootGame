package model.yut;

public enum YutResult {
    BACK_DO("빽도", -1),
    DO("도", 1),
    GAE("개", 2),
    GEOL("걸", 3),
    YUT("윷", 4),
    MO("모", 5);

    private final String name;
    private final int step;

    YutResult(String name, int step) {
        this.name = name;
        this.step = step;
    }

    public String getName() {
        return name;
    }

    public int getStep() {
        return step;
    }

    @Override
    public String toString() {
        return name;
    }
}