package Entities;

import AbstrClasses.SceneObject;
import Enums.DogStatus;
import Interfaces.intDog;

import static Enums.DogStatus.BARKING;

public class Dog extends SceneObject implements intDog {

    private DogStatus condition = DogStatus.NORMAL;

    public void setCondition(DogStatus condition) {this.condition = condition; }

    @Override
    public String toString() {
        return this.name + " " + this.condition.label;
    }

    public void startBarking() {
        this.setCondition(BARKING);
        System.out.println(this.toString());
    }


    public Dog(String name) {
        super(name);
        System.out.println("Ёффа забежала в комнату.");
    }
}
