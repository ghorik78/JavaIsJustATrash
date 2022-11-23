package ClassesForApp;

import ClassesForColl.Coordinates;
import ClassesForColl.LocationFrom;
import ClassesForColl.LocationTo;
import ClassesForColl.Route;
import Interfaces.Command;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.security.AnyTypePermission;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;

import static ClassesForApp.Main.objects;

public class Commander {

    public void serializeRoute(Route obj) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(Main.database, true));
        XStream xStream = new XStream();
        xStream.alias("Route", Route.class);
        xStream.alias("Coordinates", Coordinates.class);

        bos.write(xStream.toXML(obj).getBytes(StandardCharsets.UTF_8));
        bos.write("\n".getBytes(StandardCharsets.UTF_8));
        bos.flush();
        bos.close();
        System.out.println("Сериализация объекта с ID " + obj.getId() + " выполнена!");
    }

    public void deserializeRoute(String filename) throws IOException, IllegalAccessException, NoSuchFieldException {
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
                        objects.add(newObject);
                        objects = helper.sortCollection(objects);
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

    @Command(name = "help",
            description = "вывод всех доступных команд")
    public void help(String[] args) {
        if (args.length != 0) {
            System.out.println("Данная команда не содержит аргументов!");
        }
        else {
            for (Method m : this.getClass().getDeclaredMethods()) {
                if (m.isAnnotationPresent(Command.class)) {
                    Command command = m.getAnnotation(Command.class);
                    System.out.println(command.name() + " : " + command.description());
                }
            }
        }
    }

    @Command(name = "info",
            description = "вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)")
    public void info(String[] args) {
        if (args.length != 0) System.out.println("Данная команда не содержит аргументов!");
        else {
            System.out.println("Тип коллекции: " + objects.getClass());
            System.out.println("Дата инициализации: " + Main.collCrDate);
            System.out.println("Количество элементов на данный момент: " + objects.size());
            System.out.println("Тип хранимых в коллекции объектов: Route");
        }
    }

    @Command(name = "show",
            description = "вывести в стандартный поток вывода все элементы коллекции в строковом представлении")
    public void show(String[] args) throws IllegalAccessException {
        if (args.length != 0) System.out.println("Данная команда не имеет аргументов! Попробуйте ещё раз: ");
        else {
            if (objects.size() != 0) {
                long totalObjects = 1;
                for (Route route : objects) {
                    System.out.println("\nОбъект " + totalObjects + "\n");
                    for (Field f : route.getClass().getDeclaredFields()) {
                        f.setAccessible(true);

                        if (f.getType().getSimpleName().equals("Coordinates") || f.getType().getSimpleName().equals("LocationFrom") || f.getType().getSimpleName()
                                .equals("LocationTo")) {
                            Object tmp = (Object) f.get(route);
                            if (f.getType().getSimpleName().equals("Coordinates")) System.out.println("Coordinates:");
                            else if (f.getType().getSimpleName().equals("LocationFrom")) System.out.println("LocationFrom:");
                            else System.out.println("LocationTo:");

                            for (Field subField : tmp.getClass().getDeclaredFields()) {
                                subField.setAccessible(true);
                                System.out.println("\t" + subField.getName() + ": " + subField.get(tmp));
                            }
                        } else System.out.println(f.getName() + ": " + f.get(route));
                    }
                    totalObjects++;
                }

            } else System.out.println("Коллекция пуста..");
        }
    }

    @Command(name = "add",
            description = "добавить элемент в коллекцию")
    public void add(String[] args) throws NoSuchFieldException, IllegalAccessException {
        if (args.length != 0) System.out.println("У этой команды нет аргументов!");
        else {
            Scanner scanner = new Scanner(System.in);
            Route routeToAdd = new Route();
            Coordinates coordinates = new Coordinates();
            LocationFrom locationFrom = new LocationFrom();
            LocationTo locationTo = new LocationTo();

            String inputArg = "";
            System.out.println("По очереди введите значения всех полей: ");

            for (int i = 0; i < 11; ++i) {
                switch (i) {
                    case 0 -> {
                        routeToAdd.setId();
                    }
                    case 1 -> {
                        System.out.println("Введите имя объекта: ");
                        inputArg = scanner.nextLine();
                        while (!Pattern.compile("\\S+").matcher(inputArg).matches()) {
                            System.out.println("Значение имени введено неправильно! Попробуйте ещё раз: ");
                            inputArg = scanner.nextLine();
                        }
                        routeToAdd.setName(inputArg);
                    }
                    case 2 -> {
                        System.out.println("Введите координату х: ");
                        inputArg = scanner.nextLine();
                        while (!Pattern.compile("-*\\d+").matcher(inputArg).matches()) {
                            System.out.println("Значение координаты Х введено неправильно! Попробуйте ещё раз: ");
                            inputArg = scanner.nextLine();
                        }
                        coordinates.setX(Long.parseLong(inputArg));
                    }
                    case 3 -> {
                        System.out.println("Введите координату y: ");
                        inputArg = scanner.nextLine();
                        while (!Pattern.compile("-*\\d+").matcher(inputArg).matches()) {
                            System.out.println("Значение координаты Y введено неправильно! Попробуйте ещё раз: ");
                            inputArg = scanner.nextLine();
                        }
                        coordinates.setY(Long.parseLong(inputArg));
                        routeToAdd.setCoordinates(coordinates);
                    }
                    case 4 -> {
                        System.out.println("Введите начальную координату х: ");
                        inputArg = scanner.nextLine();
                        while (!Pattern.compile("-*\\d+").matcher(inputArg).matches()) {
                            System.out.println("Значение начальной координаты X введено неправильно! Попробуйте ещё раз: ");
                            inputArg = scanner.nextLine();
                        }
                        locationFrom.setX(Integer.parseInt(inputArg));
                    }
                    case 5 -> {
                        System.out.println("Введите начальную координату y: ");
                        inputArg = scanner.nextLine();
                        while (!Pattern.compile("-*\\d+").matcher(inputArg).matches()) {
                            System.out.println("Значение начальной координаты Y ведено неправильно! Попробуйте ещё раз: ");
                            inputArg = scanner.nextLine();
                        }
                        locationFrom.setY(Long.parseLong(inputArg));
                    }
                    case 6 -> {
                        System.out.println("Введите название: ");
                        inputArg = scanner.nextLine();
                        while (!Pattern.compile(".+").matcher(inputArg).matches()) {
                            System.out.println("Введённая строка с названием не может быть пустой! Попробуйте ещё раз: ");
                            inputArg = scanner.nextLine();
                        }
                        locationFrom.setName(inputArg);

                        routeToAdd.setFrom(locationFrom);
                    }
                    case 7 -> {
                        System.out.println("Введите конечную координату х: ");
                        inputArg = scanner.nextLine();
                        if (inputArg.equals("")) {
                            try {
                                locationTo.setX(null);
                            } catch (NullPointerException | InputMismatchException e) {
                                System.out.println("Полю с конечной координатой X будет присвоено значние null.");
                            }
                        }
                        else {
                            while (!Pattern.compile("-*\\d*\\.*\\d*").matcher(inputArg).matches()) {
                                System.out.println("Значение конечной координаты X введено неправильно! Попробуйте ещё раз: ");
                                inputArg = scanner.nextLine();
                            }
                            locationTo.setX(Double.parseDouble(inputArg));
                        }
                    }
                    case 8 -> {
                        System.out.println("Введите конечную координату y: ");
                        inputArg = scanner.nextLine();
                        while (!Pattern.compile("-*\\d+\\.*\\d*").matcher(inputArg).matches()) {
                            System.out.println("Значение конечной координаты Y введено неправильно! Попробуйте ещё раз: ");
                            inputArg = scanner.nextLine();
                        }
                        locationTo.setY(Float.parseFloat(inputArg));
                    }
                    case 9 -> {
                        System.out.println("Введите конечную координату z: ");
                        inputArg = scanner.nextLine();
                        if (inputArg.equals("")) {
                            try {
                                locationTo.setZ(null);
                            } catch (NullPointerException | InputMismatchException e) {
                                System.out.println("Полю со значением конечной координаты Z будет присвоено значние null.");
                            }
                        }
                        else {
                            while (!Pattern.compile("-*\\d*\\.*\\d*").matcher(inputArg).matches()) {
                                System.out.println("Значение конечной координаты Z введено неправильно! Попробуйте ещё раз: ");
                                inputArg = scanner.nextLine();
                            }
                            locationTo.setZ(Long.parseLong(inputArg));
                        }
                        routeToAdd.setTo(locationTo);
                    }
                    case 10 -> {
                        System.out.println("Введите расстояние: ");
                        inputArg = scanner.nextLine();
                        while (!Pattern.compile("\\d+").matcher(inputArg).matches()) {
                            System.out.println("Значение расстояния введено неправильно! Попробуйте ещё раз: ");
                            inputArg = scanner.nextLine();
                        }
                        routeToAdd.setDistance(Long.parseLong(inputArg));
                    }
                    default -> System.out.println("Что-то пошло не так.");
                }
            }

            routeToAdd.setCreationDate(LocalDate.now());
            objects.addFirst(routeToAdd);

            CollectionHelper helper = new CollectionHelper();
            objects = helper.sortCollection(objects);

            System.out.println("Объект был успешно добавлен в коллекцию!");
        }
    }

    @Command(name = "update",
            description = "обновить значение элемента коллекции, id которого равен заданному")
    public void update(String[] args) throws NoSuchFieldException, IllegalAccessException {
        if (args.length == 0 || !Pattern.compile("-*\\d+").matcher(String.valueOf(args[0])).matches()) System.out.println("Не был введён ID объекта! Попробуйте ещё раз: ");
        else {
            if (objects.size() != 0) {
                Long idBuffer = 0L;
                CollectionHelper helper = new CollectionHelper();

                Route updatedObj = helper.getObjectById(objects, Long.parseLong(args[0]));

                if (updatedObj != null) {
                    System.out.println("Объект получен.");
                    idBuffer = updatedObj.getId();
                    String[] arg = new String[0];
                    add(arg);
                    updatedObj = objects.getFirst();
                    Field id = updatedObj.getClass().getDeclaredField("id");
                    id.setAccessible(true);
                    id.set(updatedObj, idBuffer);
                } else System.out.println("Такого объекта в коллекции нет.");
            } else System.out.println("Коллекция пуста.");
        }
    }

    @Command(name = "remove_by_id",
            description = "удалить элемент из коллекции по его id")
    public void removeById(String[] args) throws NoSuchFieldException, IllegalAccessException {
        if (args.length == 0 || !Pattern.compile("-*\\d+").matcher(args[0]).matches()) System.out.println("Не был введён ID объекта! Попробуйте ещё раз: ");
        else if (objects.size() != 0) {
            CollectionHelper helper = new CollectionHelper();
            Long objID = Long.parseLong(args[0]);
            objects.remove(helper.getObjectById(objects, objID));
            System.out.println("Объект был удалён.");
        } else System.out.println("Коллекция пуста");
    }

    @Command(name = "clear",
            description = "очистить все элементы коллекции")
    public void clear(String[] args) {
        objects.clear();
        System.out.println("Коллекция очищена.");
    }

    @Command(name = "save",
            description = "сохранить коллекцию в файл")
    public void save(String[] args) throws IOException, IllegalAccessException{
        FileWriter fw = new FileWriter(Main.database);
        fw.write("");
        fw.flush();
        fw.close();
        if (objects.size() != 0) {
            System.out.println("Файл очищен. Начата запись в файл новых объектов коллекции.");
            for (Route route : objects) serializeRoute(route);
        } else System.out.println("Коллекция пуста.");
    }

    @Command(name = "execute_script",
            description = "считать и исполнить скрипт из указанного файла")
    public void executeScript(String[] args) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        if (args.length == 0 || !Pattern.compile("[a-zA-Z0-9]+.[a-zA-Z0-9]+").matcher(args[0]).matches())
            System.out.println("Имя файла не было введено, либо введено неверно! Попробуйте ещё раз: ");
        else {
            BufferedReader br = new BufferedReader(new FileReader(args[0]));
            CommandListener listener = new CommandListener();
            String line;
            while ((line = br.readLine()) != null) {
                String[] checkLine = line.split(" ");
                String command = checkLine[0];
                String[] commandArgs = Arrays.copyOfRange(checkLine, 1, checkLine.length);
                Method m = CommandListener.commands.get(command);

                System.out.println("Результат выполнения комманды " + command + ":");
                if (Pattern.compile(" *add *").matcher(command).matches() || Pattern.compile(" *update *").matcher(command).matches()) {
                    boolean ableToCreate = true;

                    Route route = new Route();
                    Coordinates coordinates = new Coordinates();
                    LocationFrom locationFrom = new LocationFrom();
                    LocationTo locationTo = new LocationTo();

                    String lineForCheck = "";
                    for (int i = 0; i < 12; ++i) {
                        if (i != 0) lineForCheck = br.readLine();
                        switch (i) {
                            case 0 -> {
                                if (Objects.equals(command, "update")) {
                                    if (Pattern.compile("-*\\d+").matcher(lineForCheck).matches()) {
                                        route.setIdManually(Long.parseLong(lineForCheck));
                                    } else {
                                        System.out.println("По данным для поля ID невозможно создать объект!");
                                        ableToCreate = false;
                                    }
                                } else {
                                    route.setId();
                                    System.out.println("ID был установлен автоматически. Он равен " + route.getId());
                                }

                            }
                            case 1 -> {
                                if (lineForCheck.equals("") || !Pattern.compile(".*").matcher(lineForCheck).matches()) {
                                    System.out.println("По данным для поля name невозможно создать объект!");
                                    ableToCreate = false;
                                }
                                else route.setName(lineForCheck);
                            }
                            case 2 -> {
                                if (lineForCheck.equals("") || !Pattern.compile("-*\\d+").matcher(lineForCheck).matches()) {
                                    System.out.println("По данным для поля coordinates невозможно создать объект!");
                                    ableToCreate = false;
                                } else coordinates.setX(Long.parseLong(lineForCheck));
                            }
                            case 3 -> {
                                if (!Pattern.compile("-*\\d+").matcher(lineForCheck).matches()) {
                                    System.out.println("По данным для поля coordinates невозможно создать объект!");
                                    ableToCreate = false;
                                }
                                else {
                                    coordinates.setY(Long.parseLong(lineForCheck));
                                    route.setCoordinates(coordinates);
                                }
                            }
                            case 4 -> {
                                if (!Pattern.compile("\\d{4}\\-\\d{2}\\-\\d{2}").matcher(lineForCheck).matches()) {
                                    System.out.println("По данным для поля creationDate невозможно создать объект!");
                                    ableToCreate = false;
                                }
                                else {
                                    route.setCreationDate(LocalDate.parse(lineForCheck));
                                }
                            }
                            case 5 -> {
                                if (lineForCheck.equals("") || !Pattern.compile("-*\\d+").matcher(lineForCheck).matches()) {
                                    System.out.println("По данным для поля from невозможно создать объект!");
                                    ableToCreate = false;
                                }
                                else locationFrom.setX(Integer.parseInt(lineForCheck));
                            }
                            case 6 -> {
                                if (lineForCheck.equals("") || !Pattern.compile("-*\\d+").matcher(lineForCheck).matches()) {
                                    System.out.println("По данным для поля from невозможно создать объект!");
                                    ableToCreate = false;
                                }
                                else locationFrom.setY(Long.parseLong(lineForCheck));
                            }
                            case 7 -> {
                                if (lineForCheck.equals("") || !Pattern.compile(".*").matcher(lineForCheck).matches()) {
                                    System.out.println("По данным для поля from невозможно создать объект!");
                                    ableToCreate = false;
                                }
                                else {
                                    locationFrom.setName(lineForCheck);
                                    route.setFrom(locationFrom);
                                }
                            }
                            case 8 -> {
                                if (!Pattern.compile("-*\\d*\\.*\\d*").matcher(lineForCheck).matches()) {
                                    System.out.println("По данным для поля to невозможно создать объект!");
                                    ableToCreate = false;
                                }
                                else locationTo.setX(Double.parseDouble(lineForCheck));
                            }
                            case 9 -> {
                                if (lineForCheck.equals("") || !Pattern.compile("-*\\d*\\.*\\d*").matcher(lineForCheck).matches()) {
                                    System.out.println("По данным для поля to невозможно создать объект!");
                                    ableToCreate = false;
                                }

                                else locationTo.setY(Float.parseFloat(lineForCheck));
                            }
                            case 10 -> {
                                if (!Pattern.compile("-*\\d*\\.*\\d*").matcher(lineForCheck).matches()) {
                                    System.out.println("По данным для поля to невозможно создать объект!");
                                    ableToCreate = false;
                                }
                                else {
                                    locationTo.setZ(Long.parseLong(lineForCheck));
                                    route.setTo(locationTo);
                                }
                            }
                            case 11 -> {
                                if (!Pattern.compile("\\d+").matcher(lineForCheck).matches() || Long.parseLong(lineForCheck) <= 1) {
                                    System.out.println("По данным для поля distance невозможно создать объект!");
                                    ableToCreate = false;
                                } else route.setDistance(Long.parseLong(lineForCheck));
                            }
                        }
                    }
                    if (ableToCreate) {
                        objects.add(route);
                        System.out.println("Объект из файла был создан.");
                    } else System.out.println("Объект из файла не был создан.");
                }
                if (!Objects.equals(command, "add") && !Objects.equals(command, "update")) {
                    try {
                        m.invoke(this, (Object) commandArgs);
                    } catch (NullPointerException e) {
                        System.out.println("Неправильная комманда в файле!");
                    }
                }
                System.out.println("\n");
            }
        }
    }

    @Command(name = "exit",
            description = "завершение программы без сохранения")
    public void exit(String[] args) {
        if (args.length != 0) System.out.println("Данная команда не содержит аргументов!");
        else {
            System.out.println("Завершение без сохранения.");
            System.exit(1);
        }
    }

    @Command(name = "remove_first",
            description = "удалить первый элемент из коллекции")
    public void removeFirst(String[] args) {
        objects.pollFirst();
        System.out.println("Первый объект был удалён.");
    }

    @Command(name = "head",
            description = "вывести первый элемент коллекции")
    public void head(String[] args) throws IllegalAccessException {
        if (objects.size() != 0) {
            Route firstObj = objects.peekFirst();
            System.out.println("Первый элемент коллекции: ");
            for (Field f : firstObj.getClass().getDeclaredFields()) {
                f.setAccessible(true);

                if (f.getType().getSimpleName().equals("Coordinates") || f.getType().getSimpleName().equals("LocationFrom") || f.getType().getSimpleName()
                        .equals("LocationTo")) {
                    Object tmp = (Object) f.get(firstObj);
                    if (f.getType().getSimpleName().equals("Coordinates")) System.out.println("Coordinates:");
                    else if (f.getType().getSimpleName().equals("LocationFrom")) System.out.println("LocationFrom:");
                    else System.out.println("LocationTo:");

                    for (Field subField : tmp.getClass().getDeclaredFields()) {
                        subField.setAccessible(true);
                        System.out.println("\t" + subField.getName() + ": " + subField.get(tmp));
                    }
                } else System.out.println(f.getName() + ": " + f.get(firstObj));
            }
        }
        else System.out.println("Коллекция пуста.");
    }

    @Command(name = "add_if_max",
    description = "добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции")
    public void addIfMax(String[] args) throws NoSuchFieldException, IllegalAccessException {
        if (args.length == 0 || !Pattern.compile("-*\\d+").matcher(args[0]).matches()) System.out.println("Не было введено значение distance! Попробуйте ещё раз: ");
        else {
            long maxId = 0;
            for (Route route : objects) {
                Field f = route.getClass().getDeclaredField("id");
                f.setAccessible(true);
                Long routeId = (Long) f.get(route);
                if (routeId > maxId) maxId = routeId;
            }
            if (Long.parseLong(args[0]) > maxId) {
                long bufferId = Long.parseLong(args[0]);
                String[] arg = new String[0];
                add(arg);
                Route updatedObj = objects.getFirst();
                Field id = updatedObj.getClass().getDeclaredField("id");
                id.set(updatedObj, bufferId);
                System.out.println("Объект с ID " + args[0] + " был успешно добавлен.");
            } else {
                System.out.println("Введённое значение не превышает значение максимального. Объект не был добавлен.");
            }
        }
    }

    @Command(name = "remove_all_by_distance",
    description = "удалить из коллекции все элементы, значение поля distance которого эквивалентно заданному")
    public void removeAllByDistance(String[] args) throws NoSuchFieldException, IllegalAccessException {
        if (args.length == 0 || !Pattern.compile("-*\\d+").matcher(args[0]).matches()) System.out.println("Не было введено значение distance! Попробуйте ещё раз: ");
        else {
            int totalDeleted = 0;
            for (Route route : objects) {
                Field distance = route.getClass().getDeclaredField("distance");
                distance.setAccessible(true);

                if ((Long) distance.get(route) == Long.parseLong(args[0])) {
                    objects.remove(route);
                    totalDeleted += 1;
                }
            }
            if (totalDeleted != 0) System.out.println("Все объекты с distance, равным " + args[0] + " были удалены. Таких объектов было " + totalDeleted + ".");
            else System.out.println("Объектов с distance, равным " + args[0] + " не оказалось.");
        }
    }

    @Command(name = "print_unique_distance",
            description = "вывести уникальные значения поля distance всех элементов в коллекции")
    public void printUniqueDistance(String[] args) throws NoSuchFieldException, IllegalAccessException {
        ArrayList<Long> uniqueDistances = new ArrayList<>();

        for (Route obj : objects) {
            Field distance = obj.getClass().getDeclaredField("distance");
            distance.setAccessible(true);
            long distanceToCheck = (long) distance.get(obj);
            if (!uniqueDistances.contains(distanceToCheck)) uniqueDistances.add(distanceToCheck);
        }

        System.out.println("Все уникальные расстояния в коллекции: ");
        for (Long dist : uniqueDistances) System.out.println(dist);
    }

    @Command(name = "print_field_ascending_distance",
            description = "вывести значения поля distance всех элементов в порядке возрастания")
    public void printFieldAscendingDistance(String[] args) throws NoSuchFieldException, IllegalAccessException {
        ArrayList<Long> sortedDistances = new ArrayList<>();

        for (Route obj : objects) {
            Field dist = obj.getClass().getDeclaredField("distance");
            dist.setAccessible(true);
            long distToAdd = (long) dist.get(obj);
            sortedDistances.add(distToAdd);
        }

        Collections.sort(sortedDistances);

        System.out.println("Distance всех элементов в порядке возрастания: ");
        for (Long dist : sortedDistances) {
            System.out.println(dist);
        }
    }

}
