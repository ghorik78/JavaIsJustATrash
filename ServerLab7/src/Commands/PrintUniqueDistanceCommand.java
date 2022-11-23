package Commands;

import Classes.Command;
import Classes.Route;
import ServerPackages.Commander;
import java.io.IOException;

public class PrintUniqueDistanceCommand extends Command {
    private final Commander commander;

    public PrintUniqueDistanceCommand(String type, String[] args, Commander commander) {
        super(type, args);
        this.commander = commander;
    }

    @Override
    public void execute(String[] args, String usr) throws IOException {
        commander.print_unique_distance(args, usr);
    }

    @Override
    public void execute(Route route, String[] args, String usr) throws NoSuchFieldException, IllegalAccessException, IOException {

    }
}
