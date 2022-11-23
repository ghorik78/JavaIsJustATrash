package Commands;

import Classes.Command;
import Classes.Route;

import java.io.IOException;
import java.io.Serial;

public class CommandToSend extends Command {
    @Serial
    private static final long serialVersionUID = 9696198273172851L;

    private String type;
    private String usrLogin;
    private String usrPassword;
    private Route route;

    public Route getRoute() { return this.route; }
    public void setRoute(Route route) { this.route = route; }


    @Override
    public void execute(String[] args, String usr) {

    }

    @Override
    public void execute(Route route, String[] args, String usr) throws NoSuchFieldException, IllegalAccessException, IOException {

    }

    public String getUsrLogin() {
        return usrLogin;
    }

    public String getUsrPassword() {
        return usrPassword;
    }

    public CommandToSend(String type, String[] args, String usrLogin, String usrPassword) {
        super(type, args, usrLogin, usrPassword);
        this.type = type;
        this.usrLogin = usrLogin;
        this.usrPassword = usrPassword;
    }
}
