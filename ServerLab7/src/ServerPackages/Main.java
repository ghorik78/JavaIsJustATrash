package ServerPackages;

import Classes.Route;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.concurrent.*;

public class Main {

    public static final ConcurrentLinkedDeque<Route> collection = new ConcurrentLinkedDeque<>();
    public static final Commander commander = new Commander();
    public static final SQLManager manager = new SQLManager();
    public static final ConcurrentHashMap<String, InetSocketAddress> connectedUsers = new ConcurrentHashMap<>();

    public static final ForkJoinPool requestsPool = new ForkJoinPool();
    public static final ExecutorService queryPool = Executors.newCachedThreadPool();
    public static final ExecutorService answerPool = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException, IllegalAccessException {
        MultiThreadedServer server = new MultiThreadedServer(59812);
        manager.connectToDatabase();

        while (true) {
            DatagramPacket dp = requestsPool.invoke(new Receiver(server.getSocket()));
            queryPool.execute(new QueryChecker(dp, server.getSocket(), answerPool));
        }
    }
}
