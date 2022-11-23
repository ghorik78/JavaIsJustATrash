package mainclasses.main;

import Classes.Notification;
import Classes.ObjectColor;
import Classes.PlaneCoords;
import Classes.Route;
import ClientUtils.Checker;
import ClientUtils.Notifier;
import Commands.ClientSideCommands;
import Commands.CommandToSend;
import Commands.IdentifyColorCommand;
import Commands.UpdateCommand;
import GUI.Creator;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.robot.Robot;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static GUI.Creator.*;
import static mainclasses.main.MainSceneController.collectionCopy;

public class Painter extends Canvas {
    public long minDistance = Integer.MAX_VALUE; public long maxDistance = 0;

    private final ContextMenu menu = Creator.createVisualContextMenu();
    private final Notifier notifier = new Notifier();
    private final ClientSideCommands commander = new ClientSideCommands();
    private final Checker checker = new Checker();
    private Group group = null;

    public void prepareVisualMenu(Circle circle) {
        MenuItem showInfo = menu.getItems().get(0);
        showInfo.setText(client.getProperties().getProperty("showInfo"));
        MenuItem update = menu.getItems().get(1);
        update.setText(client.getProperties().getProperty("update"));
        MenuItem remove = menu.getItems().get(2);
        remove.setText(client.getProperties().getProperty("remove"));

        showInfo.setOnAction(actionEvent -> {
            Stage dialogStage = new Stage();
            dialogStage.initOwner(Creator.stage);
            dialogStage.initModality(Modality.WINDOW_MODAL);

            FXMLLoader loader = new FXMLLoader(Main.class.getResource("infoWindow.fxml"));
            try {
                dialogStage.setScene(new Scene(loader.load(), 410, 510));

                Label header = (Label) loader.getNamespace().get("infoHeader");
                header.setText(client.getProperties().getProperty("infoHeader"));
                Label id = (Label) loader.getNamespace().get("id");
                Label name = (Label) loader.getNamespace().get("name");
                Label xCoord = (Label) loader.getNamespace().get("xCoord");
                Label yCoord = (Label) loader.getNamespace().get("yCoord");
                Label creationDate = (Label) loader.getNamespace().get("creationDate");
                Label xCoordFrom = (Label) loader.getNamespace().get("xCoordFrom");
                Label yCoordFrom = (Label) loader.getNamespace().get("yCoordFrom");
                Label nameFrom = (Label) loader.getNamespace().get("nameFrom");
                Label xCoordTo = (Label) loader.getNamespace().get("xCoordTo");
                Label yCoordTo = (Label) loader.getNamespace().get("yCoordTo");
                Label zCoordTo = (Label) loader.getNamespace().get("zCoordTo");
                Label distance = (Label) loader.getNamespace().get("distance");
                Label owner = (Label) loader.getNamespace().get("owner");

                id.setText(client.getProperties().getProperty("id") + ": " + ((Route) circle.getUserData()).getId());
                name.setText(client.getProperties().getProperty("name") + ": " + ((Route) circle.getUserData()).getName());
                xCoord.setText(client.getProperties().getProperty("xCoord") + ": " + ((Route) circle.getUserData()).getCoordinates().getX());
                yCoord.setText(client.getProperties().getProperty("yCoord") + ": " + ((Route) circle.getUserData()).getCoordinates().getY());
                creationDate.setText(client.getProperties().getProperty("creationDate") + ": " + ((Route) circle.getUserData()).getCreationDate());
                xCoordFrom.setText(client.getProperties().getProperty("xCoordFrom") + ": " + ((Route) circle.getUserData()).getFrom().getX());
                yCoordFrom.setText(client.getProperties().getProperty("yCoordFrom") + ": " + ((Route) circle.getUserData()).getFrom().getY());
                nameFrom.setText(client.getProperties().getProperty("nameFrom") + ": " + ((Route) circle.getUserData()).getFrom().getName());
                xCoordTo.setText(client.getProperties().getProperty("xCoordTo") + ": " + ((Route) circle.getUserData()).getTo().getX());
                yCoordTo.setText(client.getProperties().getProperty("yCoordTo") + ": " + ((Route) circle.getUserData()).getTo().getY());
                zCoordTo.setText(client.getProperties().getProperty("zCoordTo") + ": " + ((Route) circle.getUserData()).getTo().getZ());
                distance.setText(client.getProperties().getProperty("distance") + ": " + ((Route) circle.getUserData()).getDistance());
                owner.setText(client.getProperties().getProperty("owner") + ": " + ((Route) circle.getUserData()).getOwner());

                Button closeButton = (Button) loader.getNamespace().get("closeButton");
                closeButton.setText(client.getProperties().getProperty("closeButtonText"));
                closeButton.setOnAction(actionEvent1 -> dialogStage.close());

                dialogStage.show();

            } catch (IOException e) {}
        });
        update.setOnAction(actionEvent -> {
            if (!((Route) circle.getUserData()).getOwner().equals(client.getUsrLogin())) {
                notifier.showNotYourObjectAlert();
            } else createUpdateDialogWindow((Route) circle.getUserData());
        });
        remove.setOnAction(actionEvent -> createRemoveConfirmationWindow(circle));
    }

    public void normalizeRadius(ArrayList<Route> routes) {
        for (Route route : routes) {
            if (route.getDistance() <= minDistance) minDistance = route.getDistance();
            if (route.getDistance() >= maxDistance) maxDistance = route.getDistance();
        }
    }

    public boolean checkCircleCrossing(double x, double y, double rad) {
        try {
            for (Circle circle : locations.values()) {
                double deltaX = Math.abs(x - circle.getCenterX());
                double deltaY = Math.abs(y - circle.getCenterY());
                double deltaRad = 5*Math.abs(rad + circle.getRadius());
                if (Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2)) < deltaRad) return true;
            }
        } catch (NullPointerException e) { return false; }
        return false;
    }

    public ObjectColor identifyColor(Route route) {
        System.out.println(route.getId());
        client.sendObject(new IdentifyColorCommand("identifyColor", new String[]{route.getOwner()}, client.getUsrLogin(), client.getUsrPassword()));
        try {
            IdentifyColorCommand ans = (IdentifyColorCommand) client.receiveObject();
            return ans.getObjectColor();
        } catch (IOException | ClassNotFoundException | NullPointerException e) {
            notifier.showServerNotRespondingAlert();
            return null;
        }
    }

    public void drawCircle(Group c, Route route) {
        group = c;

        if (drawnObjects.get(route.getId()) == null) {
            double radiusT = 1 - Math.exp(1 - (double) route.getDistance() / minDistance);
            double radius = 3 * Math.exp((float) -1 / (radiusT * 5 + 1)) * (radiusT + 2);

            double generatedX = 0.0;
            double generatedY = 0.0;
            while ((checkCircleCrossing(generatedX, generatedY, radius)) || (((generatedX - radius * 5) < 0) || (generatedY - radius * 5) < 0) ||
                    ((generatedX + radius * 5) > (Creator.stage.getWidth() - 60)) || ((generatedY + radius * 5) > (Creator.stage.getHeight() - 175))) {
                generatedX = Math.random() * (Creator.stage.getWidth() - 60);
                generatedY = Math.random() * (Creator.stage.getHeight() - 160);
            }

            ObjectColor color = identifyColor(route);
            Circle circle = new Circle(radius, new Color(1, color.getRed(), color.getGreen(), color.getBlue()));
            circle.setCenterX(generatedX);
            circle.setCenterY(generatedY);
            circle.setUserData(route);
            locations.put((Route) circle.getUserData(), circle);

            circle.setStroke(Color.BLACK);
            circle.setStrokeType(StrokeType.OUTSIDE);
            circle.setStrokeWidth(0.1);

            ScaleTransition transition = new ScaleTransition(Duration.seconds(1), circle);
            transition.setAutoReverse(true);
            transition.setToX(5);
            transition.setToY(5);
            transition.play();

            c.getChildren().add(circle);
            drawnObjects.put(route.getId(), circle);

            circle.setCache(true);

            circle.setOnContextMenuRequested(contextMenuEvent -> {
                prepareVisualMenu(circle);
                menu.show(circle, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
            });

            circle.setOnMouseEntered(mouseEvent -> {
                circle.setStroke(Color.BLACK);
                circle.setStrokeType(StrokeType.OUTSIDE);
                circle.setStrokeWidth(0.2);
            });

            circle.setOnMouseExited(mouseEvent -> {
                circle.setStroke(Color.BLACK);
                circle.setStrokeType(StrokeType.OUTSIDE);
                circle.setStrokeWidth(0.1);
            });
        } else if (!c.getChildren().contains(drawnObjects.get(route.getId()))) {
            c.getChildren().add(drawnObjects.get(route.getId()));
        }
    }

    protected void createRemoveConfirmationWindow(Circle selectedCircle) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle(client.getProperties().getProperty("acceptTitleText"));
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(Creator.stage);

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("remove_confirm.fxml"));

        try {
            dialogStage.setScene(new Scene(fxmlLoader.load(), 220, 100));
            dialogController = fxmlLoader.getController();
            fxmlLoader.setController(dialogController);

            Label contentText = (Label) fxmlLoader.getNamespace().get("contentText");
            contentText.setText(client.getProperties().getProperty("acceptRemove"));
            Button okButton = (Button) fxmlLoader.getNamespace().get("okButton");
            okButton.setText(client.getProperties().getProperty("okButtonText"));
            Button cancelButton = (Button) fxmlLoader.getNamespace().get("cancelButton");
            cancelButton.setText(client.getProperties().getProperty("backButton"));

            dialogController.getOkButton().setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if (((Route) selectedCircle.getUserData()).getOwner().equals(client.getUsrLogin())) {
                        client.sendObject(new CommandToSend("remove_by_id", new String[]{String.valueOf(((Route) selectedCircle.getUserData()).getId())}, client.getUsrLogin(), client.getUsrPassword()));
                        try {
                            System.out.println(((Route) selectedCircle.getUserData()).getId());
                            collectionCopy.remove((Route) selectedCircle.getUserData());
                            Creator.drawnObjects.remove(((Route)selectedCircle.getUserData()).getId());
                            Creator.locations.remove((Route) selectedCircle.getUserData());
                            group.getChildren().remove(selectedCircle);

                            dialogStage.close();

                            Notification ans = (Notification) client.receiveObject();
                            notifier.showServerAnswerAlert(client.getProperties().getProperty("objectRemovedText"));

                        } catch (IOException | ClassNotFoundException | NullPointerException e) {
                            notifier.showServerNotRespondingAlert();
                        }
                    } else {
                        notifier.showNotYourObjectAlert();
                    }
                }
            });
            dialogController.getCancelButton().setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    dialogStage.close();
                }
            });

            dialogStage.show();
        } catch (IOException e) {}

    }

    protected void createUpdateDialogWindow(Route selectedRoute) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle(client.getProperties().getProperty("updateTitleText"));
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(Creator.stage);

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("update.fxml"));

        try {
            dialogStage.setScene(new Scene(fxmlLoader.load(), 300, 520));
            dialogController = fxmlLoader.getController();
            fxmlLoader.setController(dialogController);

            TextField nameField = (TextField) fxmlLoader.getNamespace().get("nameField");
            nameField.setPromptText(client.getProperties().getProperty("name") + " (" + selectedRoute.getName() + ")");
            TextField xCoordField = (TextField) fxmlLoader.getNamespace().get("xCoordField");
            xCoordField.setPromptText(client.getProperties().getProperty("xCoord") + " (" + selectedRoute.getCoordinates().getX() + ")");
            TextField yCoordField = (TextField) fxmlLoader.getNamespace().get("yCoordField");
            yCoordField.setPromptText(client.getProperties().getProperty("yCoord")+ " (" + selectedRoute.getCoordinates().getY() + ")");
            TextField xCoordFrom = (TextField) fxmlLoader.getNamespace().get("xCoordFromField");
            xCoordFrom.setPromptText(client.getProperties().getProperty("xCoordFrom")+ " (" + selectedRoute.getFrom().getX() + ")");
            TextField yCoordFrom = (TextField) fxmlLoader.getNamespace().get("yCoordFromField");
            yCoordFrom.setPromptText(client.getProperties().getProperty("yCoordFrom")+ " (" + selectedRoute.getFrom().getY() + ")");
            TextField nameFrom = (TextField) fxmlLoader.getNamespace().get("nameFromField");
            nameFrom.setPromptText(client.getProperties().getProperty("nameFrom")+ " (" + selectedRoute.getFrom().getName() + ")");
            TextField xCoordTo = (TextField) fxmlLoader.getNamespace().get("xCoordToField");
            xCoordTo.setPromptText(client.getProperties().getProperty("xCoordTo")+ " (" + selectedRoute.getTo().getX() + ")");
            TextField yCoordTo = (TextField) fxmlLoader.getNamespace().get("yCoordToField");
            yCoordTo.setPromptText(client.getProperties().getProperty("yCoordTo")+ " (" + selectedRoute.getTo().getY() + ")");
            TextField zCoordTo = (TextField) fxmlLoader.getNamespace().get("zCoordToField");
            zCoordTo.setPromptText(client.getProperties().getProperty("zCoordTo")+ " (" + selectedRoute.getTo().getZ() + ")");
            TextField distance = (TextField) fxmlLoader.getNamespace().get("distanceField");
            distance.setPromptText(client.getProperties().getProperty("distance")+ " (" + selectedRoute.getDistance() + ")");

            Button acceptButton = (Button) fxmlLoader.getNamespace().get("acceptButton");
            acceptButton.setText(client.getProperties().getProperty("acceptButtonText"));

            dialogController.getAcceptButton().setOnAction(actionEvent -> {
                if (checker.checkUpdateData(dialogController.getNameField(), dialogController.getXCoordField(),
                        dialogController.getYCoordField(), dialogController.getXCoordFromField(), dialogController.getYCoordFromField(),
                        dialogController.getNameFromField(), dialogController.getXCoordToField(), dialogController.getYCoordToField(),
                        dialogController.getZCoordToField(), dialogController.getDistanceField())) {
                    selectedRoute.setName(dialogController.getNameField().getText());

                    UpdateCommand updateCommand = new UpdateCommand("update", new String[]{String.valueOf(selectedRoute.getId())}, client.getUsrLogin(), client.getUsrPassword());
                    updateCommand.setRoute(commander.updateRoute(selectedRoute, dialogController.getNameField().getText(), Long.parseLong(dialogController.getXCoordField().getText()),
                            Long.parseLong(dialogController.getYCoordField().getText()), Integer.parseInt(dialogController.getXCoordFromField().getText()),
                            Long.parseLong(dialogController.getYCoordFromField().getText()), String.valueOf(dialogController.getNameFromField().getText()),
                            Double.parseDouble(dialogController.getXCoordToField().getText()), Float.parseFloat(dialogController.getYCoordToField().getText()),
                            Long.parseLong(dialogController.getZCoordToField().getText()), Long.parseLong(dialogController.getDistanceField().getText())));
                    client.sendObject(updateCommand);
                    try {
                        Notification ans = (Notification) client.receiveObject();
                        notifier.showServerAnswerAlert(client.getProperties().getProperty("objectUpdatedText"));

                        dialogStage.close();
                    } catch (IOException | ClassNotFoundException | NullPointerException e) {
                        notifier.showServerNotRespondingAlert();
                    }
                } else {
                    notifier.showIncorrectInputFormantAlert();
                }
            });

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        dialogStage.show();
    }
}
