package Interfaces;

import Enums.ChandelierStatus;
import Things.Chandelier;

public interface intChandelier {
    void setCondition(ChandelierStatus condition);
    void startSwaying(Chandelier chandelier);
    /*void stopSwaying();*/
}
