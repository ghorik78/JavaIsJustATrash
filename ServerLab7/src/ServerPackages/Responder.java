package ServerPackages;

import Classes.Notification;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class Responder <T extends Notification> implements Runnable {
    private final DatagramSocket ds;
    private final InetSocketAddress clientAddress;
    private final T objectToSend;

    public Responder(DatagramSocket ds, InetSocketAddress clientAddress, T objectToSend) {
        this.ds = ds;
        this.clientAddress = clientAddress;
        this.objectToSend = objectToSend;
    }

    public DatagramPacket sendAnswer() throws IOException {
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        ObjectOutput objectOutput = new ObjectOutputStream(bStream);
        objectOutput.writeObject(objectToSend);
        objectOutput.close();

        byte[] answer = bStream.toByteArray();
        return new DatagramPacket(answer, answer.length, clientAddress.getAddress(), clientAddress.getPort());
    }

    @Override
    public void run() {
        try {
            ds.send(sendAnswer());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
