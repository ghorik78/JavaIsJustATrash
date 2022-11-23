package ServerPackages;

import Classes.Command;
import Classes.Notification;
import Commands.AddCommand;
import Classes.Invoker;
import Commands.CommandToSend;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

import static ServerPackages.Commander.logger;


public class Server {
    public static final HashMap<String, Method> commands = new HashMap<>();
    private static final Invoker invoker = new Invoker();
    private static final SQLManager manager = new SQLManager();
    private static final ForkJoinPool pool = new ForkJoinPool();

    private HashMap<String, InetSocketAddress> connectedUsers;

    static DatagramSocket datagramSocket;
    static InetAddress client; static int port = 59812;

    static {
        for (Method m : Invoker.class.getDeclaredMethods()) {
            commands.put(m.getName(), m);
        }
    }

    public void connect() throws IOException {
        datagramSocket = new DatagramSocket(port);
        logger.info("Server is prepared.");
    }

    public byte[] receive() throws IOException, ClassNotFoundException {
        byte[] buffer = new byte[32768];
        DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
        datagramSocket.receive(dp);
        client = dp.getAddress();
        port = dp.getPort();
        logger.info("An object received from the user.");
        return buffer;
    }

    public void checkQuery(byte[] buffer) throws IOException {

        ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(buffer));
        Command command = null; String commandName = null; String[] args = null;
        String usrLogin = null; String usrPassword; boolean isUserDataValid = false;

        try {
            command = (Command) inputStream.readObject();
            commandName = command.getType();
            args = command.getArgs();

            if (commandName.equals("register")) manager.registerUser(command.getUsrLogin(), command.getUsrPassword(), new InetSocketAddress(InetAddress.getLocalHost(), port));
            else {
                usrLogin = command.getUsrLogin();
                usrPassword = command.getUsrPassword();
                isUserDataValid = manager.checkUserData(usrLogin, usrPassword, new InetSocketAddress(InetAddress.getLocalHost(), port));
            }
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
            sendAnswer(new Notification("Выполнение такой команды невозможно.", null, false));
            logger.error("Unknown command received");
        }

        if (isUserDataValid) {
            if (commandName.equals("add")) {
                try {
                    invoker.add(((AddCommand) command).getRoute(), new String[]{}, usrLogin);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    System.out.println("Ошибка при выполнении команды: " + e.getMessage());
                    sendAnswer(new Notification("Такой команды нет. Пользуйтесь командой help.", null, true));
                    logger.error("Error during command execution: " + commandName);
                }
            } else {
                try {
                    Method m = commands.get(commandName);
                    m.invoke(invoker, (Object) args);
                } catch (IllegalAccessException | InvocationTargetException | NullPointerException e) {
                    System.out.println("Ошибка при выполнении команды: " + e.getMessage());
                    sendAnswer(new Notification("Такой команды нет. Пользуйтесь командой help.", null, true));
                    logger.error("Error during command execution: " + commandName);
                }
            }
            inputStream.close();
        } else {
            sendAnswer(new Notification("По введённым данным не удалось войти.", null, false));
        }

    }

    public void sendAnswer(Object answer) throws IOException {
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        ObjectOutput objectOutput = new ObjectOutputStream(bStream);
        objectOutput.writeObject(answer);
        objectOutput.close();

        byte[] serAnsw = bStream.toByteArray();
        DatagramPacket dp = new DatagramPacket(serAnsw, serAnsw.length, client, port);
        datagramSocket.send(dp);
        logger.info("A response was sent to the user.");
    }

    public void getConsoleCommand() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Команда серверу: ");
        String line = scanner.nextLine();

        String[] parts = line.split(" ");
        String commandName = parts[0].toLowerCase();
        String[] commandArgs = Arrays.copyOfRange(parts, 1, parts.length);

        if (commandName.equals("exit")) {
            if (commandArgs.length != 0) {
                System.out.println("У данной команды не может быть аргумнетов!");
                logger.error("Server couldn't have any arguments!");
            } else {
                Commander commander = new Commander();
                System.out.println("Завершение работы сервера.");
                logger.info("Shutting down...");
                System.exit(1);
            }
        } else System.out.println("Такой команды не существует.");
    }

}
