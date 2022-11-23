package Things;

import Enums.BedsheetConditions;
import Exceptions.InteractionException;
import Interfaces.intScissors;

public class Scissors extends Chancellery implements intScissors {
    public Scissors(String name) {
        super(name);
    }

    public void cutBedsheet(Bedsheet bedsheet) throws InteractionException {
        if (bedsheet != null) {
            bedsheet.isCut = BedsheetConditions.CUT;
            System.out.println(bedsheet.name + " " + bedsheet.isCut.label);
        } else throw new InteractionException("Не получилось разрезать простыню.", "Простыня");
    }
}
