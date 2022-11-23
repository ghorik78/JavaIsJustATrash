package Classes;

import java.io.Serial;
import java.io.Serializable;

public class Notification implements Serializable {
    @Serial
    private static final long serialVersionUID = 16341261231512371L;
    private String text;
    private String[] args;
    private boolean isUserDataValid;

    public Notification(String text, String[] args, boolean isUserDataValid) {
        this.text = text;
        this.args = args;
        this.isUserDataValid = isUserDataValid;
    }

    public String getText() {
        return this.text;
    }
    public String[] getArgs() {
        return this.args;
    }
    public boolean getDataResult() { return this.isUserDataValid; }

    public void setText(String text) {
        this.text = text;
    }
    public void setArgs(String[] args) { this.args = args; }
}
