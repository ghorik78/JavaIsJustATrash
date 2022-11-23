package Classes;


import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;
import Classes.Route;
import ServerPackages.Commander;
import ServerPackages.Main;

import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.regex.Pattern;

public class CollectionHelper {

    public ArrayDeque<Route> sortCollection(ArrayDeque<Route> coll) {
        ArrayList<Route> buffer = new ArrayList<>(coll);

        buffer.sort(new Comparator<Route>() {
            public int compare(Route route1, Route route2) {
                return route1.getId().compareTo(route2.getId());
            }
        });

        return new ArrayDeque<>(buffer);
    }

    public boolean deserializedObjectChecker(Route route) throws IllegalAccessException, NoSuchFieldException {
        for (Field f : route.getClass().getDeclaredFields()) {
            f.setAccessible(true);

            String fieldName = f.getName();
            switch (fieldName) {
                case "id" -> {
                    if (getObjectById(Commander.collection, (Long) f.get(route)) != null) {
                        System.out.println("ID номер " + f.get(route) + "не является уникальным!");
                        return false;
                    }
                }
                case "name" -> {
                    if (!Pattern.compile(".+").matcher((String) f.get(route)).matches()) {
                        System.out.println("Недопустимое имя объекта!");
                        return false;
                    }
                }
                case "coordinates" -> {
                    Coordinates tmp = (Coordinates) f.get(route);
                    for (Field subField : tmp.getClass().getDeclaredFields()) {
                        subField.setAccessible(true);
                        if (subField.getName().equals("x") && !Pattern.compile("-*\\d+").matcher(String.valueOf(subField.get(tmp))).matches()) {
                            System.out.println("Недопустимая координата X!");
                            return false;
                        }
                        else if (subField.getName().equals("y")) {
                            if ((Long) subField.get(tmp) <= -382) {
                                System.out.println("Значение координаты Y должно быть больше -382!");
                                return false;
                            }
                        }
                    }
                }
                case "from" -> {
                    LocationFrom tmp = (LocationFrom) f.get(route);
                    for (Field subField : tmp.getClass().getDeclaredFields()) {
                        subField.setAccessible(true);
                        if (subField.getName().equals("x") && subField.get(tmp) == null) {
                            System.out.println("Значение начальной координаты X неверно!");
                            return false;
                        } else if (subField.getName().equals("y") && (subField.get(tmp) == null)) {
                            System.out.println("Значение начальной координаты Y неверно!");
                            return false;
                        } else if (subField.getName().equals("name") && String.valueOf(subField.get(tmp)).equals("")) {
                            route.getFrom().setName(null);
                        }
                    }
                }
                case "distance" -> {
                    long objDist = (long) f.get(route);
                    if (objDist <= 1) {
                        System.out.println("Поле distance должно быть больше 1!");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public Route getObjectById(ConcurrentLinkedDeque<Route> coll, Long id) throws NoSuchFieldException, IllegalAccessException {
        if (coll.size() != 0) {
            for (Route obj : coll) {
                Field f = obj.getClass().getDeclaredField("id");
                f.setAccessible(true);
                if(Objects.equals(String.valueOf(f.get(obj)), String.valueOf(id))) return obj;
            }
        }
        return null;
    }
}
