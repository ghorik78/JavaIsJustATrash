package ServerPackages;

import Classes.Command;
import Classes.Notification;
import Commands.AddCommand;
import Classes.Invoker;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import static ServerPackages.Commander.logger;


public class Server {
    public static final HashMap<String, Method> commands = new HashMap<>();
    private static final Invoker invoker = new Invoker();
    static DatagramSocket datagramSocket;
    static InetAddress client; static int port = 50001;

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
        byte[] buffer = new byte[65536];
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
        try {
            command = (Command) inputStream.readObject();
            logger.info("Command received: " + command.getType());

            commandName = command.getType();
            args = command.getArgs();
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
            sendAnswer(new Notification("Notification", new String[] {"Выполнение такой команды невозможно."}));
            logger.error("Unknown command received: " + commandName);
        }

        if (commandName.equals("add")) {
            try {
                AddCommand addCommand = (AddCommand) command;
                Method m = invoker.getClass().getMethod("add", addCommand.getRoute().getClass());
                m.invoke(invoker, addCommand.getRoute());
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                System.out.println("Ошибка при выполнении команды: " + e.getMessage());
                sendAnswer(new Notification("Такой команды нет. Пользуйтесь командой help.", null));
                logger.error("Error during command execution: " + commandName);
            }
        } else {
            try {
                Method m = commands.get(commandName);
                m.invoke(invoker, (Object) args);
            } catch (IllegalAccessException | InvocationTargetException | NullPointerException e) {
                System.out.println("Ошибка при выполнении команды: " + e.getMessage());
                sendAnswer(new Notification("Такой команды нет. Пользуйтесь командой help.", null));
                logger.error("Error during command execution: " + commandName);
            }
        }
        inputStream.close();
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

        if (commandName.equals("save")) {
            if (commandArgs.length != 0) {
                System.out.println("У данной команды не может быть аргумнетов!");
                logger.error("Server couldn't have any arguments!");
            }
            else {
                Commander commander = new Commander();
                commander.save(commandArgs);
            }
        } else if (commandName.equals("exit")) {
            if (commandArgs.length != 0) {
                System.out.println("У данной команды не может быть аргумнетов!");
                logger.error("Server couldn't have any arguments!");
            } else {
                Commander commander = new Commander();
                commander.save(commandArgs);
                System.out.println("Завершение работы сервера.");
                logger.info("Shutting down...");
                System.exit(1);
            }
        } else System.out.println("Такой команды не существует.");
    }
}
