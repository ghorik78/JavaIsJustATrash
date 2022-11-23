package Commands;

import Classes.Command;
import ServerPackages.Commander;

import java.io.IOException;

public class ExecuteScriptCommand extends Command {
    private final Commander commander;

    public ExecuteScriptCommand(String type, String[] args, Commander commander) {
        super(type, args);
        this.commander = commander;
    }

    @Override
    public void execute(String[] args) throws IOException, ClassNotFoundException {
        commander.execute_script(args);
    }

    @Override
    public void execute(Object obj) throws NoSuchFieldException, IllegalAccessException, IOException {

    }
}
