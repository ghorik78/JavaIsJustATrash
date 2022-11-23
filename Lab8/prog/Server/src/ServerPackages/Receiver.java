package ServerPackages;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.RecursiveTask;

public class Receiver extends RecursiveTask<DatagramPacket> {
    private DatagramSocket socket;

    public Receiver(DatagramSocket socket) {
        this.socket = socket;
    }

    @Override
    protected DatagramPacket compute() {
        byte[] received = new byte[32768];
        try {
            DatagramPacket dp = new DatagramPacket(received, received.length);
            socket.receive(dp);
            return dp;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
