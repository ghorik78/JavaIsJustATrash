package Commands;

import Classes.Command;
import Classes.Route;
import java.io.IOException;
import java.io.Serial;

public class AddCommand extends Command {
    @Serial
    private static final long serialVersionUID = 91273918720912L;
    private String type;
    private String[] args;
    private Route route;


    @Override
    public void execute(String[] args, String usr) throws IOException {

    }

    @Override
    public void execute(Route route, String[] args, String usr) throws NoSuchFieldException, IllegalAccessException, IOException {

    }

    public String getType() { return this.type; }
    public String[] getArgs() { return this.args; }
    public Route getRoute() { return this.route; }

    public void setRoute(Route route) { this.route = route; }

    public AddCommand(String type, String[] args) {
        super(type, args);
    };

    public AddCommand(String type, String[] args, String usrLogin, String usrPassword) {
        super(type, args, usrLogin, usrPassword);
        this.type = type;
        this.args = args;
    }

}
