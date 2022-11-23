package Classes;


import java.io.IOException;
import java.io.Serializable;

public abstract class Command implements Serializable {
    private String type;
    private String[] args;

    public abstract void execute(String[] args) throws IOException, ClassNotFoundException;
    public abstract void execute(Object obj) throws NoSuchFieldException, IllegalAccessException, IOException;

    public String getType() { return this.type; }
    public String[] getArgs() { return this.args; }

    public Command(String type, String[] args) {
        this.type = type;
        this.args = args;
    }
}
