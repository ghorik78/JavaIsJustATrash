package Commands;

import Classes.Command;

import java.io.IOException;
import java.io.Serial;

public class CommandToSend extends Command {
    @Serial
    private static final long serialVersionUID = 9696198273172851L;

    private final String usrLogin;
    private final String usrPassword;

    @Override
    public void execute(String[] args) throws IOException {

    }

    @Override
    public void execute(Object obj, String usr) throws NoSuchFieldException, IllegalAccessException, IOException {

    }

    public String getUsrLogin() {
        return usrLogin;
    }

    public String getUsrPassword() {
        return usrPassword;
    }

    public CommandToSend(String type, String[] args, String usrLogin, String usrPassword) {
        super(type, args, usrLogin, usrPassword);
        this.usrLogin = usrLogin;
        this.usrPassword = usrPassword;
    }
}
