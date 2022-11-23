package Commands;

import Classes.Command;
import Classes.ObjectColor;
import Classes.Route;

import java.io.IOException;
import java.io.Serial;
import java.sql.SQLException;

public class IdentifyColorCommand extends Command {
    @Serial
    private static final long serialVersionUID = 9182749812315124L;

    private String type;
    private String[] args;
    private String usrLogin;
    private String usrPassword;
    private ObjectColor objectColor;

    public IdentifyColorCommand(String type, String[] args, String usrLogin, String usrPassword) {
        super(type, args, usrLogin, usrPassword);
        this.type = type;
        this.usrLogin = usrLogin;
        this.usrPassword = usrPassword;
    }

    @Override
    public void execute(String[] args, String usr) throws IOException, SQLException {

    }

    @Override
    public void execute(Route route, String[] args, String usr) throws NoSuchFieldException, IllegalAccessException, IOException {

    }

    public ObjectColor getObjectColor() { return objectColor; }
    public void setObjectColor(ObjectColor objectColor) { this.objectColor = objectColor; }
}
