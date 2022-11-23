package Commands;

import Classes.Command;
import Classes.Route;

import java.io.IOException;
import java.io.Serial;

public class UpdateCommand extends Command {
    @Serial
    private static final long serialVersionUID = 5123098598716212L;
    private Route route;

    public Route getRoute() { return this.route; }
    public void setRoute(Route route) { this.route = route; }

    public UpdateCommand(String type, String[] args, String usrLogin, String usrPassword) {
        super(type, args, usrLogin, usrPassword);
    }

    @Override
    public void execute(String[] args, String usr) throws IOException {

    }

    @Override
    public void execute(Route route, String[] args, String usr) throws NoSuchFieldException, IllegalAccessException, IOException {

    }

}
