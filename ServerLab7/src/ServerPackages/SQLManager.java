package ServerPackages;

import Classes.Notification;
import Classes.Route;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDate;
import java.util.concurrent.ExecutorService;

public class SQLManager {
    private DatagramSocket socket; private InetSocketAddress clientAddress; private ExecutorService answerPool = Main.answerPool;
    public static Connection connection;

    public SQLManager(DatagramSocket socket, InetSocketAddress clientAddress) {
        this.socket = socket;
        this.clientAddress = clientAddress;
    }

    public SQLManager() {}

    public String encryptPassword(String pass) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD2");

            byte[] messageDigest = md.digest(pass.getBytes());

            BigInteger no = new BigInteger(1, messageDigest);

            StringBuilder hashtext = new StringBuilder(no.toString(16));

            while (hashtext.length() < 32) {
                hashtext.insert(0, "0");
            }
            return hashtext.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void connectToDatabase() throws IllegalAccessException {
        String url = "jdbc:postgresql://localhost:50002/studs";
        String login = "";
        String password = "";

        try {
            DriverManager.registerDriver((Driver) Class.forName("org.postgresql.Driver").newInstance());
            connection = DriverManager.getConnection(url, login, password);
        } catch (ClassNotFoundException | InstantiationException | SQLException e) {
            System.out.println("Ошибка при подключении к ДБ: " + e.getMessage());
        }
    }

    public void createObjectsDB() {
        String createTableSQL = "CREATE TABLE objects("
                + "id INT NOT NULL, "
                + "name VARCHAR NOT NULL, "
                + "xCoord INT NOT NULL, "
                + "yCoord INT NOT NULL, "
                + "creationDate VARCHAR NOT NULL, "
                + "xCoordFrom INT NOT NULL, "
                + "yCoordFrom INT NOT NULL, "
                + "nameFrom VARCHAR NOT NULL, "
                + "xCoordTo INT NOT NULL, "
                + "yCoordTo INT NOT NULL, "
                + "zCoordTo INT NOT NULL, "
                + "distance INT, "
                + "userBy VARCHAR NOT NULL)";
        try {
            Statement statement = connection.createStatement();
            statement.execute(createTableSQL);
            System.out.println("Команда выполнена.");
        } catch (Exception e) {
            System.out.println("Ошибка во время создания ДБ: " + e.getMessage());
        }
    }

    public void createUsersDB() {
        String createTableSQL = "CREATE TABLE users("
                + "name VARCHAR NOT NULL, "
                + "password VARCHAR NOT NULL)";
        try {
            Statement statement = connection.createStatement();
            statement.execute(createTableSQL);
            System.out.println("БД юзеров успешно создана.");
        } catch (SQLException e) {
            System.out.println("Ошибка создания БД для юзеров: " + e.getMessage());
        }

    }

    public ResultSet executeDBCommand(String command) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(command);
        } catch (Exception e) {
            System.out.println("Команда не выполнена: " + e.getMessage());
        } return null;
    }

    public void createSequence() {
        try {
            Statement statement = connection.createStatement();
            statement.execute("CREATE SEQUENCE seq_id START WITH 1 INCREMENT BY 1;");
        } catch (SQLException e) {
            System.out.println("Ошибка во время создания sequence: " + e.getMessage());
        }
    }

    public void registerUser(String login, String password, InetSocketAddress address) throws IOException {
        try {
            if (!isUserExists(login)) {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO users (name, password) VALUES (?, ?)");
                statement.setString(1, login);
                statement.setString(2, encryptPassword(password));
                statement.execute();
                Main.connectedUsers.put(login, address);
                answerPool.execute(new Responder<>(socket, address, new Notification("Регистрация успешна.", null, true)));
            } else {
                answerPool.execute(new Responder<>(socket, address, new Notification("Такой логин уже используется.", null, false)));
            }

        } catch (SQLException e) {
            System.out.println("Ошибка во время регистрации.");
        }
    }

    public boolean insertRouteIntoDB(Route route, String userBy) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO objects " +
                    "(id, name, xCoord, yCoord, creationDate, xCoordFrom, yCoordFrom, nameFrom, xCoordTo, yCoordTo, zCoordTo, distance, userBy) " +
                    "VALUES (nextval('seq_id'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, route.getName());
            statement.setLong(2, route.getCoordinates().getX());
            statement.setLong(3, route.getCoordinates().getY());
            statement.setString(4, String.valueOf(route.getCreationDate()));
            statement.setInt(5, route.getFrom().getX());
            statement.setLong(6, route.getFrom().getY());
            statement.setString(7, route.getFrom().getName());
            statement.setDouble(8, route.getTo().getX());
            statement.setDouble(9, route.getTo().getY());
            statement.setDouble(10, route.getTo().getZ());
            statement.setLong(11, route.getDistance());
            statement.setString(12, userBy);
            statement.execute();
            System.out.println("Объект добавлен.");
            return true;
        } catch (Exception e) {
            System.out.println("Ошибка во время добавления объекта в БД: " + e.getMessage());
            return false;
        }
    }

    public boolean updateRouteInDB(Long id, String name, Long xCoord, long yCoord, LocalDate creationDate, Integer xCoordFrom, Long yCoordFrom, String nameFrom, Double xCoordTo, Float yCoordTo, Long zCoordTo, long distance) {
       try {
           PreparedStatement statement = connection.prepareStatement("UPDATE objects " +
                   "SET name = ?, xCoord = ?, yCoord = ?, creationDate = ?, xCoordFrom = ?, yCoordFrom = ?, nameFrom = ?, xCoordTo = ?, yCoordTo = ?, zCoordTo = ?, distance = ?" +
                   "WHERE id = ?;");
           statement.setString(1, name); statement.setLong(2, xCoord); statement.setLong(3, yCoord);
           statement.setString(4, String.valueOf(creationDate)); statement.setInt(5, xCoordFrom); statement.setLong(6, yCoordFrom);
           statement.setString(7, nameFrom); statement.setDouble(8, xCoordTo); statement.setFloat(9, yCoordTo);
           statement.setLong(10, zCoordTo); statement.setLong(11, distance);
           statement.setLong(12, id);
           statement.execute();
           return true;
       } catch (SQLException e) {
           System.out.println("Ошибка во время обновления объекта в БД: " + e.getMessage());
           return false;
       }


    }

    public boolean removeRouteFromDBbyId(Long id) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM objects WHERE id = ?");
            statement.setLong(1, id);
            statement.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Ошибка во время удаления объекта из БД: " + e.getMessage());
            return false;
        }
    }

    public boolean removeRouteFromDBbyDistance(Long dist) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM objects WHERE distance = ?");
            statement.setLong(1, dist);
            statement.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Ошибка при удалении объектов из БД по дистанции.");
            return false;
        }
    }

    public boolean removeRouteFromDBbyOwner(String owner) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM objects WHERE userBy = ?");
            statement.setString(1, owner);
            statement.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Ошибка во время удаления объектов пользователя " + owner + " : " + e.getMessage());
            return false;
        }
    }

    public int countUserObjects(String usr) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM objects WHERE userBy = ?");
            statement.setString(1, usr);
            ResultSet rs = statement.executeQuery();
            int size = 0;
            while (rs.next()) size++;
            return size;
        } catch (SQLException e) {
            System.out.println("Ошибка во время счёта объектов в БД: " + e.getMessage());
            return 0;
        }
    }

    public boolean checkUserData(String login, String password, InetSocketAddress address) throws IOException {
        String encryptedUserPassword = encryptPassword(password);
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE name = ?");
            statement.setString(1, login);
            ResultSet set = statement.executeQuery();
            boolean isOk = false;
            if (set.next()) isOk = set.getString(2).equals(encryptedUserPassword);
            if (isOk) {
                Main.connectedUsers.put(login, address);
                return true;
            }
        } catch (SQLException | NullPointerException e) {
            System.out.println("Ошибка во время проверки БД юзеров: " + e.getMessage());
        } return false;
    }

    public boolean isCorrectUser(long objId, String usr) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM objects WHERE id = ?");
            statement.setLong(1, objId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) return rs.getString(13).equals(usr);
        } catch (SQLException e) {
            System.out.println("Ошибка во время проверки принадлежности объекта пользователю: " + e.getMessage());
        } return false;
    }

    public boolean isUserExists(String usr) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE name = ?");
            statement.setString(1, usr);
            ResultSet rs = statement.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Ошибка проверки наличия пользователя в БД: " + e.getMessage());
            return false;
        }
    }
}
