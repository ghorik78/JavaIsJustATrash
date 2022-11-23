package Interfaces;

import Enums.BedsheetConditions;

public interface intBedsheet {
    void setCondition(BedsheetConditions condition);
    void startFluttering();
    BedsheetConditions getPosition();
    BedsheetConditions getCondition();
}
