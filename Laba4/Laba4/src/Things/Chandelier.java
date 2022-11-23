package Things;

import AbstrClasses.SceneObject;
import Enums.ChandelierStatus;
import Exceptions.InteractionException;
import Interfaces.intChandelier;
import static Enums.ChandelierStatus.SWAYING;

public class Chandelier extends SceneObject implements intChandelier {

    public ChandelierStatus condition = ChandelierStatus.NORMAL;

    public void setCondition(ChandelierStatus condition) {
        this.condition = condition;
    };

    @Override
    public String toString() {
        return this.name + " " + this.condition.label;
    }

    public void startSwaying(Chandelier chandelier) throws InteractionException {
        if (chandelier != null) {
            this.setCondition(SWAYING);
            System.out.println(this.toString());
        }
        else {
            throw new InteractionException("Несуществующая люстра не может качаться.", "Люстра");
        }
    }

    public void stopSwaying() throws InteractionException {
        if (this.condition == SWAYING) {
            this.condition = ChandelierStatus.NORMAL;
            System.out.println(this.toString());
        }
        else {
            throw new InteractionException("Люстра и так не качалась.", "Люстра");
        }
    }

    public Chandelier (String name) {
        super(name);
        System.out.println("Люстра висит на потолке комнаты.");
    }

}
