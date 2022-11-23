package Entities;

import Enums.HumanStatus;
import Exceptions.InteractionException;
import Interfaces.intHuman;
import Interfaces.intMalysh;


public class Human implements intHuman {

    public String name;
    public HumanStatus condition;

    public Human(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name + " " + this.condition.label;
    }
    public void Say(String msg) {
        System.out.println(msg + " - сказал " + this.name);
    }

    public void startConsternation() {
        this.condition = HumanStatus.CONSTERNATION;
        System.out.println(this.toString());
    }

    public void stopConsternation() {
        this.condition = HumanStatus.NORMAL;
        System.out.println(this.toString());
    }

    public static intMalysh tryToStopCarlson() {
        class Malysh implements intMalysh {
            @Override
            public void tryToStop(Carlson carlson) throws InteractionException {
                if (carlson != null) System.out.println("Малыш попытался остановить Карлсона");
                else throw new InteractionException("Остановить Карлсона не получилось.", "Карлсон");
            }
        }
        return new Malysh();
    }

    public void watchTheFlight() {
        System.out.println(this.name + " " + "проводят наблюдение за полётом.");
    }

    public void stay() {
        System.out.println(this.name + " решают остаться на месте.");
    }
}
