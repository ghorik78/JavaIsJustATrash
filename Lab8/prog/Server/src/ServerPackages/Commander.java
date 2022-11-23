package ServerPackages;

import Classes.*;
import Commands.IdentifyColorCommand;
import Interfaces.CommandData;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.*;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Commander {
    public static final Logger logger = LogManager.getLogger(Commander.class);
    private DatagramSocket socket; private InetSocketAddress clientAddress; private String usr;

    private final ForkJoinPool requestsPool = Main.requestsPool;
    private final ExecutorService queryPool = Main.queryPool;
    private final ExecutorService answerPool = Main.answerPool;

    private final SQLManager manager = new SQLManager();
    static LocalDate initDate = LocalDate.now();

    public Commander(DatagramSocket socket, InetSocketAddress clientAddress, String usr) {
        this.socket = socket;
        this.clientAddress = clientAddress;
        this.usr = usr;
    }

    public Commander() {}

    public String getCurrentUsr() { return usr; }

    Server server = new Server();

    public void help(String[] args, String usr) throws IOException {
        if (isArgsEmpty("help", args)) {
            String msg = "";
            for (Method m : Invoker.class.getDeclaredMethods()) {
                if (m.isAnnotationPresent(CommandData.class)) {
                    CommandData cmd = m.getAnnotation(CommandData.class);
                    msg = msg.concat(cmd.name() + " : " + cmd.description() + "\n");
                }
            }
            msg = msg + " " + usr;
            answerPool.execute(new Responder<>(socket, clientAddress, new Notification(msg, new String[]{"1"}, true)));
            logger.info("Command \"help\" has been executed");
        } else {
            logger.warn("Command \"help\" doesn't have arguments!");
        }
    }

    public void info(String[] args, String usr) throws IOException {
        if (isArgsEmpty("info", args)) {
            String msg = "";
            msg = msg.concat("Тип коллекции: " + Main.collection.getClass().getSimpleName() + "\n" +
                    "Дата инициализации: " + initDate + "\n" +
                    "Количество элементов в данный момент: " + (long) Main.collection.size() + "\n" +
                    "Тип хранимых объектов: Route" + "\n");
            logger.info("Command \"info\" has been executed.");
            answerPool.execute(new Responder<>(socket, clientAddress, new Notification(msg, new String[]{"1"}, true, new ArrayList<>(Main.collection))));
        } else {
            logger.warn("Command \"info\" doesn't have arguments!");
        }
    }

    public void show(String[] args, String usr) throws IOException {
        fillCollectionFromDB();
        if (Main.collection.size() != 0) {
            Notification ans = new Notification("Check alert.", new String[]{"1"}, true, new ArrayList<>(Main.collection));
            answerPool.execute(new Responder<>(socket, clientAddress, ans));
            logger.info("Command \"show\" has been executed.");
        } else {
            answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Коллекция пуста.", new String[]{"0"}, true)));
            logger.warn("Collection is empty.");
        }
    }

    public void add(Route receivedRoute, String[] args, String usr) throws NoSuchFieldException, IllegalAccessException, IOException {
        fillCollectionFromDB();
        logger.info("New route received. Name of received object: " + receivedRoute.getName());
        boolean result = manager.insertRouteIntoDB(receivedRoute, usr);
        long setId = 0;
        if (result) {
            try {
                PreparedStatement statement = SQLManager.connection.prepareStatement("SELECT * FROM objects WHERE userBy = ?");
                statement.setString(1, usr);
                ResultSet rs = statement.executeQuery();
                while (rs.next()) setId = rs.getInt(1);
                receivedRoute.setIdManually(setId);
                logger.info("Route " + receivedRoute.getName() + " has been added.");
                fillCollectionFromDB();
                sortCollectionByName(usr);
                answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Объект успешно добавлен в базу данных. Его id: " + setId, new String[]{"1"}, true, new ArrayList<>(Main.collection))));
                logger.info("Command \"add\" has been executed.");
            } catch (SQLException e) {
                System.out.println("Ошибка при получении объекта из БД: " + e.getMessage());
                answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Ошибка при получении объекта из БД.", new String[]{"0"}, true)));
            }
        } else {
            answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Ошибка во время добавления объекта в БД.", new String[]{"0"}, true)));
            logger.error("An error during adding object into database.");
        }
    }

    public void update(Route routeToChange, String[] args, String usr) {
        fillCollectionFromDB();
        if (Pattern.compile("\\d+").matcher(args[0]).matches()) {
            Long id = Long.parseLong(args[0]);
            Optional<Route> optionalRoute = Main.collection.stream().filter(route -> Objects.equals(route.getId(), id)).findFirst();
            Route routeToUpdate = optionalRoute.orElse(null);
            if (routeToUpdate == null) {
                answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Объект с таким id отсутствует!", new String[]{"0"}, true)));
                logger.warn("Route with id " + id + " doesn't exists!");
            }
            else if (!routeToUpdate.getOwner().equals(usr)) {
                answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Нельзя менять объекты, которые вам не принадлежат!", new String[]{"0"}, true)));
            }
            else {
                Main.collection.remove(routeToUpdate);
                try {
                    if (manager.updateRouteInDB(id, routeToChange.getName(), routeToChange.getCoordinates().getX(), routeToChange.getCoordinates().getY(),
                            routeToChange.getCreationDate(), routeToChange.getFrom().getX(), routeToChange.getFrom().getY(),
                            routeToChange.getFrom().getName(), routeToChange.getTo().getX(), routeToChange.getTo().getY(),
                            routeToChange.getTo().getZ(), routeToChange.getDistance())) {
                        Main.collection.add(routeToChange);
                        Notification ans = new Notification("Объект успешно обновлён.", new String[]{"1"}, true, new ArrayList<Route>(Main.collection));
                        answerPool.execute(new Responder<>(socket, clientAddress, ans));
                    } else {
                        answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Не удалось обновить объект.", new String[]{"0"}, true)));
                    }

                    logger.info("Route has been updated.");
                } catch (Exception e) {
                    answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Ошибка при выполнении команды update!", null, true)));
                    logger.error("Error during execution command: update.");
                }
                answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Объект успешно обновлён", null, true)));
            }
        } else {
            answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Аргумент не был введён или введён неправильно!", null, true)));
            logger.error("Invalid argument for update command!");
        }
        logger.info("Command \"update\" has been executed.");
    }

    public void remove_by_id(String[] args, String usr) {
        fillCollectionFromDB();
        if (!Pattern.compile("\\d+").matcher(args[0]).matches()) {
            answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Id не был введён или введён неправильно!", new String[]{"0"}, true)));
            logger.error("Invalid id for \"remove_by_id\" command!");
        }
        else {
            Long id = Long.parseLong(args[0]);
            Optional<Route> optionalRoute = Main.collection.stream().filter(route -> Objects.equals(route.getId(), id)).findFirst();
            Route routeToRemove = optionalRoute.orElse(null);
            if (routeToRemove == null) {
                answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Объект с таким id отсутствует!", new String[]{"0"}, true)));
                logger.error("Route with id " + id + " doesn't exists!");
            } else if (!routeToRemove.getOwner().equals(usr)) {
                answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Нельзя изменять объекты, которые вам не принадлежат!", new String[]{"0"}, true)));
                logger.error("Can't modify this object. " + usr + " isn't owner.");
            }
            else {
                if (manager.removeRouteFromDB(id)) {
                    Main.collection.remove(routeToRemove);
                    logger.info("Route has been removed.");
                    answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Объект был удалён.", new String[]{"1"}, true, new ArrayList<>(Main.collection))));
                } else {
                    logger.error("An error during removing object from DB.");
                    answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Ошибка во время удаления.", new String[]{"0"}, true)));
                }
            }
        }
        logger.info("Command \"remove_by_id\" has been executed.");
    }

    public void clear(String[] args, String usr) throws IOException {
        Main.collection.stream().filter(route -> route.getOwner().equals(usr)).forEach(route -> manager.removeRouteFromDB(route.getId()));
        Main.collection.stream().filter(route -> route.getOwner().equals(usr)).forEach(route -> Main.collection.remove(route));
        answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Все ваши объекты были удалены.", new String[]{"1"} ,true)));
        logger.info("Collection has been cleared.");
        logger.info("Command \"clear\" has been executed.");
    }

    public void execute_script(String[] args, String usr) {}

    public void remove_first(String[] args, String usr) throws IOException {
        if (args.length != 0) {
            answerPool.execute(new Responder<>(socket, clientAddress, new Notification("У данной команды не может быть аргументов!", new String[]{"0"}, true)));
            logger.error("Command \"remove_first\" doesn't have arguments!");
        }
        else {
            if (Main.collection.size() == 0) {
                answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Коллекция пуста.", new String[]{"1"}, true)));
                logger.error("Collection is empty!");
            }
            else {
                Route firstRoute = Main.collection.stream().filter(route -> route.getOwner().equals(usr)).findFirst().get();
                Main.collection.remove(firstRoute);
                logger.info("First route has been removed");
                answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Первый элемент был удалён.", new String[]{"1"}, true, new ArrayList<>(Main.collection))));
                logger.info("Command \"remove_first\" has been executed.");
            }
        }
    }

    public void head(String[] args, String usr) throws IOException {
        if (args.length != 0) {
            answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Данная команда не может иметь аргументов!", new String[]{"0"}, true)));
            logger.error("Head command doesn't have arguments!");
        } else if (Main.collection.size() != 0) {
            Route route = Main.collection.stream().filter(route1 -> route1.getOwner().equals(usr)).findFirst().get();
            answerPool.execute(new Responder<>(socket, clientAddress, new Notification(route.toString(), new String[]{"1"}, true, new ArrayList<>(Main.collection))));
            logger.info("Command \"head\" has been executed.");
        }

    }

    public void add_if_max(Route receivedRoute, String[] args, String usr) throws IOException {
        String name = receivedRoute.getName();
        sortCollectionByName(usr);
        Route maxRouteName = Main.collection.getFirst();
        System.out.println(name.compareTo(maxRouteName.getName()) < 0);
        if (name.compareTo(maxRouteName.getName()) < 0) {
            logger.info("Condition for adding is met.");
            try {
                if (manager.insertRouteIntoDB(receivedRoute, usr)) Main.collection.addFirst(receivedRoute);
                answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Объект был успешно добавлен.", new String[]{"1"}, true, new ArrayList<>(Main.collection))));
                logger.info("Command \"add_if_max\" has been executed.");
            } catch (Exception e) {
                logger.error("Error during execution command \"add_if_max\": class not found!");
                answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Ошибка во время выполнения команды \"add_if_max\"", new String[]{"0"}, true)));
            }
        }
        else {
            logger.error("Condition for adding isn't met.");
            answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Условие для добавления не выполнено!", new String[]{"0"}, true)));
        }
    }

    public void remove_all_by_distance(String[] args, String usr) throws IOException {
        if (args.length != 1) {
            answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Неправильно введны аргументы команды!", new String[]{"0"}, true)));
            logger.error("Command \"remove_all_by_distance\" has incorrect argument(s)!");
        } else {
            Main.collection.stream().filter(route -> route.getDistance() == Long.parseLong(args[0])).forEach(route -> manager.removeRouteFromDB(route.getId()));
            Main.collection.stream().filter(route -> route.getDistance() == Long.parseLong(args[0])).filter(route -> route.getOwner().equals(usr)).forEach(route -> Main.collection.remove(route));
            answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Объекты с distance = " + args[0] + "были удалены", new String[]{"1"}, true, new ArrayList<>(Main.collection))));
            logger.info("Command \"remove_all_by_distance\" has been executed.");
        }
    }

    public void print_unique_distance(String[] args, String usr) throws IOException {
        if (isArgsEmpty("print_unique_distance", args)) {
            answerPool.execute(new Responder<>(socket, clientAddress, new Notification("", new String[]{"1"}, true, Main.collection.stream().filter(distinctByKey(Route::getDistance)).collect(Collectors.toCollection(ArrayList::new)))));
            logger.info("Command \"print_unique_distance\" has been executed.");
        }
    }

    public void print_field_ascending_distance(String[] args, String usr) throws IOException {
        if (isArgsEmpty("print_field_ascending_distance", args)) {
            ArrayList<Route> result = Main.collection.stream().sorted(new Comparator<Route>() {
                @Override
                public int compare(Route r, Route r1) {
                    if (r.getDistance() > r1.getDistance()) return 1;
                    else if (r.getDistance() < r1.getDistance()) return -1;
                    else return 0;
                }
            }).collect(Collectors.toCollection(ArrayList::new));
            answerPool.execute(new Responder<>(socket, clientAddress, new Notification("", new String[]{"1"}, true, result)));
            logger.info("Command \"print_unique_distance\" has been executed.");
        }
    }

    public void identifyColor(String[] args, String usr) {
        ObjectColor objectColor = new ObjectColor();
        if (Main.objectColors.get(args[0]) == null) {
            objectColor.setRed(Math.random());
            objectColor.setGreen(Math.random());
            objectColor.setBlue(Math.random());
            Main.objectColors.put(args[0], objectColor);
            answerPool.execute(new Responder<>(socket, clientAddress, new IdentifyColorCommand("identifyColor", null, objectColor)));
        } else {
            answerPool.execute(new Responder<>(socket, clientAddress, new IdentifyColorCommand("identifyColor", null, Main.objectColors.get(args[0]))));
        }
    }

    public void sortCollectionByName(String usr) {
        Main.collection = Main.collection.stream().sorted(Comparator.comparing(Route::getName)).collect(Collectors.toCollection(ConcurrentLinkedDeque::new));
        logger.info("Collection has been sorted.");
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public void fillCollectionFromDB() {
        ConcurrentLinkedDeque<Route> temp = new ConcurrentLinkedDeque<>();
        Main.collection.clear();
        try {
            Statement statement = SQLManager.connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM objects");
            while (rs.next()) {
                Route routeToAdd = new Route();
                Coordinates coordinatesToAdd = new Coordinates();
                LocationFrom locationFrom = new LocationFrom();
                LocationTo locationTo = new LocationTo();

                routeToAdd.setIdManually(rs.getLong(1));
                routeToAdd.setName(rs.getString(2));
                coordinatesToAdd.setX(rs.getLong(3));
                coordinatesToAdd.setY(rs.getLong(4));
                routeToAdd.setCreationDate(LocalDate.parse(rs.getString(5)));
                locationFrom.setX(rs.getInt(6));
                locationFrom.setY(rs.getLong(7));
                locationFrom.setName(rs.getString(8));
                locationTo.setX(rs.getDouble(9));
                locationTo.setY(rs.getFloat(10));
                locationTo.setZ(rs.getLong(11));
                routeToAdd.setDistance(rs.getLong(12));
                routeToAdd.setCoordinates(coordinatesToAdd);
                routeToAdd.setFrom(locationFrom);
                routeToAdd.setTo(locationTo);
                routeToAdd.setOwner(rs.getString(13));
                Main.collection.add(routeToAdd);
                logger.info("Object with id " + routeToAdd.getId() + " has been added to the collection.");
            }
            statement.close();
        } catch (SQLException e) {
            logger.error("An error during filling the collection from database!");
        }
    }

    public boolean isArgsEmpty(String commandName, String[] args) throws IOException {
        if (args.length != 0) {
            answerPool.execute(new Responder<>(socket, clientAddress, new Notification("У команды " + commandName + " не может быть аргументов!", null, true)));
            logger.error("Command \"" + commandName + "\" doesn't have any arguments!");
            return false;
        } return true;
    }
}
