package Things;

import AbstrClasses.SceneObject;
import Enums.MotorStatus;
import Interfaces.intMotor;

import static Enums.MotorStatus.*;

public class Motor extends SceneObject implements intMotor {

    public MotorStatus condition = TURNED_OFF;

    public Motor(String name) {
        super(name);
        System.out.println("У Карлсона на спине появился моторчик.");
    }



    @Override
    public String toString() {
        return this.name + " " + this.condition.label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Motor motor = (Motor) o;
        return condition == motor.condition;
    }


    public void turnOn(Motor motor) {
        motor.condition = TURNED_ON;
        System.out.println(this.toString());
    }

    public void turnOff(Motor motor) {
        motor.condition = TURNED_OFF;
        System.out.println(this.toString());
    }


    public class Silencer {

        public String name;
        public Silencer(String name) {
            this.name = name;
            System.out.println("У моторчика теперь существует глушитель.");
        }

        public void lessNoise() {
            Motor.this.setCondition(MAKE_LESS_NOISE);
            System.out.println(Motor.this.name + " " + Motor.this.condition.label);
        }

    }


    public boolean checkMotor(Motor motor) {
        return motor.condition.equals(TURNED_ON);
    }

    public void setCondition(MotorStatus condition) {
        this.condition = condition;
    }
}
