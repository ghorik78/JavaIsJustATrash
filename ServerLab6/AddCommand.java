package Commands;

import Classes.Command;
import Classes.Route;
import ServerPackages.Commander;
import ServerPackages.Server;

import java.io.IOException;
import java.io.Serial;

public class AddCommand extends Command {
    @Serial
    private static final long serialVersionUID = 91273918720912L;
    private final Commander commander;
    private Route route;

    public AddCommand(String type, String[] args, Commander commander) {
        super(type, args);
        this.commander = commander;
    }

    @Override
    public void execute(String[] args) {

    }

    @Override
    public void execute(Object obj) {
        try {
            commander.add(obj);
        } catch (NoSuchFieldException | IllegalAccessException | IOException e) {
            System.out.println("Ошибка при выполнении команды add: " + e.getMessage());
        }
    }

    public Route getRoute() { return this.route; }

    public void setRoute(Route route) { this.route = route; }
}
