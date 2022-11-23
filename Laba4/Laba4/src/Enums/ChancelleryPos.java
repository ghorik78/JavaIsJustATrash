package Enums;

public enum ChancelleryPos {
    IN_BOX("сейчас находятся в ящике."),
    NOT_IN_BOX("вытащили из ящика.");

    public String label;
    ChancelleryPos(String label) {
        this.label = label;
    }
}
