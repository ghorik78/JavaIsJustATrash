package ClientUtils;

import javafx.scene.control.Alert;
import static GUI.Creator.client;
public class Notifier {
    private Alert alert = new Alert(Alert.AlertType.NONE);

    public void showFilterSelectionAlert() {
        alert.setAlertType(Alert.AlertType.ERROR);
        alert.setTitle(client.getProperties().getProperty("error"));
        alert.setHeaderText(null);
        alert.setContentText(client.getProperties().getProperty("selectionErrorText"));
        alert.showAndWait();
    }

    public void showFilterChoiceAlert() {
        alert.setAlertType(Alert.AlertType.ERROR);
        alert.setTitle(client.getProperties().getProperty("error"));
        alert.setHeaderText(null);
        alert.setContentText(client.getProperties().getProperty("selectionErrorText"));
        alert.showAndWait();
    }

    public void showObjectNotSelectedAlert() {
        alert.setAlertType(Alert.AlertType.ERROR);
        alert.setTitle(client.getProperties().getProperty("error"));
        alert.setHeaderText(null);
        alert.setContentText(client.getProperties().getProperty("objectNotSelectedText"));
        alert.showAndWait();
    }

    public void showNotYourObjectAlert() {
        alert.setAlertType(Alert.AlertType.ERROR);
        alert.setTitle(client.getProperties().getProperty("error"));
        alert.setHeaderText(null);
        alert.setContentText(client.getProperties().getProperty("notYourObjectText"));
        alert.showAndWait();
    }

    public void showIncorrectInputFormantAlert() {
        alert.setAlertType(Alert.AlertType.ERROR);
        alert.setTitle(client.getProperties().getProperty("error"));
        alert.setHeaderText(null);
        alert.setContentText(client.getProperties().getProperty("incorrectInputText"));
        alert.showAndWait();
    }

    public void showExecuteScriptError() {
        alert.setAlertType(Alert.AlertType.INFORMATION);
        alert.setTitle(client.getProperties().getProperty("errorTitle"));
        alert.setHeaderText(null);
        alert.setContentText(client.getProperties().getProperty("executeScriptError"));
        alert.showAndWait();
    }

    public void showExecuteScriptFileCorrupted(String commandName) {
        alert.setAlertType(Alert.AlertType.ERROR);
        alert.setTitle(client.getProperties().getProperty("errorTitle"));
        alert.setHeaderText(null);
        alert.setContentText(client.getProperties().getProperty("fileCorruptedText") + commandName);
        alert.showAndWait();
    }

    public void showUnableToCompleteCommand() {
        alert.setAlertType(Alert.AlertType.ERROR);
        alert.setTitle(client.getProperties().getProperty("errorTitle"));
        alert.setHeaderText(null);
        alert.setContentText(client.getProperties().getProperty("unableToCompleteText"));
        alert.showAndWait();
    }

    public void showHelpAlert() {
        alert.setAlertType(Alert.AlertType.INFORMATION);
        alert.setTitle(client.getProperties().getProperty("helpTitle"));
        alert.setHeaderText(null);
        alert.setContentText(client.getProperties().getProperty("helpText"));
        alert.showAndWait();
    }

    // <--> SERVER-SIDE ALERTS <--->
    public void showServerNotRespondingAlert() {
        alert.setAlertType(Alert.AlertType.ERROR);
        alert.setTitle(client.getProperties().getProperty("error"));
        alert.setHeaderText(null);
        alert.setContentText(client.getProperties().getProperty("serverNotRespondingText"));
        alert.showAndWait();
    }

    public void showServerAnswerAlert(String ans) {
        try {
            alert.setAlertType(Alert.AlertType.INFORMATION);
            alert.setTitle(client.getProperties().getProperty("ansFromServerTitle"));
            alert.setHeaderText(null);
            alert.setContentText(ans);
            alert.showAndWait();
        } catch (Exception e) {
            showServerNotRespondingAlert();
        }
    }
}
