package ClassesForApp;

import Interfaces.Command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class CommandListener {
    public static final HashMap<String, Method> commands = new HashMap<>();
    private static final Commander commander = new Commander();

    static {
        for (Method m : commander.getClass().getDeclaredMethods()) {
            if (m.isAnnotationPresent(Command.class)) {
                Command cmd = m.getAnnotation(Command.class);
                commands.put(cmd.name(), m);
            }
        }
    }

    public String receiveMsg() {
        Scanner in = new Scanner(System.in);
        return in.nextLine();
    }

    public void checkMsg(String msg) {
        try {
            String[] args = msg.split(" ");
            String command = args[0].toLowerCase();
            String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);
            Method m = commands.get(command);

            m.invoke(commander, (Object) commandArgs);
        } catch (NullPointerException | ArrayIndexOutOfBoundsException | IllegalAccessException | InvocationTargetException e) {
            System.out.println("Проверьте правильность введённой комманды, её аргументов и полей, если создавался объект. Пользуйтесь справкой help.");
        }
    }
}
