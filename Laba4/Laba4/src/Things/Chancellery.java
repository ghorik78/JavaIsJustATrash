package Things;

import AbstrClasses.SceneObject;
import Enums.ChancelleryPos;
import Interfaces.intChancellery;

public class Chancellery extends SceneObject implements intChancellery {

    public ChancelleryPos position = ChancelleryPos.IN_BOX;

    public Chancellery(String name) {
        super(name);
    }

    public ChancelleryPos getPosition() {
        return this.position;
    }

    public void setPosition(ChancelleryPos position) {
        this.position = position;
        System.out.println(this.name + " " + this.position.label);
    }
}
