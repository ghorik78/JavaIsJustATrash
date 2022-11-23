package Interfaces;

import Entities.Carlson;
import Enums.CarlsonConditions;
import Things.*;

public interface intCarlson {
    void setCondition(CarlsonConditions condition);
    CarlsonConditions getCondition();
    void setPosition(CarlsonConditions position);
    CarlsonConditions getPosition();
    void flyAround(Motor motor, Carlson carlson, Chandelier chandelier);
    void wrapUp(Bedsheet bedsheet);
    void stopFlying(Motor motor, Carlson carlson);
    void Say(String msg);
    void takeCrayons(Table table, Table.Box box, Chancellery crayons);
    void takeScissors(Table table, Table.Box box, Scissors scissors);
    void takeBedsheet(Bedsheet bedsheet);
    void drawFace(Bedsheet bedsheet, Chancellery crayons);
    void goToBed(Bed bed);
    void cut(Bedsheet bedsheet, Scissors scissors);
}
