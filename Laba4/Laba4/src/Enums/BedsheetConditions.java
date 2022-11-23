package Enums;

public enum BedsheetConditions {
    ON_THE_BED("лежит на кровати."),
    NOT_ON_THE_BED("теперь не лежит на кровати."),
    ON_THE_ENTITY("теперь надета."),
    NOT_CUT("сейчас не имеет на себе дырок."),
    CUT("теперь имеет дырки в виде глаз."),
    NORMAL("теперь в обычном состоянии."),
    FLUTTERING("жутко развевается.");

    public String label;

    BedsheetConditions(String label) { this.label = label; }
}
