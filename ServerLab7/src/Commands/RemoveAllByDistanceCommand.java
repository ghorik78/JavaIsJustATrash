package Commands;

import Classes.Command;
import Classes.Route;
import ServerPackages.Commander;
import java.io.IOException;

public class RemoveAllByDistanceCommand extends Command {
    private final Commander commander;

    public RemoveAllByDistanceCommand(String type, String[] args, Commander commander) {
        super(type, args);
        this.commander = commander;
    }

    @Override
    public void execute(String[] args, String usr) throws IOException {
        commander.remove_all_by_distance(args, usr);
    }

    @Override
    public void execute(Route route, String[] args, String usr) throws NoSuchFieldException, IllegalAccessException, IOException {

    }
}
