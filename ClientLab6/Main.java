package ServerPackages;

import Classes.Command;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Client client = new Client();
        client.prepare();
        client.selectAuthMethod();
        while (client.isLegit()) {
            client.prepare();
            Command command = client.getCommandFromUser();
            try {
                client.checkCommand(command);
            } catch (Exception e) {
                System.out.println("Критическая ошибка во время выполнения.");
                System.exit(1);
            }
        }
    }
}
