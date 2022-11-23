package ServerPackages;

import Classes.*;
import Commands.AddCommand;
import Commands.CommandToSend;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;


public class Client {
    private static final ClientSideCommands commander = new ClientSideCommands();
    private String login; private String password; private boolean isLegit = false;

    static DatagramChannel datagramChannel; static DatagramSocket ds; static DatagramPacket dp;
    static InetSocketAddress host; static int port = 59812;
    static byte[] arr; ByteBuffer buffer = ByteBuffer.allocate(65536);
    static Selector selector;

    public boolean isLegit() { return isLegit; }

    public void prepare() throws UnknownHostException {
        host = new InetSocketAddress(InetAddress.getLocalHost(), port);
        try {
            selector = Selector.open();
            datagramChannel = DatagramChannel.open();
            datagramChannel.configureBlocking(false);

            ds = datagramChannel.socket();
            datagramChannel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            System.out.println("Проблемы с созданием канала.");
        }
    }

    public void sendObject(Object obj) {
        try {
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(bStream);
            objectOutputStream.writeObject(obj);
            objectOutputStream.close();
            arr = bStream.toByteArray();
            datagramChannel.send(ByteBuffer.wrap(arr), host);
        } catch (IOException e) {
            System.out.println("Ошибка при отправке объекта: " + e.getMessage());
        }

    }

    public void showMsg(Notification msg) {
        try {
            System.out.println(msg.getText());
        } catch (Exception e) {
            System.out.println("Ответ не был получен. Возможно, сервер временно недоступен.");
        }

    }

    public Object receiveObject() throws IOException, ClassNotFoundException {
        buffer.clear();

        int n = selector.select(1000);

        if (n != 0) {
            Set<SelectionKey> readyKeys = selector.selectedKeys();

            for (SelectionKey key : readyKeys) {
                readyKeys.remove(key);

                if (key.isReadable()) {
                    datagramChannel = (DatagramChannel) key.channel();

                    datagramChannel.receive(buffer);

                    key.interestOps(SelectionKey.OP_READ);

                    buffer.flip();
                    byte[] receivedBytes = new byte[buffer.remaining()];
                    buffer.get(receivedBytes);
                    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(receivedBytes));
                    return ois.readObject();
                }
            }
        }
        return null;
    }

    public CommandToSend getCommandFromUser() {
        Scanner input = new Scanner(System.in);
        System.out.print("Введите команду и её аргументы, если они есть: ");

        try {
            String msg = input.nextLine();
            String[] parts = msg.split(" ");
            String commandName = parts[0].toLowerCase();
            String[] commandArgs = Arrays.copyOfRange(parts, 1, parts.length);
            return new CommandToSend(commandName, commandArgs, null, null);
        } catch (NullPointerException | ArrayIndexOutOfBoundsException | NoSuchElementException e) {
            System.out.println("Проверьте правильность введённой комманды, её аргументов и полей, если создавался объект. Пользуйтесь справкой help.");
        } return null;
    }

    public <T extends Command> void checkCommand(T command) throws IOException, ClassNotFoundException {
        switch (command.getType()) {
            case "exit":
                commander.exit(command.getArgs());
                break;
            case "add":
                try {
                    AddCommand addCommand = new AddCommand("add", null, login, password);
                    Route routeToSend = commander.add(new String[]{});
                    addCommand.setRoute(routeToSend);
                    sendObject(addCommand);
                    showMsg((Notification) receiveObject());
                } catch (ClassNotFoundException | IOException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case "update":
                if (command.getArgs().length == 0 || !Pattern.compile("\\d+").matcher(command.getArgs()[0]).matches()) {
                    System.out.println("Аргумент не был введён или введён неправильно!");
                } else {
                    sendObject(new CommandToSend("update", command.getArgs(), login, password));
                    Notification answer = (Notification) receiveObject();
                    showMsg(answer);
                    if (answer.getArgs()[0].equals("1")) {
                        Route routeToChange = commander.add(new String[]{});
                        sendObject(routeToChange);
                        showMsg((Notification) receiveObject());
                    }
                }
                break;
            case "execute_script":
                if (command.getArgs().length == 0 || !Pattern.compile("[a-zA-Z0-9]+\\.?[a-zA-Z0-9]+").matcher(command.getArgs()[0]).matches()) {
                    System.out.println("Имя файла не было введено или введено с ошибкой!");
                } else {
                    BufferedReader br = new BufferedReader(new FileReader(command.getArgs()[0]));
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] checkLine = line.split(" ");
                        String cmdFromFile = checkLine[0];
                        String[] cmdFromFileArgs = Arrays.copyOfRange(checkLine, 1, checkLine.length);

                        if (cmdFromFile.equals("execute_script") && cmdFromFileArgs[0].equals(command.getArgs()[0])) {
                            System.out.println("Команда execute_script с таким именем файла не может выполнить саму себя!");
                            continue;
                        }

                        if (Pattern.compile(" *add *").matcher(cmdFromFile).matches() || Pattern.compile(" *update *").matcher(cmdFromFile).matches()) {
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
                                        if (Objects.equals(cmdFromFile, "update")) {
                                            if (Pattern.compile("-*\\d+").matcher(cmdFromFileArgs[0]).matches()) {
                                                route.setIdManually(Long.parseLong(cmdFromFileArgs[0]));
                                            } else {
                                                System.out.println("По данному ID невозможно изменить объект объект!");
                                                ableToCreate = false;
                                            }
                                        } else {
                                            System.out.println("ID будет установлен на сервере");
                                        }
                                    }
                                    case 1 -> {
                                        if (lineForCheck.equals("") || !Pattern.compile(".*").matcher(lineForCheck).matches()) {
                                            System.out.println("По данным для поля name невозможно создать объект!");
                                            ableToCreate = false;
                                        } else route.setName(lineForCheck);
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
                                        } else {
                                            coordinates.setY(Long.parseLong(lineForCheck));
                                            route.setCoordinates(coordinates);
                                        }
                                    }
                                    case 4 -> {
                                        if (!Pattern.compile("\\d{4}\\-\\d{2}\\-\\d{2}").matcher(lineForCheck).matches()) {
                                            System.out.println("По данным для поля creationDate невозможно создать объект!");
                                            ableToCreate = false;
                                        } else {
                                            route.setCreationDate(LocalDate.parse(lineForCheck));
                                        }
                                    }
                                    case 5 -> {
                                        if (lineForCheck.equals("") || !Pattern.compile("-*\\d+").matcher(lineForCheck).matches()) {
                                            System.out.println("По данным для поля from невозможно создать объект!");
                                            ableToCreate = false;
                                        } else locationFrom.setX(Integer.parseInt(lineForCheck));
                                    }
                                    case 6 -> {
                                        if (lineForCheck.equals("") || !Pattern.compile("-*\\d+").matcher(lineForCheck).matches()) {
                                            System.out.println("По данным для поля from невозможно создать объект!");
                                            ableToCreate = false;
                                        } else locationFrom.setY(Long.parseLong(lineForCheck));
                                    }
                                    case 7 -> {
                                        if (lineForCheck.equals("") || !Pattern.compile(".*").matcher(lineForCheck).matches()) {
                                            System.out.println("По данным для поля from невозможно создать объект!");
                                            ableToCreate = false;
                                        } else {
                                            locationFrom.setName(lineForCheck);
                                            route.setFrom(locationFrom);
                                        }
                                    }
                                    case 8 -> {
                                        if (!Pattern.compile("-*\\d*\\.*\\d*").matcher(lineForCheck).matches()) {
                                            System.out.println("По данным для поля to невозможно создать объект!");
                                            ableToCreate = false;
                                        } else locationTo.setX(Double.parseDouble(lineForCheck));
                                    }
                                    case 9 -> {
                                        if (lineForCheck.equals("") || !Pattern.compile("-*\\d*\\.*\\d*").matcher(lineForCheck).matches()) {
                                            System.out.println("По данным для поля to невозможно создать объект!");
                                            ableToCreate = false;
                                        } else locationTo.setY(Float.parseFloat(lineForCheck));
                                    }
                                    case 10 -> {
                                        if (!Pattern.compile("-*\\d*\\.*\\d*").matcher(lineForCheck).matches()) {
                                            System.out.println("По данным для поля to невозможно создать объект!");
                                            ableToCreate = false;
                                        } else {
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
                                if (route.getId() == 0) {
                                    AddCommand addCommand = new AddCommand("add", new String[]{}, login, password);
                                    addCommand.setRoute(route);
                                    sendObject(addCommand);
                                    Notification answer = (Notification) receiveObject();
                                    showMsg(answer);
                                } else {
                                    sendObject(new CommandToSend("update", cmdFromFileArgs, login, password));
                                    showMsg((Notification) receiveObject());
                                    sendObject(route);
                                    showMsg((Notification) receiveObject());
                                }
                            } else System.out.println("Объект из файла не был создан.");
                        } else {
                            sendObject(new CommandToSend(cmdFromFile, cmdFromFileArgs, login, password));
                            Notification answer = (Notification) receiveObject();
                            showMsg(answer);
                        }
                    }
                }
                break;
            case "add_if_max":
                if (!Pattern.compile("\\S+").matcher(command.getArgs()[0]).matches())
                    System.out.println("Аргумент не был введён или введён неправильно!");
                else {
                    sendObject(new CommandToSend("add_if_max", command.getArgs(), login, password));
                    Notification answer = (Notification) receiveObject();
                    if (answer.getArgs()[0].equals("1")) {
                        Route newRoute = commander.add(new String[]{});
                        sendObject(newRoute);
                    }
                }
            default:
                sendObject(new CommandToSend(command.getType(), command.getArgs(), login, password));
                try {
                    showMsg((Notification) receiveObject());
                } catch (Exception e) {}
                break;
        }
    }

    public void logIn() throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Логин: ");
        login = scanner.nextLine();
        while (!Pattern.compile(".+").matcher(login).matches()) {
            System.out.println("Такой логин невозможен. Попробуйте снова.");
            System.out.println("Введите логин:");
            login = scanner.nextLine();
        }
        System.out.println("Пароль: ");
        password = scanner.nextLine();
        while (!Pattern.compile(".+").matcher(login).matches()) {
            System.out.println("Такой пароль невозможен. Попробуйте снова.");
            System.out.println("Введите пароль:");
            password = scanner.nextLine();
        }

        CommandToSend command = new CommandToSend("logIn", null, login, password);
        sendObject(command);
        Notification ans = (Notification) receiveObject();

        if (!ans.getDataResult()) {
            System.out.println("Зарегистрируйтесь.");
            register();
        } else {
            System.out.println("Вход выполнен успешно.");
            isLegit = true;
        }

    }

    public void register() throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        String pass1, pass2;
        System.out.println("Введите логин: ");
        login = scanner.nextLine();
        while (!Pattern.compile(".+").matcher(login).matches()) {
            System.out.println("Такой логин невозможен. Попробуйте снова.");
            login = scanner.nextLine();
        }
        System.out.println("Введите пароль: ");
        pass1 = scanner.nextLine();
        while (!Pattern.compile(".+").matcher(pass1).matches()) {
            System.out.println("Такой пароль невозможен. Попробуйте снова.");
            System.out.println("Введите пароль: ");
            pass1 = scanner.nextLine();
        }
        System.out.println("Подтвердите введённый пароль: ");
        pass2 = scanner.nextLine();
        if (!Objects.equals(pass1, pass2)) {
            System.out.println("Введённые пароли не совпадают. Попробуйте ещё раз.");
            register();
        }

        sendObject(new CommandToSend("register", null, login, pass2));
        Notification ans = (Notification) receiveObject();

        if (!ans.getDataResult()) System.out.println("Отказано в доступе.");
        else {
            System.out.println("Вход выполнен успешно.");
            isLegit = true;
        }

    }

    public void selectAuthMethod() throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Выберите метод авторизации (0 - вход в существующий аккаунт, 1 - регистрация): ");
        String authMethod = scanner.nextLine();
        while (!Pattern.compile("\\d+").matcher(authMethod).matches()) {
            System.out.println("Такой вариант ответа невозможен. Попробуйте снова.");
            selectAuthMethod();
        }
        switch (Integer.parseInt(authMethod)) {
            case 0:
                logIn();
                break;
            case 1:
                register();
                break;
            default:
                System.out.println("Такой вариант ответа невозможен. Попробуйте снова: ");
                selectAuthMethod();
        }
    }
}
