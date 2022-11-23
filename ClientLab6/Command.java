package Classes;

import java.io.*;

public abstract class Command implements Serializable {
    private String type;
    private String[] args;
    private String usrLogin;
    private String usrPassword;

    public abstract void execute(String[] args) throws IOException;
    public abstract void execute(Object obj, String args) throws NoSuchFieldException, IllegalAccessException, IOException;

    public String getType() { return this.type; }
    public String[] getArgs() { return this.args; }
    public String getUsrLogin() { return this.usrLogin; }
    public String getUsrPassword() { return this.usrPassword; }

    public Command(String type, String[] args, String usrLogin, String usrPassword) {
        this.type = type;
        this.args = args;
        this.usrLogin = usrLogin;
        this.usrPassword = usrPassword;
    }

    public Command(String type, String[] args) {
        this.type = type;
        this.args = args;
    }

    public Command(String type, String usr) {
        this.type = type;
        this.usrLogin = usr;
    }
}
