package Commands;

import Classes.Command;
import ServerPackages.Commander;

import java.io.IOException;

public class PrintFieldAscendingDistanceCommand extends Command {
    private final Commander commander;

    public PrintFieldAscendingDistanceCommand(String type, String[] args, Commander commander) {
        super(type, args);
        this.commander = commander;
    }

    @Override
    public void execute(String[] args) throws IOException, ClassNotFoundException {
        commander.print_field_ascending_distance(args);
    }

    @Override
    public void execute(Object obj) throws NoSuchFieldException, IllegalAccessException, IOException {

    }
}
