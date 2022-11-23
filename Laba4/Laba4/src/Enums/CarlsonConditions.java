package Enums;

public enum CarlsonConditions {
    NORMAL("спокойно сидит на подоконнике."),
    FLYING("начинает летать по комнате!"),
    NEAR_THE_BED("теперь находится около кровати"),
    IN_ROOM("находится в комнате"),
    WRAPPED("замотался в простыню так, что видны только его маленькие пухлые ручки."),
    UNWRAPPED("размотался и перестал выглядеть жутко.");

    public String label;
    CarlsonConditions(String label) {
        this.label = label;
    }
}
