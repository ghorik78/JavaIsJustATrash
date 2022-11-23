package Commands;

import Classes.Command;
import ServerPackages.Commander;
import java.io.IOException;

public class RemoveAllByDistanceCommand extends Command {
    private final Commander commander;

    public RemoveAllByDistanceCommand(String type, String[] args, Commander commander) {
        super(type, args);
        this.commander = commander;
    }

    @Override
    public void execute(String[] args) throws IOException, ClassNotFoundException {
        commander.remove_all_by_distance(args);
    }

    @Override
    public void execute(Object obj) throws NoSuchFieldException, IllegalAccessException, IOException {

    }
}
