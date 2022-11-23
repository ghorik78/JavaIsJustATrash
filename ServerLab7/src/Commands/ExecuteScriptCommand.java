package Commands;

import Classes.Command;
import Classes.Route;
import ServerPackages.Commander;

import java.io.IOException;

public class ExecuteScriptCommand extends Command {
    private final Commander commander;

    public ExecuteScriptCommand(String type, String[] args, Commander commander) {
        super(type, args);
        this.commander = commander;
    }

    @Override
    public void execute(String[] args, String usr) {
        commander.execute_script(args, usr);
    }

    @Override
    public void execute(Route route, String[] args, String usr) throws NoSuchFieldException, IllegalAccessException, IOException {

    }

}
