package ServerPackages;

import Classes.*;
import Interfaces.CommandData;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.security.AnyTypePermission;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;



public class Commander {
    public static final Logger logger = LogManager.getLogger(Commander.class);
    public static ArrayDeque<Route> collection = new ArrayDeque<>();
    static LocalDate initDate = LocalDate.now();

    Server server = new Server();

    public void help(String[] args) throws IOException {
        if (isArgsEmpty("help", args)) {
            String msg = "";
            for (Method m : Invoker.class.getDeclaredMethods()) {
                if (m.isAnnotationPresent(CommandData.class)) {
                    CommandData cmd = m.getAnnotation(CommandData.class);
                    msg = msg.concat(cmd.name() + " : " + cmd.description() + "\n");
                }
            }
            server.sendAnswer(new Notification(msg, null));
            logger.info("Command \"help\" has been executed");
        } else {
            server.sendAnswer(new Notification("У данной команды не может быть аргументов!", null));
            logger.warn("Command \"help\" doesn't have arguments!");
        }
    }

    public void info(String[] args) throws IOException {
        if (isArgsEmpty("info", args)) {
            try {
                String msg = "";
                msg = msg.concat("Тип коллекции: " + collection.getClass().getSimpleName() + "\n" +
                        "Дата инициализации: " + initDate + "\n" +
                        "Количество элементов в данный момент: " + collection.stream().count() + "\n" +
                        "Тип хранимых объектов: Route" + "\n");
                logger.info("Command \"info\" has been executed.");
                server.sendAnswer(new Notification(msg, null));
            } catch (IOException e) {
                System.out.println("Ошибка при выполнении команды info: " + e.getMessage());
                logger.error("Error during command execution: info");
            }
        } else {
            server.sendAnswer(new Notification("У данной команды не может быть аргументов!", null));
            logger.warn("Command \"info\" doesn't have arguments!");
        }
    }

    public void show(String[] args) throws IOException {
        if (isArgsEmpty("show", args)) {
            if (collection.size() != 0) {
                ArrayList<String> msg = new ArrayList<>();
                collection.forEach(route -> msg.add(route.toString()));
                server.sendAnswer(new Notification(String.join("", msg), null));
            } else {
                server.sendAnswer(new Notification("Коллекция пуста", null));
                logger.warn("Collection is empty.");
            }
        } else {
            server.sendAnswer(new Notification("У данной команды не может быть аргументов!", null));
            logger.warn("Show command doesn't have arguments!");
        }
        logger.info("Command \"show\" has been executed.");
    }

    public void add(Object obj) throws NoSuchFieldException, IllegalAccessException, IOException {
        Route receivedRoute = (Route) obj;
        logger.info("New route received. Name of received object: " + receivedRoute.getName());
        receivedRoute.setId();
        collection.add(receivedRoute);
        logger.info("Route " + receivedRoute.getName() + " has been added.");
        server.sendAnswer(new Notification("Объект успешно добавлен. Его id: " + receivedRoute.getId() + "\n", null));
        logger.info("Command \"add\" has been executed.");
    }

    public void update(String[] args) throws IOException {
        if (Pattern.compile("\\d+").matcher(args[0]).matches()) {
            Long id = Long.parseLong(args[0]);
            Optional<Route> optionalRoute = collection.stream().filter(route -> Objects.equals(route.getId(), id)).findFirst();
            Route routeToUpdate = optionalRoute.orElse(null);
            if (routeToUpdate == null) {
                server.sendAnswer(new Notification("Объект с таким id отсутствует в коллекции!", new String[]{"0"}));
                logger.warn("Route with id " + id + " doesn't exists!");
            }
            else {
                server.sendAnswer(new Notification("Объект был найден.", new String[]{"1"}));
                collection.remove(routeToUpdate);
                logger.info("Old route has been removed.");
                try {
                    byte[] serializedRoute = server.receive();
                    logger.info("New route has been received.");
                    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(serializedRoute));
                    Route routeToChange = (Route) ois.readObject();
                    routeToChange.setIdManually(id);
                    collection.addLast(routeToChange);
                    logger.info("Route was updated.");
                } catch (IOException | ClassNotFoundException e) {
                    server.sendAnswer(new Notification("Ошибка при выполнении команды update!", null));
                    logger.error("Error during execution command: update.");
                }
                server.sendAnswer(new Notification("Объект успешно обновлён", null));
            }
        } else {
            server.sendAnswer(new Notification("Аргумент не был введён или введён неправильно!", null));
            logger.error("Invalid argument for update command!");
        }
        logger.info("Command \"update\" has been executed.");
    }

    public void remove_by_id(String[] args) throws IOException {
        if (!Pattern.compile("\\d+").matcher(args[0]).matches()) {
            server.sendAnswer(new Notification("Id не был введён или введён неправильно!", null));
            logger.error("Invalid id for \"remove_by_id\" command!");
        }
        else {
            Long id = Long.parseLong(args[0]);
            Optional<Route> optionalRoute = collection.stream().filter(route -> Objects.equals(route.getId(), id)).findFirst();
            Route routeToRemove = optionalRoute.orElse(null);
            if (routeToRemove == null) {
                server.sendAnswer(new Notification("Объект с таким id отсутствует!", null));
                logger.error("Route with id " + id + " doesn't exists!");
            }
            else {
                collection.remove(routeToRemove);
                logger.info("Route was removed.");
                server.sendAnswer(new Notification("Объект был удалён.", null));
            }
        }
        logger.info("Command \"remove_by_id\" has been executed.");
    }

    public void clear(String[] args) throws IOException {
        if (args.length != 0) {
            server.sendAnswer(new Notification("У данной команды не может быть аргументов!", null));
            logger.warn("Clear command doesn't have arguments!");
        }
        else {
            if (collection.size() > 0) {
                collection.clear();
                server.sendAnswer(new Notification("Коллекция была очищена.", null));
                logger.info("Collection has been cleared.");
                logger.info("Command \"clear\" has been executed.");
            } else {
                server.sendAnswer(new Notification("Коллекция и так пуста.", null));
            }
        }
    }

    public void execute_script(String[] args) {}

    public void remove_first(String[] args) throws IOException {
        if (args.length != 0) {
            server.sendAnswer(new Notification("У данной команды не может быть аргументов!", null));
            logger.error("Command \"remove_first\" doesn't have arguments!");
        }
        else {
            if (collection.size() == 0) {
                server.sendAnswer(new Notification("Коллекция пуста.", null));
                logger.error("Collection is empty!");
            }
            else {
                Route firstRoute = collection.stream().findFirst().get();
                collection.remove(firstRoute);
                logger.info("First route has been removed");
                server.sendAnswer(new Notification("Первый элемент был удалён.", null));
                logger.info("Command \"remove_first\" has been executed.");
            }
        }
    }

    public void head(String[] args) throws IOException {
        if (args.length != 0) {
            server.sendAnswer(new Notification("Данная команда не может иметь аргументов!", null));
            logger.error("Head command doesn't have arguments!");
        } else if (collection.size() != 0) {
                Route route = collection.stream().findFirst().get();
                server.sendAnswer(new Notification(route.toString(), null));
                logger.info("Command \"head\" has been executed.");
        }

    }

    public void add_if_max(String[] args) throws IOException {
        String name = args[0];
        sortCollectionByName();
        Route maxRouteName = collection.getFirst();
        if (name.compareTo(maxRouteName.getName()) < 0) {
            logger.info("Condition for adding is met.");
            server.sendAnswer(new Notification("Условие для добавления выполнено.", new String[]{"1"}));
            try {
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(server.receive()));
                collection.addFirst((Route) ois.readObject());
                server.sendAnswer(new Notification("Объект был успешно добавлен.", null));
                logger.info("Command \"add_if_max\" has been executed.");
            } catch (ClassNotFoundException e) {
                logger.error("Error during execution command \"add_if_max\": class not found!");
                server.sendAnswer(new Notification("Ошибка во время выполнения команды \"add_if_max\"", null));
            }

        }
        else {
            logger.error("Condition for adding isn't met.");
            server.sendAnswer(new Notification("Условие для добавления не выполнено!", new String[]{"0"}));
        }
    }

    public void remove_all_by_distance(String[] args) throws IOException {
        if (args.length != 1) {
            server.sendAnswer(new Notification("Неправильно введны аргументы команды!", null));
            logger.error("Command \"remove_all_by_distance\" has incorrect argument(s)!");
        } else {
            collection.stream().filter(route -> route.getDistance() == Long.parseLong(args[0])).forEach(route -> collection.remove(route));
            server.sendAnswer(new Notification("Объекты с distance = " + args[0] + "были удалены", null));
            logger.info("Command \"remove_all_by_distance\" has been executed.");
        }
    }

    public void print_unique_distance(String[] args) throws IOException {
        if (isArgsEmpty("print_unique_distance", args)) {
            ArrayList<Long> allDistances = new ArrayList<>();
            collection.forEach(route -> allDistances.add(route.getDistance()));
            ArrayList<String> msg = allDistances.stream().distinct().map(String::valueOf).collect(Collectors.toCollection(ArrayList::new));
            server.sendAnswer(new Notification(String.join("\n", msg), null));
            logger.info("Command \"print_unique_distance\" has been executed.");
        }
    }

    public void print_field_ascending_distance(String[] args) throws IOException {
        if (isArgsEmpty("print_field_ascending_distance", args)) {
            ArrayList<Long> sortedDistances = new ArrayList<>();
            collection.stream().distinct().forEach(route -> sortedDistances.add(route.getDistance()));
            sortedDistances.sort(Long::compareTo);
            server.sendAnswer(new Notification(String.join("\n", sortedDistances.toString()), null));
            logger.info("Command \"print_unique_distance\" has been executed.");
        }
    }

    public void sortCollectionByName() {
        collection = collection.stream().sorted(Comparator.comparing(Route::getName)).collect(Collectors.toCollection(ArrayDeque::new));
        logger.info("Collection has been sorted.");
    }

    public void serializeRoute(Route obj) throws IOException {
        String filename = System.getenv("LABFILE");
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filename, true));
        XStream xStream = new XStream();
        xStream.alias("Route", Route.class);
        xStream.alias("Coordinates", Coordinates.class);


        bos.write(xStream.toXML(obj).getBytes(StandardCharsets.UTF_8));
        bos.write("\n".getBytes(StandardCharsets.UTF_8));
        bos.flush();
        bos.close();
        logger.info("Route with id " + obj.getId() + " has been serialized.");
    }

    public void deserializeRoutes(String filename) {
        int routesTotal = 0, addedTotal = 0;
        StringBuilder data = new StringBuilder();
        ArrayList<String> dataToSerialize = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));

            String line = "";

            while ((line = br.readLine()) != null) {
                data.append(line).append("\n");
                if (line.equals("</Route>")) {
                    dataToSerialize.add(String.valueOf(data));
                    data.delete(0, data.length());
                    routesTotal += 1;
                }
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Ошибка ввода/вывода: " + e.getMessage());
        }

        XStream xs = new XStream();
        xs.alias("Route", Route.class);
        xs.addPermission(AnyTypePermission.ANY);


        for (int i = 0; i < routesTotal; ++i) {
            try {
                Route newObject = (Route) xs.fromXML(dataToSerialize.get(i));

                CollectionHelper helper = new CollectionHelper();

                try {
                    if (helper.deserializedObjectChecker(newObject)) {
                        collection.add(newObject);
                        sortCollectionByName();
                        addedTotal++;
                    } else {
                        System.out.println("Объект с ID " + newObject.getId() + " не был добавлен, так как не удовлетворял требованиям!");
                    }
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    System.out.println(e.getMessage());
                }

            } catch (ConversionException e) {
                System.out.println("По данным из файла не может быть создан объект!");
            }

        }

        if (routesTotal - addedTotal > 0) System.out.println("Объектов, которые не могут быть созданы: " + (routesTotal - addedTotal));
        if (routesTotal == 0) System.out.println("Файл был пуст.");
        else if (addedTotal > 0) System.out.println("Десериализация всех объектов была выполнена успешно.\nБыло добавлено объектов: " + addedTotal);
        else System.out.println("Не было добавлено ни одного объекта.");
    }

    public boolean isArgsEmpty(String commandName, String[] args) throws IOException {
        if (args.length != 0) {
            server.sendAnswer(new Notification("У команды " + commandName + " не может быть аргументов!", null));
            logger.error("Command \"" + commandName + "\" doesn't have any arguments!");
            return false;
        } return true;
    }

    public void save(String[] args) {
        if (collection.size() != 0) {
            File file = new File(System.getenv("LABFILE"));
            file.delete();
            collection.forEach(route -> {
                try {
                    serializeRoute(route);
                    logger.info("Route with id " + route.getId() + " has been serialized.");
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("An error during serialization. Last id was: " + route.getId());
                }
            });
            System.out.println("Коллекция была сохранена в файл.");
        } else System.out.println("Коллекция пуста.");
    }


}
