package ServerPackages;

import java.io.IOException;

public class Main {

    public static final Commander commander = new Commander();
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        Server server = new Server();
        server.connect();
        commander.deserializeRoutes(System.getenv("LABFILE"));

        Thread mainThread = new Thread(() -> {
            while (true) {
                commander.sortCollectionByName();
                try {
                    server.checkQuery(server.receive());
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread subThread = new Thread(() -> {
            while(true) {
                server.getConsoleCommand();
            }
        });

        mainThread.start();
        subThread.start();
    }
}
