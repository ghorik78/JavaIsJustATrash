package ClientUtils;

import Classes.Notification;
import Commands.ClientSideCommands;
import Commands.CommandToSend;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.regex.Pattern;

public class Checker {
    private Client client;

    public Checker(Client client) {
        this.client = client;
    }

    public Checker() {}

    public boolean checkSignInData(String login, String pass) {
        try {
            CommandToSend command = new CommandToSend("logIn", null, login, pass);
            client.sendObject(command);
            Notification ans = (Notification) client.receiveObject();
            if (ans.getDataResult()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Успешно");
                alert.setHeaderText(null);
                alert.setContentText(ans.getText());
                alert.showAndWait();
                return true;
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText(null);
                alert.setContentText(ans.getText());
                alert.showAndWait();
                return false;
            }
        } catch (IOException | ClassNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Ошибка во время отправки запроса серверу.");
            alert.showAndWait();
            System.out.println(e.getMessage());
            return false;
        } catch (NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ошибка сервера");
            alert.setHeaderText(null);
            alert.setContentText("Сервер не отвечает. Возможно, сейчас он недоступен.");
            alert.showAndWait();
            return false;
        }
    }

    public boolean checkSignUpData(String login, String pass, String confirmedPass) {
        if (!pass.equals(confirmedPass)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Введённые вами пароли не совпадают!");
            alert.showAndWait();
            return false;
        }
        try {
            client.sendObject(new CommandToSend("register", null, login, pass));
            Notification ans = (Notification) client.receiveObject();
            if (ans.getDataResult()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Успешно");
                alert.setHeaderText(null);
                alert.setContentText(ans.getText());
                alert.showAndWait();
                return true;
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText(null);
                alert.setContentText(ans.getText());
                alert.showAndWait();
                return false;
            }
        } catch (IOException | ClassNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Ошибка во время отправки запроса серверу.");
            alert.showAndWait();
            return false;
        } catch (NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Сервер не отвечает. Возможно, сейчас он недоступен.");
            alert.showAndWait();
            return false;
        }
    }

    public boolean IsIntOrLong(String v) {
        return Pattern.compile("\\d+").matcher(v).matches();
    }

    public boolean IsFloatOrDouble(String v) {
        return Pattern.compile("\\d+\\.*\\d*").matcher(v).matches();
    }

    public boolean IsString(String v) {
        return Pattern.compile("\\S+").matcher(v).matches();
    }

    public boolean checkUpdateData(TextField name, TextField xCoord, TextField yCoord,
                                   TextField xCoordFrom, TextField yCoordFrom, TextField nameFrom,
                                   TextField xCoordTo, TextField yCoordTo, TextField zCoordTo, TextField distance) {
        return Pattern.compile("\\S+").matcher(name.getText()).matches() &&
                Pattern.compile("-*\\d+").matcher(xCoord.getText()).matches() &&
                Pattern.compile("-*\\d+").matcher(yCoord.getText()).matches() &&
                Pattern.compile("-*\\d+").matcher(xCoordFrom.getText()).matches() &&
                Pattern.compile("-*\\d+").matcher(yCoordFrom.getText()).matches() &&
                Pattern.compile("\\S+").matcher(nameFrom.getText()).matches() &&
                Pattern.compile("-*\\d+\\.*\\d*").matcher(xCoordTo.getText()).matches() &&
                Pattern.compile("-*\\d+\\.*\\d*").matcher(yCoordTo.getText()).matches() &&
                Pattern.compile("-*\\d+\\.*\\d*").matcher(zCoordTo.getText()).matches() &&
                Pattern.compile("\\d+").matcher(distance.getText()).matches();
    }

}
