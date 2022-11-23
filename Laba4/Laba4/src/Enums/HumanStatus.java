package Enums;

public enum HumanStatus {
    NORMAL("теперь в спокойном состоянии."),
    CONSTERNATION("знают, что это Карлсон, но им всё равно жутко.");

    public String label;
    HumanStatus(String label) {this.label = label;}
}
