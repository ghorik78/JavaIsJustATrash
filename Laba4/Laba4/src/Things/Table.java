package Things;

import AbstrClasses.SceneObject;
import Enums.BoxStatus;
import Enums.TablePos;
import static Enums.TablePos.IN_ROOM;

public class Table extends SceneObject {

    public TablePos position = IN_ROOM;

    public Table(String name) {
        super(name);
    }

    public TablePos getPosition() {
        return this.position;
    }

    public static class Box extends Table {
        public BoxStatus condition;
        public Box(String name) {
            super(name);
        }

        public void setCondition(BoxStatus condition) {
            this.condition = condition;
        }
    }
}
