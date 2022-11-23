package Exceptions;

//UNCHECKED
public class InteractionException extends NullPointerException {
    public String errObj;
    public InteractionException(String message, String errObj) {
        super(message);
        this.errObj = errObj;
    }

    public String getErrObj() {
        return "Проблемы с объектом: " + this.errObj;
    }

    public void killProcess() {
        System.exit(1);
    }
}
