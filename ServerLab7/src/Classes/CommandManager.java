package Classes;

import java.io.IOException;
import java.sql.SQLException;

public class CommandManager {
    public <T extends Command> void invoke(T obj, Route route, String[] args, String usr) throws NoSuchFieldException, IllegalAccessException, IOException {
        obj.execute(route, args, usr);
    }

    public <T extends Command> void invoke(T obj, String[] args, String usr) throws IOException, SQLException {
        obj.execute(args, usr);
    }
}
