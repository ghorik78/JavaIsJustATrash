package Enums;

public enum MotorStatus {
    TURNED_ON("завёлся и начал тарахтеть."),
    TURNED_OFF("выключился и перестал тарахтеть."),
    MAKE_LESS_NOISE("теперь не создаёт никакого шума.");

    public String label;
    MotorStatus(String label) {
        this.label = label;
    }
}
