package Commands;

import Classes.Command;
import Classes.Route;
import ServerPackages.Commander;

import java.io.IOException;

public class ClearCommand extends Command {
    private final Commander commander;

    public ClearCommand(String type, String[] args, Commander commander) {
        super(type, args);
        this.commander = commander;
    }

    @Override
    public void execute(String[] args, String usr) throws IOException {
        commander.clear(args, usr);
    }

    @Override
    public void execute(Route route, String[] args, String usr) throws NoSuchFieldException, IllegalAccessException, IOException {

    }
}
