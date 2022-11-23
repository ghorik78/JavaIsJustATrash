package Enums;

public enum BoxStatus {
    OPENED("сейчат закрыт."),
    NOT_OPENED("закрыт.");

    public String label;

    BoxStatus(String label) {
        this.label = label;
    }
}
