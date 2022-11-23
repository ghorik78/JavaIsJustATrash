package Classes;

import java.io.IOException;

public class CommandManager {
    public <T extends Command> void invoke(T obj, String[] args) {
        try {
            obj.execute(args);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public <T extends Command> void invoke(T obj, Route route) throws NoSuchFieldException, IllegalAccessException, IOException {
        obj.execute(route);
    }
}
