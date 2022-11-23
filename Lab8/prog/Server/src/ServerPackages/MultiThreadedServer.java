package ServerPackages;

import Classes.Invoker;
import Commands.CommandToSend;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

public class MultiThreadedServer implements Runnable {
    private int port;
    private final DatagramSocket socket; private InetSocketAddress clientAddress;
    private DatagramPacket identifyDp;
    private SQLManager manager;
    public static final ForkJoinPool requestsPool = new ForkJoinPool();
    private final ExecutorService cachedPool = Executors.newCachedThreadPool();
    public static final ExecutorService answerPool = Executors.newFixedThreadPool(10);
    public static final HashMap<String, Method> commands = new HashMap<>();

    static {
        for (Method m : Invoker.class.getDeclaredMethods()) {
            commands.put(m.getName(), m);
        }
    }

    public MultiThreadedServer(int port) throws SocketException {
        this.port = port;
        socket = new DatagramSocket(port);
    }

    public MultiThreadedServer(DatagramSocket socket, DatagramPacket dp) {
        this.socket = socket;
        this.identifyDp = dp;
        this.clientAddress = new InetSocketAddress(dp.getAddress(), dp.getPort());
        manager = new SQLManager(socket, clientAddress);
    }

    public DatagramSocket getSocket() { return this.socket; }

    public DatagramPacket receive() throws IOException {
        byte[] dpData = new byte[32768];
        DatagramPacket dp = new DatagramPacket(dpData, dpData.length);
        socket.receive(dp);
        return dp;
    }

    @Override
    public void run() {
        try {
            manager.connectToDatabase();
            cachedPool.execute(new QueryChecker(identifyDp, socket, answerPool));
        } catch (IOException | ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                System.out.println("trying to receive");
                DatagramPacket dp = requestsPool.invoke(new Receiver(socket));
                cachedPool.execute(new QueryChecker(dp, socket, answerPool));
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isAlreadyConnected(ConcurrentHashMap<String, InetSocketAddress> db, String usr) {
        return db.get(usr) != null;
    }

    public CommandToSend getCommandFromDp(DatagramPacket dp) throws IOException, ClassNotFoundException {
        byte[] data = dp.getData();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        return (CommandToSend) ois.readObject();
    }
}
