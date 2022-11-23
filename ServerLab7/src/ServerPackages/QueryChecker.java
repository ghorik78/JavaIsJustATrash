package ServerPackages;

import Classes.Command;
import Classes.Invoker;
import Classes.Notification;
import Commands.AddCommand;
import Commands.UpdateCommand;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import static ServerPackages.Commander.logger;
import static ServerPackages.Main.queryPool;
import static ServerPackages.Main.requestsPool;

public class QueryChecker implements Runnable {
    private String commandName;
    private String[] commandArgs;
    private String usrLogin;
    private String usrPassword;
    private boolean isLegit;
    private Command receivedQuery;

    private Invoker invoker;
    private Commander commander;
    private SQLManager manager;

    private ExecutorService answerPool;

    private DatagramSocket socket;
    private InetSocketAddress clientAddress;

    public QueryChecker(DatagramPacket dp, DatagramSocket socket, ExecutorService answerPool) throws IOException, ClassNotFoundException {
        byte[] data = dp.getData();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Command receivedCommand = (Command) ois.readObject();

        this.commandName = receivedCommand.getType();
        this.commandArgs = receivedCommand.getArgs();
        this.usrLogin = receivedCommand.getUsrLogin();
        this.usrPassword = receivedCommand.getUsrPassword();
        this.receivedQuery = receivedCommand;
        this.socket = socket;
        this.clientAddress = new InetSocketAddress(dp.getAddress(), dp.getPort());
        this.answerPool = answerPool;

        commander = new Commander(socket, clientAddress, usrLogin);
        invoker = new Invoker(commander);
        manager = new SQLManager(socket, clientAddress);
    }

    public boolean isAlreadyConnected(ConcurrentHashMap<String, InetSocketAddress> db, String usr) {
        return db.get(usr) != null;
    }

    public void checkQuery() throws IOException, SQLException, NoSuchFieldException, IllegalAccessException {
        switch (commandName) {
            case "register":
                manager.registerUser(usrLogin, usrPassword, clientAddress);
                break;
            case "logIn":
                if (isAlreadyConnected(Main.connectedUsers, usrLogin)) {
                    isLegit = false;
                    answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Обнаружен одновременный вход с разных устройств. Авторизуйтесь заново!", null, false)));
                }
                if (manager.checkUserData(usrLogin, usrPassword, clientAddress)) {
                    answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Вход выполнен успешно.", null, true)));
                    Main.connectedUsers.put(usrLogin, clientAddress);
                }
                break;
            case "disconnect":
                if (isAlreadyConnected(Main.connectedUsers, usrLogin))
                    Main.connectedUsers.remove(usrLogin);
                break;
        }

        isLegit = manager.checkUserData(usrLogin, usrPassword, clientAddress);

        if (isLegit) {
            if (commandName.equals("add")) {
                try {
                    invoker.add(((AddCommand) receivedQuery).getRoute(), commandArgs, usrLogin);
                } catch (NoSuchFieldException | IllegalAccessException | IOException e) {
                    answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Ошибка при добавлении объекта.", null, true)));
                    logger.error("Error during command execution: " + commandName);
                }
            } else if (commandName.equals("add_if_max")) {
                invoker.add_if_max(((AddCommand) receivedQuery).getRoute(), commandArgs, usrLogin);
            }
            else if (commandName.equals("update")) {
                try {
                    invoker.update(((UpdateCommand) receivedQuery).getRoute(), commandArgs, usrLogin);
                } catch (SQLException e) {
                    answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Ошибка при обновлении объекта.", null, true)));
                    logger.error("Error during command execution: " + commandName);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            else if (!commandName.equals("register") && !commandName.equals("logIn")) {
                try {
                    Method m = MultiThreadedServer.commands.get(commandName);
                    m.invoke(invoker, (Object) commandArgs);
                } catch (InvocationTargetException | IllegalAccessException | NullPointerException e) {
                    System.out.println("Ошибка при выполнении команды: " + commandName + " : " + e.getMessage());
                    answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Выполнение этой команды невозможно.", null, true)));
                    logger.error("Error during command execution: " + commandName);
                }
            }
        } else {
            answerPool.execute(new Responder<>(socket, clientAddress, new Notification("Отказано в доступе. Пройдите авторизацию снова.", null ,false)));
        }

    }

    @Override
    public void run() {
        try {
            checkQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
