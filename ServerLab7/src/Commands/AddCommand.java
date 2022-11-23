package Commands;

import Classes.Command;
import Classes.Route;
import ServerPackages.Commander;

import java.io.IOException;
import java.io.Serial;

public class AddCommand extends Command {
    @Serial
    private static final long serialVersionUID = 91273918720912L;
    private final Commander commander;
    private Route route;

    public Route getRoute() { return this.route; }

    public void setRoute(Route route) { this.route = route; }

    public AddCommand(String type, String usr, Commander commander) {
        super(type, usr);
        this.commander = commander;
    }

    @Override
    public void execute(String[] args, String usr) {}

    @Override
    public void execute(Route route, String[] args, String usr) {
        try {
            commander.add(route, args,  usr);
        } catch (NoSuchFieldException | IllegalAccessException | IOException e) {
            System.out.println("Ошибка при выполнении команды add: " + e.getMessage());
        }
    }


}
