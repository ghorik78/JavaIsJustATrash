package Commands;

import Classes.Command;
import ServerPackages.Commander;

import java.io.IOException;

public class HeadCommand extends Command {
    private final Commander commander;

    public HeadCommand(String type, String[] args, Commander commander) {
        super(type, args);
        this.commander = commander;
    }

    @Override
    public void execute(String[] args) throws IOException, ClassNotFoundException {
        commander.head(args);
    }

    @Override
    public void execute(Object obj) throws NoSuchFieldException, IllegalAccessException, IOException {

    }
}
