package Commands;

import Classes.Command;
import ServerPackages.Commander;
import java.io.IOException;

public class RemoveByIdCommand extends Command {
    private final Commander commander;

    public RemoveByIdCommand(String type, String[] args, Commander commander) {
        super(type, args);
        this.commander = commander;
    }

    @Override
    public void execute(String[] args) throws IOException, ClassNotFoundException {
        commander.remove_by_id(args);
    }

    @Override
    public void execute(Object obj) throws NoSuchFieldException, IllegalAccessException, IOException {

    }
}
