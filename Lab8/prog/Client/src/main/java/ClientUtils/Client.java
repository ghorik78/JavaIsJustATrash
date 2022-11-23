package ClientUtils;

import Classes.Notification;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Properties;
import java.util.Set;

public class Client {
    private String usrLogin; private String usrPassword;
    private Properties properties;

    static DatagramChannel datagramChannel; static DatagramSocket ds; static DatagramPacket dp;
    static InetSocketAddress host; static int port = 59812;
    static byte[] arr; ByteBuffer buffer = ByteBuffer.allocate(65536);
    static Selector selector;
    private int currTab;

    public String getUsrLogin() { return this.usrLogin; }
    public String getUsrPassword() { return this.usrPassword; }
    public Properties getProperties() { return this.properties; }
    public int getCurrTab() { return this.currTab; }

    public void setUsrLogin(String usrLogin) { this.usrLogin = usrLogin; }
    public void setUsrPassword(String usrPassword) { this.usrPassword = usrPassword; }
    public void setProperties(Properties properties) { this.properties = properties; }
    public void setCurrTab(int tab) { this.currTab = tab; }

    public void prepare() throws UnknownHostException {
        host = new InetSocketAddress(InetAddress.getLocalHost(), port);
        try {
            selector = Selector.open();
            datagramChannel = DatagramChannel.open();
            datagramChannel.configureBlocking(false);
            ds = datagramChannel.socket();
            datagramChannel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            System.out.println("Проблемы с созданием канала.");
        }
    }

    public void sendObject(Object obj) {
        try {
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(bStream);
            objectOutputStream.writeObject(obj);
            objectOutputStream.close();
            arr = bStream.toByteArray();
            datagramChannel.send(ByteBuffer.wrap(arr), host);
        } catch (IOException e) {
            System.out.println("Ошибка при отправке объекта: " + e.getMessage());
        }

    }

    public void showMsg(Notification msg) {
        try {
            System.out.println(msg.getText());
        } catch (Exception e) {
            System.out.println("Ответ не был получен. Возможно, сервер временно недоступен.");
        }

    }

    public Object receiveObject() throws IOException, ClassNotFoundException {
        buffer.clear();

        try {
            int n = selector.select(4000);

            if (n != 0) {
                Set<SelectionKey> readyKeys = selector.selectedKeys();

                for (SelectionKey key : readyKeys) {
                    readyKeys.remove(key);

                    if (key.isReadable()) {
                        datagramChannel = (DatagramChannel) key.channel();

                        datagramChannel.receive(buffer);

                        key.interestOps(SelectionKey.OP_READ);

                        buffer.flip();
                        byte[] receivedBytes = new byte[buffer.remaining()];
                        buffer.get(receivedBytes);
                        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(receivedBytes));
                        return ois.readObject();
                    }
                }
            }
        } catch (IOException e) {}

        return null;
    }
}