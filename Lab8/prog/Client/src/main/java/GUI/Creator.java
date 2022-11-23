package GUI;

import Classes.PlaneCoords;
import Classes.Route;
import ClientUtils.Checker;
import ClientUtils.Client;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import mainclasses.main.AuthController;
import mainclasses.main.DialogController;
import mainclasses.main.MainSceneController;
import mainclasses.main.Main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Creator {
    public static Stage stage;
    public static Client client;
    private Checker checker;

    protected AuthController authController = new AuthController();

    public static MainSceneController mainSceneController;
    public static DialogController dialogController;
    public static final HashMap<Long, Circle> drawnObjects = new HashMap<>();
    public static final HashMap<Route, Circle> locations = new HashMap<>();

    public Creator(Stage stage, Client client) throws IOException {
        Creator.stage = stage;
        Creator.client = client;

        checker = new Checker(client);
    }

    public void start() throws IOException { authController.loadLangSelectionScene(); }

    public static void createTableScene() throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("selection.fxml"));
            stage.setScene(new Scene(fxmlLoader.load(), 1020, 600));
            stage.setTitle(client.getProperties().getProperty("mainStageTitle"));

            Label selectionLabel = (Label) fxmlLoader.getNamespace().get("selectionLabel");
            selectionLabel.setText(client.getProperties().getProperty("selectionLabelText"));
            Label currUsrLabel = (Label) fxmlLoader.getNamespace().get("currUsrLabel");
            currUsrLabel.setText(client.getProperties().getProperty("currUsrLabel") + ": " + client.getUsrLogin());

            Button tableButton = (Button) fxmlLoader.getNamespace().get("tableButton");
            tableButton.setText(client.getProperties().getProperty("tableButtonText"));
            Button objectsButton = (Button) fxmlLoader.getNamespace().get("visualButton");
            objectsButton.setText(client.getProperties().getProperty("visualButtonText"));
            Button exitButton = (Button) fxmlLoader.getNamespace().get("exitButton");
            exitButton.setText(client.getProperties().getProperty("exitButtonText"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Scene createVisualScene() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("visual_int.fxml"));
            return new Scene(fxmlLoader.load(), 1020, 600);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static ContextMenu createTableContextMenu() {
        ContextMenu tableContextMenu = new ContextMenu();

        tableContextMenu.getItems().addAll(
                new MenuItem("Отфильтровать таблицу"),
                new MenuItem("Добавить новый объект"),
                new MenuItem("Изменить выделенный объект"),
                new MenuItem("Удалить выделенный объект"),
                new MenuItem("Удалить все свои объекты"),
                new MenuItem("Информация о коллекции"),
                new MenuItem("Показать уникальные поля distance"),
                new MenuItem("Выполнить скрипт"),
                new MenuItem("Удалить по distance"),
                new MenuItem("Добавить по условию")
        );
        return tableContextMenu;
    }

    public static ContextMenu createVisualContextMenu() {
        ContextMenu menu = new ContextMenu();
        menu.getItems().addAll(
                new MenuItem("Информация"),
                new MenuItem("Изменить этот объект"),
                new MenuItem("Удалить этот объект")
        );
        return menu;
    }
}