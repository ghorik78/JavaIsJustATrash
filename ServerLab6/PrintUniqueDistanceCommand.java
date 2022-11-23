package Commands;

import Classes.Command;
import ServerPackages.Commander;
import java.io.IOException;

public class PrintUniqueDistanceCommand extends Command {
    private final Commander commander;

    public PrintUniqueDistanceCommand(String type, String[] args, Commander commander) {
        super(type, args);
        this.commander = commander;
    }

    @Override
    public void execute(String[] args) throws IOException, ClassNotFoundException {
        commander.print_unique_distance(args);
    }

    @Override
    public void execute(Object obj) throws NoSuchFieldException, IllegalAccessException, IOException {

    }
}
