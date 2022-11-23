import Entities.*;
import Room.*;
import Things.*;

public class Main {

    public static void main(String[] args) {
	    Room room = new Room();


        Motor motor = new Motor("Моторчик");
        Motor.Silencer silencer = motor.new Silencer("Глушитель");
        room.addObject(new Carlson("Карлсон"));
        room.addObject(motor);
        room.addObject(silencer);
        room.addObject(new Chandelier("Люстра"));
        room.addObject(new Bedsheet("Простыня"));
        room.addObject(new Dog("Ёффа"));
        room.addObject(new Human("Дети"));
        room.addObject(new Bed("Кровать"));
        room.addObject(new Chancellery("Мелки"));
        room.addObject(new Scissors("Ножницы"));
        room.addObject(new Table("Стол"));
        room.addObject(new Table.Box("Ящик"));

        room.go();

    }
}
