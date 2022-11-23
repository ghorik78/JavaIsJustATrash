package Commands;

import Classes.Command;
import Classes.ObjectColor;
import Classes.Route;
import ServerPackages.Commander;

import java.io.IOException;
import java.io.Serial;
import java.sql.SQLException;

public class IdentifyColorCommand extends Command {
    @Serial
    private static final long serialVersionUID = 9182749812315124L;

    private String type;
    private String usrLogin;
    private String usrPassword;
    private ObjectColor objectColor;
    private Commander commander;

    public IdentifyColorCommand(String type, String[] args, String usrLogin, Commander commander) {
        super(type, args);
        this.type = type;
        this.usrLogin = usrLogin;
        this.commander = commander;
    }

    public IdentifyColorCommand(String type, String[] args, ObjectColor objectColor) {
        super(type, args);
        this.type = type;
        this.objectColor = objectColor;
    }

    @Override
    public void execute(String[] args, String usr) throws IOException, SQLException {
        commander.identifyColor(args, usr);
    }

    @Override
    public void execute(Route route, String[] args, String usr) throws NoSuchFieldException, IllegalAccessException, IOException {

    }

    public ObjectColor getObjectColor() { return objectColor; }
    public void setObjectColor(ObjectColor objectColor) { this.objectColor = objectColor; }
}
