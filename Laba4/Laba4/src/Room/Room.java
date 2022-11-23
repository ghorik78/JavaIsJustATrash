package Room;

import Entities.Carlson;
import Entities.Dog;
import Entities.Human;
import Exceptions.ContinuationException;
import Exceptions.InteractionException;
import Interfaces.intMalysh;
import Interfaces.intPhone;
import Things.*;

import java.util.ArrayList;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;


public final class Room {
    private Carlson carlson;
    private Motor motor;
    private Chandelier chandelier;
    private Bedsheet bedsheet;
    private Dog dog;
    private Motor.Silencer silencer;
    private Human children;
    private Bed bed;
    private Chancellery crayons;
    private Scissors scissors;
    private intMalysh malysh = Human.tryToStopCarlson();
    private Table table;
    private Table.Box box;


    ArrayList<Object> objectList = new ArrayList<Object>();

    public void addObject(Object obj) {
        objectList.add(obj);
    }

    public void prepareTail() throws ContinuationException {
        try {
            if (objectList.size() > 0) {
                carlson = (Carlson) objectList.get(0);
                motor = (Motor) objectList.get(1);
                silencer = (Motor.Silencer) objectList.get(2);
                chandelier = (Chandelier) objectList.get(3);
                bedsheet = (Bedsheet) objectList.get(4);
                dog = (Dog) objectList.get(5);
                children = (Human) objectList.get(6);
                bed = (Bed) objectList.get(7);
                crayons = (Chancellery) objectList.get(8);
                scissors = (Scissors) objectList.get(9);
                table = (Table) objectList.get(10);
                box = (Table.Box) objectList.get(11);

            }
            else throw new ContinuationException("Сказка не готова.");
        }
        catch (ClassCastException e ) {
            System.out.println("При добавлении объектов произошла ошибка");
            System.exit(1);
        }
    }

    public void sleeping(int time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            System.out.println("Ошибка задержек.");
        }
    }

   public void go() {

        intPhone phone = new intPhone() {
           public void Ring() {
               System.out.println("Раздаётся звонок телефона.");
           }
       };

        try {
            prepareTail();
        } catch (ContinuationException e) {
            System.out.println(e.getMessage());
        }

        System.out.println();

        try {
            sleeping(750);
            carlson.Say("— Спокойствие, только спокойствие!");
            carlson.Say("— Не тебе учить лучшее в мире привидение, как должны вести себя привидения. Я только слегка попугаю всех до смерти, никто этого даже и не заметит.");
            System.out.println();
        } catch (NullPointerException e) {
            System.out.println("Говорить было некому.");
            System.exit(1);
        }

        sleeping(750);

        try {
            carlson.goToBed(bed);
            carlson.takeBedsheet(bedsheet);

            System.out.println();
            carlson.Say("— Материал подходящий, можно сделать вполне приличную одежду для привидения.");
            System.out.println();

            carlson.openBox(box);
            sleeping(750);
            carlson.takeCrayons(table, box, crayons);
            carlson.takeScissors(table, box, scissors);
            malysh.tryToStop(carlson);
            carlson.drawFace(bedsheet, crayons);
            carlson.cut(bedsheet, scissors);

            System.out.println();

            carlson.Say("— Простыня — это пустяки, дело житейское. А привидение должно видеть, что происходит вокруг, иначе оно начнёт блуждать и попадёт в конце концов невесть куда,");

            System.out.println();

            carlson.wrapUp(bedsheet);
            sleeping(750);

            try {
                children.startConsternation();
                dog.startBarking();
                sleeping(750);
                motor.turnOn(motor);
            } catch (NullPointerException e) {
                System.out.println("Нужного объекта в сказке не оказалось.");
                System.exit(1);
            }

            sleeping(750);
        } catch (InteractionException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getErrObj());
            e.killProcess();
        }


        try {
            carlson.flyAround(motor, carlson, chandelier);
            chandelier.startSwaying(chandelier);
            bedsheet.startFluttering();

            sleeping(750);
            System.out.println();
            carlson.Say("— Я небольшое привидение с мотором! — Дикое, но симпатичное!");
            System.out.println();

            children.stopConsternation();
            children.watchTheFlight();

            sleeping(750);
            System.out.println();
            carlson.Say("— Вообще говоря, я люблю, когда во время полёта жужжит мотор, но, поскольку я привидение, следует, вероятно, включить глушитель. Вот так!");
            System.out.println();

            sleeping(750);
            silencer.lessNoise();
            System.out.println();

            sleeping(750);
            System.out.println(carlson.name + " сделал несколько кругов совершенно бесшумно и стал ещё больше похож на привидение.");
            System.out.println("Теперь дело было лишь за тем, чтобы найти, кого пугать.");

            sleeping(750);
            System.out.println();
            carlson.Say("— Может быть, мы отправимся на лестничную площадку? Кто-нибудь войдёт в дом и испугается до смерти!");
            System.out.println();

            sleeping(750);
            phone.Ring();
            children.stay();
        }
        catch (InteractionException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getErrObj());
            System.exit(1);
        }


        sleeping(750);


        System.out.println("\nЖуткое зрелище...");
   }

}

/*
— Спокойствие, только спокойствие! — ответил Карлсон. — Не тебе учить лучшее в мире привидение, как должны вести себя привидения. Я только слегка попугаю всех до смерти, никто этого даже и не заметит. — Он подошёл к кровати Малыша и взял простыню. — Материал подходящий, можно сделать вполне приличную одежду для привидения.

        Карлсон достал из ящика письменного стола цветные мелки и нарисовал в одном углу простыни страшную рожу. Потом он взял ножницы и, прежде чем Малыш успел его остановить, быстро прорезал две дырки для глаз.

        — Простыня — это пустяки, дело житейское. А привидение должно видеть, что происходит вокруг, иначе оно начнёт блуждать и попадёт в конце концов невесть куда.

        Затем Карлсон закутался с головой в простыню, так что видны были только его маленькие пухлые ручки.

        Хотя дети и знали, что это всего-навсего Карлсон, закутанный в простыню, они всё же слегка испугались; а что касается Ёффы, то он бешено залаял. Когда же Карлсон включил свой моторчик и принялся летать вокруг люстры — простыня на нём так и развевалась, — стало ещё страшнее. Это было и вправду жуткое зрелище.

        — Я небольшое привидение с мотором! — кричал он. — Дикое, но симпатичное!

        Дети притихли и боязливо следили за его полётом. А Ёффа просто надрывался от лая.

        — Вообще говоря, — продолжал Карлсон, — я люблю, когда во время полёта жужжит мотор, но, поскольку я привидение, следует, вероятно, включить глушитель. Вот так!

        Он сделал несколько кругов совершенно бесшумно и стал ещё больше похож на привидение.

        Теперь дело было лишь за тем, чтобы найти, кого пугать.

        — Может быть, мы отправимся на лестничную площадку? Кто-нибудь войдёт в дом и испугается до смерти!

        В это время зазвонил телефон, но Малыш решил не подходить. Пусть себе звонит!

*/
