package Commands;

import Classes.Command;
import Classes.Route;
import ServerPackages.Commander;
import java.io.IOException;
import java.io.Serial;

public class UpdateCommand extends Command {
    @Serial
    private static final long serialVersionUID = 5123098598716212L;
    private final Commander commander;
    private Route route;

    public Route getRoute() { return this.route; }
    public void setRoute(Route route) { this.route = route; }

    public UpdateCommand(String type, String[] args, Commander commander) {
        super(type, args);
        this.commander = commander;
    }

    @Override
    public void execute(String[] args, String usr) throws IOException {

    }

    @Override
    public void execute(Route obj, String[] args, String usr) throws NoSuchFieldException, IllegalAccessException, IOException {
        commander.update(obj, args, usr);
    }
}
