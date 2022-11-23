package Commands;

import Classes.Command;
import ServerPackages.Commander;
import java.io.IOException;

public class ShowCommand extends Command {
    private final Commander commander;

    public ShowCommand(String type, String[] args, Commander commander) {
        super(type, args);
        this.commander = commander;
    }

    @Override
    public void execute(String[] args) throws IOException {
        commander.show(args);
    }

    @Override
    public void execute(Object obj) throws NoSuchFieldException, IllegalAccessException, IOException {

    }
}
