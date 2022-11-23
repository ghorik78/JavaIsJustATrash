package mainclasses.main;

import Classes.Notification;
import ClientUtils.Notifier;
import Commands.CommandToSend;
import GUI.Creator;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Screen;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.CancelledKeyException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static GUI.Creator.stage;
import static GUI.Creator.client;


public class AuthController {
    private Notifier notifier = new Notifier();

    //<---> SELECT METHOD FORM <--->
    @FXML
    private Button signInSelectButton;
    @FXML
    private Button signUpSelectButton;

    //<---> SIGN IN FORM <--->
    @FXML
    private Button signInButton;
    @FXML
    private TextField loginField_signIn;
    @FXML
    private PasswordField passwordField_signIn;

    //<---> SIGN UP FORM <--->
    @FXML
    private Button signUpButton;
    @FXML
    private TextField loginField_signUp;
    @FXML
    private PasswordField passwordField_signUp;
    @FXML
    private PasswordField confirmPasswordField_signUp;

    @FXML
    private Button getBackButton;

    //<---> SELECTION <--->
    public void loadLangSelectionScene() throws IOException {
        Properties properties = new Properties();
        properties.load(new InputStreamReader(new FileInputStream("config_rus.cfg"), StandardCharsets.UTF_8));
        client.setProperties(properties);

        stage.setTitle(client.getProperties().getProperty("authStageTitle"));

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("langSelection.fxml"));
        try {
            stage.setScene(new Scene(loader.load(), 300, 120));
            stage.setMinWidth(300);
            stage.setMinHeight(150);
        } catch (IOException e) {}

        MenuButton menu = (MenuButton) loader.getNamespace().get("langSelection");
        Button acceptButton = (Button) loader.getNamespace().get("acceptButton");
        acceptButton.setText(client.getProperties().getProperty("acceptButtonText"));

        menu.getItems().get(0).setOnAction(actionEvent -> {
            menu.setText("Русский");
            try {
                properties.load(new InputStreamReader(new FileInputStream("config_rus.cfg"), StandardCharsets.UTF_8));
                stage.setTitle(client.getProperties().getProperty("authStageTitle"));
                acceptButton.setText(client.getProperties().getProperty("acceptButtonText"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        menu.getItems().get(1).setOnAction(actionEvent -> {
            menu.setText("Беларускі");
            try {
                properties.load(new InputStreamReader(new FileInputStream("config_by.cfg"), StandardCharsets.UTF_8));
                stage.setTitle(client.getProperties().getProperty("authStageTitle"));
                acceptButton.setText(client.getProperties().getProperty("acceptButtonText"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        menu.getItems().get(2).setOnAction(actionEvent -> {
            menu.setText("Latviski");
            try {
                properties.load(new InputStreamReader(new FileInputStream("config_lt.cfg"), StandardCharsets.UTF_8));
                stage.setTitle(client.getProperties().getProperty("authStageTitle"));
                acceptButton.setText(client.getProperties().getProperty("acceptButtonText"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        menu.getItems().get(3).setOnAction(actionEvent -> {
            menu.setText("Español (Ecuador)");
            try {
                properties.load(new InputStreamReader(new FileInputStream("config_esp.cfg"), StandardCharsets.UTF_8));
                stage.setTitle(client.getProperties().getProperty("authStageTitle"));
                acceptButton.setText(client.getProperties().getProperty("acceptButtonText"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        acceptButton.setOnAction(actionEvent -> loadSelectionScene());
        client.setProperties(properties);
    }

    public void loadSelectionScene() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("selectAuthMethod.fxml"));
        try {
            stage.setScene(new Scene(loader.load(), 300, 120));
            stage.setMinWidth(300);
            stage.setMinHeight(120);
        } catch (IOException e) {}

        signInSelectButton = (Button) loader.getNamespace().get("signInSelectButton");
        signInSelectButton.setText(client.getProperties().getProperty("signInSelect"));
        signUpSelectButton = (Button) loader.getNamespace().get("signUpSelectButton");
        signUpSelectButton.setText(client.getProperties().getProperty("signUpSelect"));
    }

    @FXML
    protected void signInChoiceClicked() {
        loadSignInScene();
    }

    @FXML
    protected void signUpChoiceClicked() {
        loadSignUpScene();
    }

    //<---> SIGN IN <--->
    protected void loadSignInScene() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("signIn.fxml"));
        try {
            stage.setScene(new Scene(loader.load(), 300, 220));
            stage.setMinWidth(300);
            stage.setMinHeight(220);
        } catch (IOException e) {
            System.out.println("+");
        }

        signInButton = (Button) loader.getNamespace().get("signInButton");
        signInButton.setText(client.getProperties().getProperty("signInSelect"));
        loginField_signIn = (TextField) loader.getNamespace().get("loginField_signIn");
        loginField_signIn.setPromptText(client.getProperties().getProperty("loginPrompt"));
        passwordField_signIn = (PasswordField) loader.getNamespace().get("passwordField_signIn");
        passwordField_signIn.setPromptText(client.getProperties().getProperty("passwordPrompt"));
        getBackButton = (Button) loader.getNamespace().get("getBackButton");
        getBackButton.setText(client.getProperties().getProperty("backButton"));
    }

    @FXML
    protected void signInButtonClicked() {
        client.sendObject(new CommandToSend("logIn", null, loginField_signIn.getText(), passwordField_signIn.getText()));
        try {
            Notification ans = (Notification) client.receiveObject();
            try {
                if (ans.getDataResult()) {
                    notifier.showServerAnswerAlert(client.getProperties().getProperty("successfullySignedIn"));
                } else {
                    notifier.showServerAnswerAlert(client.getProperties().getProperty("accessError"));
                }
                if (ans.getDataResult()) {
                    client.setUsrLogin(loginField_signIn.getText());
                    client.setUsrPassword(passwordField_signIn.getText());
                    Creator.createTableScene();
                    stage.centerOnScreen();
                }
            } catch (NullPointerException e) {
                notifier.showServerNotRespondingAlert();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            notifier.showServerNotRespondingAlert();
        }

    }

    //<---> SIGN UP <--->
    protected void loadSignUpScene() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("signUp.fxml"));
        try {
            stage.setScene(new Scene(loader.load(), 300, 280));
            stage.setMinWidth(300);
            stage.setMinHeight(280);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        signUpButton = (Button) loader.getNamespace().get("signUpButton");
        signUpButton.setText(client.getProperties().getProperty("signUpSelect"));
        loginField_signUp = (TextField) loader.getNamespace().get("loginField_signUp");
        loginField_signUp.setPromptText(client.getProperties().getProperty("loginPrompt"));
        passwordField_signUp = (PasswordField) loader.getNamespace().get("passwordField_signUp");
        passwordField_signUp.setPromptText(client.getProperties().getProperty("passwordPrompt"));
        confirmPasswordField_signUp = (PasswordField) loader.getNamespace().get("confirmPasswordField_signUp");
        confirmPasswordField_signUp.setPromptText(client.getProperties().getProperty("confirmPasswordPrompt"));
        getBackButton = (Button) loader.getNamespace().get("getBackButton");
        getBackButton.setText(client.getProperties().getProperty("backButton"));
    }

    @FXML
    protected void signUpButtonClicked() {
        if (passwordField_signUp.getText().equals(confirmPasswordField_signUp.getText())) {
            client.sendObject(new CommandToSend("register", new String[]{}, loginField_signUp.getText(), passwordField_signUp.getText()));
            try {
                Notification ans = (Notification) client.receiveObject();
                if (ans.getDataResult()) {
                    notifier.showServerAnswerAlert(client.getProperties().getProperty("successfullySignedUp"));
                    client.setUsrLogin(loginField_signUp.getText());
                    client.setUsrPassword(passwordField_signUp.getText());
                    Creator.createTableScene();
                    stage.centerOnScreen();
                } else {
                    notifier.showServerAnswerAlert(client.getProperties().getProperty("loginAlreadyInUse"));
                }
            } catch (IOException | ClassNotFoundException | NullPointerException e) {
                notifier.showServerAnswerAlert(client.getProperties().getProperty("serverNotRespondingText"));
            }
        } else notifier.showServerAnswerAlert(client.getProperties().getProperty("differentPasswords"));
    }

    @FXML
    protected void getBackButtonClicked() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("selectAuthMethod.fxml"));
        try {
            stage.setScene(new Scene(loader.load(), 300, 120));
            stage.setMinWidth(300);
            stage.setMinHeight(120);
        } catch (IOException e) {}

        signInSelectButton = (Button) loader.getNamespace().get("signInSelectButton");
        signInSelectButton.setText(client.getProperties().getProperty("signInSelect"));
        signUpSelectButton = (Button) loader.getNamespace().get("signUpSelectButton");
        signUpSelectButton.setText(client.getProperties().getProperty("signUpSelect"));
    }

}
