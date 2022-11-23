package Things;

import AbstrClasses.SceneObject;
import Enums.BedsheetConditions;
import Interfaces.intBedsheet;

import static Enums.BedsheetConditions.*;

public class Bedsheet extends SceneObject implements intBedsheet {

    public BedsheetConditions condition = NORMAL;
    public BedsheetConditions position = ON_THE_BED;
    public BedsheetConditions isCut;

    public Bedsheet(String name) {
        super(name);
        System.out.println("Простыня теперь находится в комнате.");
    }


    @Override
    public String toString() {
        return this.name + " " + this.condition.label;
    }

    public void startFluttering() {
        this.setCondition(FLUTTERING);
    }

    public BedsheetConditions getCondition() {
        return this.condition;
    }
    public BedsheetConditions getPosition() {
        return this.position;
    }

    public void setCondition(BedsheetConditions condition) {
        this.condition = condition;
        System.out.println(this.name + " " + this.condition.label);
    }
    public void setPosition(BedsheetConditions position) {
        this.position = position;
        System.out.println(this.name + " " + this.position.label);
    }
}
