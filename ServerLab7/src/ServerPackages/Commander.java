package ServerPackages;

import Classes.*;
import Interfaces.CommandData;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.*;
import java.lang.reflect.Method;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Commander {
    public static final Logger logger = LogManager.getLogger(Commander.class);
    public static ConcurrentLinkedDeque<Route> collection = Main.collection;
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

    public void help(String[] args, String usr) throws IOException {
        if (isArgsEmpty("help", args)) {
            String msg = "";
            for (Method m : Invoker.class.getDeclaredMethods()) {
                if (m.isAnnotationPresent(CommandData.class)) {
                    CommandData cmd = m.getAnnotation(CommandData.class);
                    msg = msg.concat(cmd.name() + " : " + cmd.description() + "\n");
                }
            }
            answerPool.execute(new Responder<>(socket, clientAddress, new Notification(msg, null, true)));
            logger.info("Command \"help\" has been executed");
        } else {
            logger.warn("Command \"help\" doesn't have arguments!");
        }
    }

    public void info(String[] args, String usr) throws IOException {
        if (isArgsEmpty("info", args)) {
            String msg = "";
            msg = msg.concat("Тип коллекции: " + collection.getClass().getSimpleName() + "\n" +
                    "Дата инициализации: " + initDate + "\n" +
                    "Количество элементов в данный момент: " + (long) collection.size() + "\n" +
                    "Тип хранимых объектов: Route" + "\n");
            logger.info("Command \"info\" has been executed.");
            answerPool.execute(new Responder<>(socket, clientAddress, new Notification(msg, null, true)));
        } else {
            logger.warn("Command \"info\" doesn't have arguments!");
        }
    }

    public void show(String[] args, String usr) throws IOException {
        if (isArgsEmpty("show", args)) {
            fillCollectionFromDB();
            if (Main.collection.size() != 0) {
                ArrayList<String> msg = new ArrayList<>();
                collection.forEach(route -> msg.add(route.toString()));
                answerPool.execute(new Responder<>(socket, clientAddress, new Notification(String.join("", msg), null, true)));
                logger.info("Command \"show\" has been executed.");
            } else {
                answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Коллекция пуста.", null, true)));
                logger.warn("Collection is empty.");
            }
        } else {
            logger.warn("Show command doesn't have arguments!");
        }
    }

    public void add(Route receivedRoute, String[] args, String usr) throws NoSuchFieldException, IllegalAccessException, IOException {
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
                collection.add(receivedRoute);
                logger.info("Route " + receivedRoute.getName() + " has been added.");
                sortCollectionByName();
                answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Объект успешно добавлен в базу данных. Его id: " + setId, null, true)));
                logger.info("Command \"add\" has been executed.");
            } catch (SQLException e) {
                System.out.println("Ошибка при получении объекта из БД: " + e.getMessage());
                answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Ошибка при получении объекта из БД.", null, true)));
            }
        } else {
            answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Ошибка во время добавления объекта в БД.", null, true)));
            logger.error("An error during adding object into database.");
        }

    }

    public void update(Route routeToChange, String[] args, String usr) {
        collection.clear();
        fillCollectionFromDB();
        if (Pattern.compile("\\d+").matcher(args[0]).matches()) {
            Long id = Long.parseLong(args[0]);
            Optional<Route> optionalRoute = collection.stream().filter(route -> Objects.equals(route.getId(), id)).findFirst();
            Route routeToUpdate = optionalRoute.orElse(null);
            if (routeToUpdate == null) {
                answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Объект с таким id отсутствует!", new String[]{"0"}, true)));
                logger.warn("Route with id " + id + " doesn't exists!");
            }
            else if (!routeToUpdate.getOwner().equals(usr)) {
                answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Нельзя менять объекты, которые вам не принадлежат!", new String[]{"0"}, true)));
            }
            else {
                collection.remove(routeToUpdate);
                try {
                    if (manager.updateRouteInDB(id, routeToChange.getName(), routeToChange.getCoordinates().getX(), routeToChange.getCoordinates().getY(),
                            routeToChange.getCreationDate(), routeToChange.getFrom().getX(), routeToChange.getFrom().getY(),
                            routeToChange.getFrom().getName(), routeToChange.getTo().getX(), routeToChange.getTo().getY(),
                            routeToChange.getTo().getZ(), routeToChange.getDistance())) {
                        collection.add(routeToChange);
                        answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Объект успешно обновлён.", null, true)));
                    } else {
                        answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Не удалось обновить объект.", null, true)));
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
        collection.clear();
        fillCollectionFromDB();
        if (!Pattern.compile("\\d+").matcher(args[0]).matches()) {
            answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Id не был введён или введён неправильно!", null, true)));
            logger.error("Invalid id for \"remove_by_id\" command!");
        }
        else {
            Long id = Long.parseLong(args[0]);
            Optional<Route> optionalRoute = collection.stream().filter(route -> Objects.equals(route.getId(), id)).findFirst();
            Route routeToRemove = optionalRoute.orElse(null);
            if (routeToRemove == null) {
                answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Объект с таким id отсутствует!", null, true)));
                logger.error("Route with id " + id + " doesn't exists!");
            } else if (!routeToRemove.getOwner().equals(usr)) {
                answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Нельзя изменять объекты, которые вам не принадлежат!", null, true)));
                logger.error("Can't modify this object. " + usr + " isn't owner.");
            }
            else {
                if (manager.removeRouteFromDBbyId(id)) {
                    collection.remove(routeToRemove);
                    logger.info("Route was removed.");
                    answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Объект был удалён.", null, true)));
                } else {
                    logger.error("An error during removing object from DB.");
                    answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Ошибка во время удаления.", null, true)));
                }
            }
        }
        logger.info("Command \"remove_by_id\" has been executed.");
    }

    public void clear(String[] args, String usr) {
        if (args.length != 0) {
            answerPool.execute(new Responder<>(socket, clientAddress, new Notification("У данной команды не может быть аргументов!", null, true)));
            logger.error("Clear command doesn't have arguments!");
        }
        else {
            fillCollectionFromDB();
            if (manager.countUserObjects(usr) == 0) {
                answerPool.execute(new Responder<>(socket, clientAddress, new Notification("У вас нет объектов.", null, true)));
            } else if (manager.removeRouteFromDBbyOwner(usr)) {
                answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Все ваши объекты были удалены.", null, true)));
                logger.info("Command \"clear\" has been executed. User: " + usr);
            }
        }
    }

    public void execute_script(String[] args, String usr) {}

    public void remove_first(String[] args, String usr) {
        if (args.length != 0) {
            answerPool.execute(new Responder<>(socket, clientAddress, new Notification("У данной команды не может быть аргументов!", null, true)));
            logger.error("Command \"remove_first\" doesn't have arguments!");
        }
        else {
            fillCollectionFromDB();
            if (manager.countUserObjects(usr) > 0) {
                Route firstUserObj = collection.stream().filter(route -> route.getOwner().equals(usr)).findFirst().get();
                if (manager.removeRouteFromDBbyId(firstUserObj.getId())) {
                    fillCollectionFromDB();
                    answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Первый элемент был удалён.", null, true)));
                    logger.info("Command \"remove_first\" has been executed.");
                }
            } else {
                answerPool.execute(new Responder<>(socket, clientAddress, new Notification("У вас нет объектов.", null, true)));
                logger.warn("User " + usr + " doesn't have any objects.");
            }
        }
    }

    public void head(String[] args, String usr) {
        if (args.length != 0) {
            answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Данная команда не может иметь аргументов!", null, true)));
            logger.error("Head command doesn't have arguments!");
        } else if (manager.countUserObjects(usr) > 0) {
            fillCollectionFromDB();
            sortCollectionByName();
            Route firstRoute = collection.stream().filter(route -> route.getOwner().equals(usr)).findFirst().get();
            answerPool.execute(new Responder<>(socket, clientAddress, new Notification(firstRoute.toString(), null, true)));
            logger.info("Command \"head\" has been executed.");
        } else {
            answerPool.execute(new Responder<>(socket, clientAddress, new Notification("У вас нет объектов.", null, true)));
            logger.warn("User " + usr + " doesn't have any objects.");
        }

    }

    public void add_if_max(Route routeToAdd, String[] args, String usr) {
        String name = args[0];
        sortCollectionByName();
        fillCollectionFromDB();
        if (manager.countUserObjects(usr) > 0) {
            ConcurrentLinkedDeque<Route> temporaryColl = collection.stream().filter(route -> route.getOwner().equals(usr)).collect(Collectors.toCollection(ConcurrentLinkedDeque::new));
            temporaryColl = temporaryColl.stream().sorted(Comparator.comparing(Route::getName)).collect(Collectors.toCollection(ConcurrentLinkedDeque::new));
            Route maxRouteName = temporaryColl.getFirst();
            if (name.compareTo(maxRouteName.getName()) < 0) {
                logger.info("Condition for adding if met.");
                if (manager.insertRouteIntoDB(routeToAdd, usr)) {
                    fillCollectionFromDB();
                    answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Объект успешно добавлен.", null, true)));
                    logger.info("Object has been added by " + usr);
                }
                else {
                    answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Ошибка во время добавления объекта в БД.", null, true)));
                    logger.error("An error during adding an object by " + usr);
                }
            } else {
                answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Условие для добавления не выполнено.", null, true)));
                logger.error("Condition for adding isn't met.");
            }
        }
    }

    public void remove_all_by_distance(String[] args, String usr) {
        if (args.length != 1 || !Pattern.compile("\\d").matcher(args[0]).matches()) {
            answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Неправильно введны аргументы команды!", null, true)));
            logger.error("Command \"remove_all_by_distance\" has incorrect argument(s)!");
        } else {
            if (manager.countUserObjects(usr) > 0) {
                manager.removeRouteFromDBbyDistance(Long.parseLong(args[0]));
                fillCollectionFromDB();
                answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Объекты с distance = " + args[0] + "были удалены", null, true)));
                logger.info("Command \"remove_all_by_distance\" has been executed.");
            } else {
                answerPool.execute(new Responder<>(socket, clientAddress, new Notification("У вас нет объектов.", null, true)));
            }
        }
    }

    public void print_unique_distance(String[] args, String usr) throws IOException {
        if (isArgsEmpty("print_unique_distance", args)) {
            ArrayList<Long> allDistances = new ArrayList<>();
            fillCollectionFromDB();
            collection.forEach(route -> allDistances.add(route.getDistance()));
            ArrayList<String> msg = allDistances.stream().distinct().map(String::valueOf).collect(Collectors.toCollection(ArrayList::new));
            answerPool.execute(new Responder<>(socket, clientAddress, new Notification(String.join("\n", msg), null, true)));
            logger.info("Command \"print_unique_distance\" has been executed.");
        }
    }

    public void print_field_ascending_distance(String[] args, String usr) throws IOException {
        if (isArgsEmpty("print_field_ascending_distance", args)) {
            ArrayList<Long> sortedDistances = new ArrayList<>();
            fillCollectionFromDB();
            collection.stream().distinct().forEach(route -> sortedDistances.add(route.getDistance()));
            sortedDistances.sort(Long::compareTo);
            answerPool.execute(new Responder<>(socket, clientAddress, new Notification(String.join("\n", sortedDistances.toString()), null, true)));
            logger.info("Command \"print_unique_distance\" has been executed.");
        }
    }

    public void sortCollectionByName() {
        collection = collection.stream().sorted(Comparator.comparing(Route::getName)).collect(Collectors.toCollection(ConcurrentLinkedDeque::new));
        logger.info("Collection has been sorted.");
    }

    public synchronized void fillCollectionFromDB() {
        collection.clear();
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
                collection.add(routeToAdd);
                logger.info("Object with id " + routeToAdd.getId() + " has been added to the collection.");
            }
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
