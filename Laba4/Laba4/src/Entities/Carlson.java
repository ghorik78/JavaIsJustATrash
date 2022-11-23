package Entities;

import AbstrClasses.FairyPerson;
import Enums.*;
import Exceptions.InteractionException;
import Interfaces.intCarlson;
import Things.*;

import java.util.Objects;

import static Enums.BedsheetConditions.NOT_ON_THE_BED;
import static Enums.BedsheetConditions.ON_THE_BED;
import static Enums.CarlsonConditions.*;
import static Enums.ChancelleryPos.IN_BOX;
import static Enums.ChancelleryPos.NOT_IN_BOX;

public class Carlson extends FairyPerson implements intCarlson {

    public CarlsonConditions condition = NORMAL;
    public CarlsonConditions position = IN_ROOM;

    public Carlson(String name) {
        super(name);
        System.out.println("Карлсон таинственным образом оказался в комнате.");
    }

    @Override
    public String toString() {
        return this.name + " " + this.condition.label;
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition);
    }

    public void flyAround(Motor motor, Carlson carlson, Chandelier chandelier) throws InteractionException {
        if (motor.checkMotor(motor) && chandelier != null) {
            this.setCondition(FLYING);
        }
        else if (!motor.checkMotor(motor)) throw new InteractionException("При старте полёта что-то пошло не так.", "Моторчик");
        else throw new InteractionException("При старте полёта что-то пошло не так.", "Люстра");
    }

    public void stopFlying(Motor motor, Carlson carlson) throws InteractionException {
        if (motor != null && motor.checkMotor(motor) && this.condition == FLYING) {
            carlson.setCondition(CarlsonConditions.NORMAL);
            motor.condition = MotorStatus.TURNED_OFF;
            System.out.println(this.name + " " + this.condition.label);
        }
        else {
            System.out.println("Карлсон и так никуда не взлетал :(");
            System.exit(1);
        }
    }

    public void wrapUp(Bedsheet bedsheet) throws InteractionException {
        if (bedsheet != null) {
            this.setCondition(WRAPPED);
        }
        else throw new InteractionException("Простыни в сказке не оказалось.", "Простыня");
    }

    public void Say (String msg) {
        System.out.println(msg + " - сказал Карлсон.");
    }

    public void openBox(Table.Box box) {
        if (box != null) {
            box.setCondition(BoxStatus.OPENED);
        }
        else throw new InteractionException("Ящик не был открыт", "Ящик");
    }

    public void takeCrayons(Table table, Table.Box box, Chancellery crayons) throws InteractionException {
        if (crayons != null && table != null && box != null && crayons.getPosition().equals(IN_BOX)) {
            crayons.setPosition(NOT_IN_BOX);
        }
        else if (table == null) throw new InteractionException("Не получилось достать мелки." , "Стол");
        else if (box == null) throw new InteractionException("Не получилось достать мелки." , "Ящик");
        else throw new InteractionException("Не получилось достать мелки." , "Мелки");
    }

    public void takeScissors(Table table, Table.Box box, Scissors scissors) throws InteractionException {
        if (scissors != null && table != null && box != null && scissors.getPosition().equals(IN_BOX)) {
            scissors.setPosition(NOT_IN_BOX);
        }
        else if (table == null) throw new InteractionException("Не получилось достать мелки." , "Стол");
        else if (box == null) throw new InteractionException("Не получилось достать мелки." , "Ящик");
        else throw new InteractionException("Не получилось достать мелки." , "Ножницы");
    }

    public void takeBedsheet(Bedsheet bedsheet) throws InteractionException {
        if (bedsheet != null && bedsheet.getPosition().equals(ON_THE_BED) && this.getPosition().equals(NEAR_THE_BED)) {
            System.out.println(this.name + " " + "взял простыню в руки.");
            bedsheet.setCondition(NOT_ON_THE_BED);
        }
        else throw new InteractionException("Взять простыню не удалось", "Простыня");
    }

    public void drawFace(Bedsheet bedsheet, Chancellery crayons) throws InteractionException {
        if (bedsheet != null && crayons != null && crayons.getPosition().equals(NOT_IN_BOX)) {
            System.out.println(this.name + " нарисовал рожицу на простыне.");
        }
        else {
            throw new InteractionException("На несуществующей простыне нельзя рисовать.", "Простыня");
        }
    }

    public void goToBed(Bed bed) throws InteractionException {
        if (bed != null) {
            this.setPosition(NEAR_THE_BED);
        }
        else {
            throw new InteractionException(this.name + " " + "не может пойти к несуществующей кровати.", "Кровать");
        }
    }

    public void cut(Bedsheet bedsheet, Scissors scissors) throws InteractionException {
        if (bedsheet != null && scissors != null) {
            scissors.cutBedsheet(bedsheet);
        } else throw new InteractionException("Карлсон не смог разрезать простыню.", "Простыня");
    }


    public void setCondition(CarlsonConditions condition) {
        this.condition = condition;
        System.out.println(this.name + " " + this.condition.label);
    }
    public void setPosition(CarlsonConditions position) {
        this.position = position;
        System.out.println(this.name + " " + this.position.label);
    }

    public CarlsonConditions getCondition() { return this.condition; }
    public CarlsonConditions getPosition() {return this.position; }
}
