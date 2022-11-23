package ClassesForApp;

import ClassesForColl.Route;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class Main {

    static String database = System.getenv("LABFILE");
    public static ArrayDeque<Route> objects = new ArrayDeque<>();

    public static java.time.LocalDate collCrDate = LocalDate.now();

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);
        Commander commander = new Commander();

        try {
            commander.deserializeRoute(database);
        } catch (IOException | IllegalAccessException | NoSuchFieldException e) {
            System.out.println("Проблемы с десериализацией объекта: " + e.getMessage());
        }

        CollectionHelper helper = new CollectionHelper();

        while (true) {
            CommandListener listener = new CommandListener();
            listener.checkMsg(listener.receiveMsg());
        }

    }
}
