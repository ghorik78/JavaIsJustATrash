package mainclasses.main;

import ClientUtils.Client;
import GUI.Creator;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static GUI.Creator.client;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException, ClassNotFoundException {
        Client client = new Client();
        client.prepare();

        Creator creator = new Creator(stage, client);
        creator.start();

        stage.centerOnScreen();
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}