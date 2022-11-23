package Enums;

public enum TablePos {
    IN_ROOM("находится в комнате."),
    NOT_IN_ROOM("не находится в комнате.");

    public String label;
    TablePos(String label) {
        this.label = label;
    }
}
