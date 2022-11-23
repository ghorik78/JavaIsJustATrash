package Commands;

import Classes.Command;

import java.io.IOException;
import java.io.Serial;

public class CommandToSend extends Command {
    @Serial
    private static final long serialVersionUID = 9696198273172851L;

    private final String type;
    private final String[] args;

    @Override
    public void execute(String[] args) throws IOException {

    }

    @Override
    public void execute(Object obj) throws NoSuchFieldException, IllegalAccessException, IOException {

    }

    public CommandToSend(String type, String[] args) {
        super(type, args);
        this.type = type;
        this.args = args;
    }
}