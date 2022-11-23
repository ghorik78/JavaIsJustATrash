package Classes;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Notification implements Serializable {
    @Serial
    private static final long serialVersionUID = 16341261231512371L;
    private String text;
    private String[] args;
    private boolean isUserDataValid;
    private ArrayList<Route> collection;

    public Notification(String text, String[] args, boolean isUserDataValid) {
        this.text = text;
        this.args = args;
        this.isUserDataValid = isUserDataValid;
    }

    public Notification(String text, String[] args, boolean isUserDataValid, ArrayList<Route> collection) {
        this.text = text;
        this.args = args;
        this.isUserDataValid = isUserDataValid;
        this.collection = collection;
        System.out.println(collection);
    }

    public String getText() {
        return this.text;
    }
    public String[] getArgs() {
        return this.args;
    }
    public boolean getDataResult() { return this.isUserDataValid; }
    public ArrayList<Route> getCollection() { return this.collection; }

    public void setText(String text) {
        this.text = text;
    }
    public void setArgs(String[] args) { this.args = args; }
    public void setCollection(ArrayList<Route> collection) { this.collection = collection; }
}
