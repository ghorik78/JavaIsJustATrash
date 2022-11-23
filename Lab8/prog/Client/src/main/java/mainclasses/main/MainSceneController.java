package mainclasses.main;

import Classes.*;
import ClientUtils.Checker;
import ClientUtils.Notifier;
import ClientUtils.TableFilter;
import Commands.AddCommand;
import Commands.ClientSideCommands;
import Commands.CommandToSend;
import Commands.UpdateCommand;
import GUI.Creator;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

import static GUI.Creator.*;

public class MainSceneController {
    private DialogController dialogController;
    private TableFilter tableFilter = new TableFilter();
    private final Notifier notifier = new Notifier();
    private final Checker checker = new Checker();
    private final ClientSideCommands commander = new ClientSideCommands();
    private final Painter painter = new Painter();

    public static ArrayList<Route> collectionCopy;

    public boolean isTableCreated = false;

    @FXML
    private Button tableButton;
    @FXML
    private Button visualButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Button exitButton;
    @FXML
    private Label currUserLabel;
    @FXML
    public static TableView<Route> objectsTable = new TableView<>();
    @FXML
    private Pane canvasRegion;
    @FXML
    private Group group;

    @FXML
    protected void tableButtonClicked() throws IOException {
        Creator.createTableScene();
        createObjectsTable();
    }

    @FXML
    protected void objectsButtonClicked() throws IOException, InterruptedException {
        stage.setScene(Creator.createVisualScene());
        createVisualObjects();
    }

    @FXML
    protected void refreshButtonClicked() {
        try {
            createObjectsTable();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    protected void exitButtonClicked() { System.exit(1); }

    public void createObjectsTable() throws IOException {
        client.setCurrTab(0);
        loadTableScene();
        client.sendObject(new CommandToSend("show", new String[]{}, client.getUsrLogin(), client.getUsrPassword()));
        try {
            Notification ans = (Notification) client.receiveObject();
            try {
                ArrayList<Route> obj = new ArrayList<>(ans.getCollection());
                collectionCopy = obj;
                ObservableList<Route> obsObj = FXCollections.observableList(obj);
                objectsTable.setItems(obsObj);
            } catch (NullPointerException e) {
                objectsTable.setPlaceholder(new Label("Коллекция пуста."));
            }

            objectsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            objectsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

            ContextMenu menu = Creator.createTableContextMenu();
            setTableContextItems(menu);
            objectsTable.setContextMenu(menu);

            // <---> TABLE STRUCTURE <--->
            if (!isTableCreated) {
                TableColumn<Route, Long> idColumn = new TableColumn<>(client.getProperties().getProperty("id"));
                idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
                objectsTable.getColumns().add(idColumn);

                TableColumn<Route, String> nameColumn = new TableColumn<>(client.getProperties().getProperty("name"));
                nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
                objectsTable.getColumns().add(nameColumn);

                TableColumn<Route, Long> xCoordColumn = new TableColumn<>(client.getProperties().getProperty("xCoord"));
                xCoordColumn.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getCoordinates().getX()).asObject());
                objectsTable.getColumns().add(xCoordColumn);

                TableColumn<Route, Long> yCoordColumn = new TableColumn<>(client.getProperties().getProperty("yCoord"));
                yCoordColumn.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getCoordinates().getY()).asObject());
                objectsTable.getColumns().add(yCoordColumn);

                TableColumn<Route, LocalDate> creationDate = new TableColumn<>(client.getProperties().getProperty("creationDate"));
                creationDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCreationDate()));
                objectsTable.getColumns().add(creationDate);

                TableColumn<Route, Integer> xCoordFromColumn = new TableColumn<>(client.getProperties().getProperty("xCoordFrom"));
                xCoordFromColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getFrom().getX()).asObject());
                objectsTable.getColumns().add(xCoordFromColumn);

                TableColumn<Route, Long> yCoordFromColumn = new TableColumn<>(client.getProperties().getProperty("yCoordFrom"));
                yCoordFromColumn.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getFrom().getY()).asObject());
                objectsTable.getColumns().add(yCoordFromColumn);

                TableColumn<Route, String> nameFromColumn = new TableColumn<>(client.getProperties().getProperty("nameFrom"));
                nameFromColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFrom().getName()));
                objectsTable.getColumns().add(nameFromColumn);

                TableColumn<Route, Double> xCoordToColumn = new TableColumn<>(client.getProperties().getProperty("xCoordTo"));
                xCoordToColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getTo().getX()).asObject());
                objectsTable.getColumns().add(xCoordToColumn);

                TableColumn<Route, Float> yCoordToColumn = new TableColumn<>(client.getProperties().getProperty("yCoordTo"));
                yCoordToColumn.setCellValueFactory(cellData -> new SimpleFloatProperty(cellData.getValue().getTo().getY()).asObject());
                objectsTable.getColumns().add(yCoordToColumn);

                TableColumn<Route, Long> zCoordToColumn = new TableColumn<>(client.getProperties().getProperty("zCoordTo"));
                zCoordToColumn.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getTo().getZ()).asObject());
                objectsTable.getColumns().add(zCoordToColumn);

                TableColumn<Route, Long> distanceColumn = new TableColumn<>(client.getProperties().getProperty("distance"));
                distanceColumn.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getDistance()).asObject());
                objectsTable.getColumns().add(distanceColumn);

                TableColumn<Route, String> ownerColumn = new TableColumn<>(client.getProperties().getProperty("owner"));
                ownerColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOwner()));
                objectsTable.getColumns().add(ownerColumn);

                isTableCreated = true;
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    protected void createVisualObjects() throws IOException, InterruptedException {
        client.setCurrTab(1);
        client.sendObject(new CommandToSend("show", new String[]{}, client.getUsrLogin(), client.getUsrPassword()));
        try {
            Notification ans = (Notification) client.receiveObject();
            collectionCopy = ans.getCollection();
        } catch (IOException | ClassNotFoundException | NullPointerException e) {
            notifier.showServerNotRespondingAlert();
        }

        loadVisualScene();

        painter.normalizeRadius(collectionCopy);

        for (Route route : collectionCopy) {
            painter.drawCircle(group, route);
        }

        refreshDrawnObjects();

    }

    // <---> CREATING DIALOG ELEMENTS <--->
    protected void setTableContextItems(ContextMenu menu) {
        MenuItem filterBy = menu.getItems().get(0);
        filterBy.setText(client.getProperties().getProperty("filter"));
        MenuItem addNew = menu.getItems().get(1);
        addNew.setText(client.getProperties().getProperty("addNew"));
        MenuItem updateSelected = menu.getItems().get(2);
        updateSelected.setText(client.getProperties().getProperty("update"));
        MenuItem removeSelected = menu.getItems().get(3);
        removeSelected.setText(client.getProperties().getProperty("remove"));
        MenuItem removeAll = menu.getItems().get(4);
        removeAll.setText(client.getProperties().getProperty("removeAll"));
        MenuItem collectionInfo = menu.getItems().get(5);
        collectionInfo.setText(client.getProperties().getProperty("collectionInfo"));
        MenuItem uniqueDistances = menu.getItems().get(6);
        uniqueDistances.setText(client.getProperties().getProperty("uniqueDistances"));
        MenuItem executeScript = menu.getItems().get(7);
        executeScript.setText(client.getProperties().getProperty("executeScript"));
        MenuItem removeByDistance = menu.getItems().get(8);
        removeByDistance.setText(client.getProperties().getProperty("removeByDistance"));
        MenuItem addIf = menu.getItems().get(9);
        addIf.setText(client.getProperties().getProperty("addIf"));

        filterBy.setOnAction(actionEvent -> {
            createFilterDialogWindow();
            tableFilter.setPredicate(dialogController.getSelectMethod().getId());
        });

        addNew.setOnAction(actionEvent -> createAddDialogWindow(0));

        updateSelected.setOnAction(actionEvent -> {
            Route selected = objectsTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                notifier.showObjectNotSelectedAlert();
            }
            else if (!Objects.equals(selected.getOwner(), client.getUsrLogin())) notifier.showNotYourObjectAlert();
            else createUpdateDialogWindow(objectsTable.getSelectionModel().getSelectedItem());
        });

        removeSelected.setOnAction(actionEvent -> {
            Route selected = objectsTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                createRemoveConfirmationWindow(selected);
            } else notifier.showObjectNotSelectedAlert();
        });

        removeAll.setOnAction(actionEvent -> {
            client.sendObject(new CommandToSend("clear", null, client.getUsrLogin(), client.getUsrPassword()));
            try {
                Notification ans = (Notification) client.receiveObject();
                if (ans.getArgs()[0].equals("1")) notifier.showServerAnswerAlert(client.getProperties().getProperty("allObjectsRemovedText"));
                createObjectsTable();
            } catch (Exception e) {
                notifier.showServerNotRespondingAlert();
            }

        });

        collectionInfo.setOnAction(actionEvent -> {
            client.sendObject(new CommandToSend("info", new String[]{}, client.getUsrLogin(), client.getUsrPassword()));
            try {
                Notification ans = (Notification) client.receiveObject();
                if (ans.getArgs()[0].equals("1")) {
                    notifier.showServerAnswerAlert(client.getProperties().getProperty("info1.1") + ans.getCollection().size() + '\n' +
                            client.getProperties().getProperty("info1.2") + LocalDate.now());
                }
            } catch (IOException | ClassNotFoundException ignore) {}
            catch (NullPointerException e){
                notifier.showServerNotRespondingAlert();
            }
        });

        uniqueDistances.setOnAction(actionEvent -> objectsTable.setItems(FXCollections.observableList(tableFilter.filterByUniqueDistances(objectsTable))));

        executeScript.setOnAction(actionEvent -> createExecuteScriptWindow());

        removeByDistance.setOnAction(actionEvent -> {
            createRemoveByDistanceWindow();
        });

        addIf.setOnAction(actionEvent -> {
            createAddDialogWindow(1);
        });
    }

    protected void createFilterDialogWindow() {
        Stage dialogStage = new Stage();
        dialogStage.setTitle(client.getProperties().getProperty("filterTitleText"));
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(Creator.stage);

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("filter.fxml"));
        try {
            dialogStage.setScene(new Scene(fxmlLoader.load(), 300, 200));
            dialogController = fxmlLoader.getController();

            dialogController.getSelectColumn().setText(client.getProperties().getProperty("dialogControllerText"));
            dialogController.getSelectColumn().getItems().get(0).setText(client.getProperties().getProperty("id"));
            dialogController.getSelectColumn().getItems().get(1).setText(client.getProperties().getProperty("name"));
            dialogController.getSelectColumn().getItems().get(2).setText(client.getProperties().getProperty("xCoord"));
            dialogController.getSelectColumn().getItems().get(3).setText(client.getProperties().getProperty("yCoord"));
            dialogController.getSelectColumn().getItems().get(4).setText(client.getProperties().getProperty("creationDate"));
            dialogController.getSelectColumn().getItems().get(5).setText(client.getProperties().getProperty("xCoordFrom"));
            dialogController.getSelectColumn().getItems().get(6).setText(client.getProperties().getProperty("yCoordFrom"));
            dialogController.getSelectColumn().getItems().get(7).setText(client.getProperties().getProperty("nameFrom"));
            dialogController.getSelectColumn().getItems().get(8).setText(client.getProperties().getProperty("xCoordTo"));
            dialogController.getSelectColumn().getItems().get(9).setText(client.getProperties().getProperty("yCoordTo"));
            dialogController.getSelectColumn().getItems().get(10).setText(client.getProperties().getProperty("zCoordTo"));
            dialogController.getSelectColumn().getItems().get(11).setText(client.getProperties().getProperty("distance"));
            dialogController.getSelectColumn().getItems().get(12).setText(client.getProperties().getProperty("owner"));

            dialogController.getSelectMethod().setText(client.getProperties().getProperty("filterType"));
            dialogController.getSelectMethod().getItems().get(0).setText(client.getProperties().getProperty("type0"));
            dialogController.getSelectMethod().getItems().get(1).setText(client.getProperties().getProperty("type1"));
            dialogController.getSelectMethod().getItems().get(2).setText(client.getProperties().getProperty("type2"));
            dialogController.getSelectMethod().getItems().get(3).setText(client.getProperties().getProperty("type3"));
            dialogController.getSelectMethod().getItems().get(4).setText(client.getProperties().getProperty("type4"));

            Button filterButton = (Button) fxmlLoader.getNamespace().get("filterButton");
            filterButton.setText(client.getProperties().getProperty("filterButtonText"));

            tableFilter = new TableFilter(dialogController.getSelectMethod(), dialogController.getSelectColumn(), dialogController.getFilterTextField());

            for (MenuItem item : dialogController.getSelectColumn().getItems()) {
                item.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        dialogController.getSelectColumn().setText(item.getText());
                        tableFilter.setChoice(item.getId());
                    }
                });
            }

            for (MenuItem item : dialogController.getSelectMethod().getItems()) {
                item.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        dialogController.getSelectMethod().setText(item.getText());
                        tableFilter.setPredicate(item.getId());
                    }
                });
            }

            dialogController.getFilterButton().setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if ((tableFilter.getUserChoice() != null)) {
                        try {
                            ObservableList<Route> sorted = FXCollections.observableList(tableFilter.filterByUserChoice(objectsTable, tableFilter.getUserChoice()));
                            objectsTable.setItems(sorted);
                            dialogStage.close();
                        } catch (Exception e) {}
                    } else notifier.showFilterSelectionAlert();
                }
            });
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        dialogStage.show();
    }

    protected void createAddDialogWindow(int type) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle(client.getProperties().getProperty("createTitleText"));
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(Creator.stage);

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("create.fxml"));

        try {
            dialogStage.setScene(new Scene(fxmlLoader.load(), 300, 520));
            dialogController = fxmlLoader.getController();
            fxmlLoader.setController(dialogController);

            TextField nameField = (TextField) fxmlLoader.getNamespace().get("nameField");
            nameField.setPromptText(client.getProperties().getProperty("name"));
            TextField xCoordField = (TextField) fxmlLoader.getNamespace().get("xCoordField");
            xCoordField.setPromptText(client.getProperties().getProperty("xCoord"));
            TextField yCoordField = (TextField) fxmlLoader.getNamespace().get("yCoordField");
            yCoordField.setPromptText(client.getProperties().getProperty("yCoord"));
            TextField xCoordFrom = (TextField) fxmlLoader.getNamespace().get("xCoordFromField");
            xCoordFrom.setPromptText(client.getProperties().getProperty("xCoordFrom"));
            TextField yCoordFrom = (TextField) fxmlLoader.getNamespace().get("yCoordFromField");
            yCoordFrom.setPromptText(client.getProperties().getProperty("yCoordFrom"));
            TextField nameFrom = (TextField) fxmlLoader.getNamespace().get("nameFromField");
            nameFrom.setPromptText(client.getProperties().getProperty("nameFrom"));
            TextField xCoordTo = (TextField) fxmlLoader.getNamespace().get("xCoordToField");
            xCoordTo.setPromptText(client.getProperties().getProperty("xCoordTo"));
            TextField yCoordTo = (TextField) fxmlLoader.getNamespace().get("yCoordToField");
            yCoordTo.setPromptText(client.getProperties().getProperty("yCoordTo"));
            TextField zCoordTo = (TextField) fxmlLoader.getNamespace().get("zCoordToField");
            zCoordTo.setPromptText(client.getProperties().getProperty("zCoordTo"));
            TextField distance = (TextField) fxmlLoader.getNamespace().get("distanceField");
            distance.setPromptText(client.getProperties().getProperty("distance"));

            Button acceptButton = (Button) fxmlLoader.getNamespace().get("acceptButton");
            acceptButton.setText(client.getProperties().getProperty("acceptButtonText"));

            dialogController.getAcceptButton().setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if (checker.checkUpdateData(dialogController.getNameField(), dialogController.getXCoordField(),
                            dialogController.getYCoordField(), dialogController.getXCoordFromField(), dialogController.getYCoordFromField(),
                            dialogController.getNameFromField(), dialogController.getXCoordToField(), dialogController.getYCoordToField(),
                            dialogController.getZCoordToField(), dialogController.getDistanceField())) {
                        Route route = commander.createRoute(dialogController.getNameField().getText(), Long.parseLong(dialogController.getXCoordField().getText()),
                                Long.parseLong(dialogController.getYCoordField().getText()), Integer.parseInt(dialogController.getXCoordFromField().getText()),
                                Long.parseLong(dialogController.getYCoordFromField().getText()), String.valueOf(dialogController.getNameFromField().getText()),
                                Double.parseDouble(dialogController.getXCoordToField().getText()), Float.parseFloat(dialogController.getYCoordToField().getText()),
                                Long.parseLong(dialogController.getZCoordToField().getText()), Long.parseLong(dialogController.getDistanceField().getText()));
                        AddCommand addCommand = null;
                        if (type == 1) {
                            addCommand = new AddCommand("add_if_max", new String[]{route.getName()}, client.getUsrLogin(), client.getUsrPassword());
                        } else if (type == 0) {
                            addCommand = new AddCommand("add", new String[]{}, client.getUsrLogin(), client.getUsrPassword());
                        }
                        assert addCommand != null;
                        addCommand.setRoute(route);
                        client.sendObject(addCommand);
                        try {
                            Notification ans = (Notification) client.receiveObject();

                            if (ans.getArgs()[0].equals("1")) notifier.showServerAnswerAlert(client.getProperties().getProperty("objectAddedText"));
                            else if (ans.getArgs()[0].equals("0")) notifier.showUnableToCompleteCommand();

                            refreshTable();

                            dialogStage.close();
                        } catch (IOException | ClassNotFoundException | NullPointerException e) {
                            notifier.showServerNotRespondingAlert();
                        }
                    }
                }
            });
            dialogStage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
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
            distance.setPromptText(client.getProperties().getProperty("distance") + " (" + selectedRoute.getDistance() + ")");

            Button acceptButton = (Button) fxmlLoader.getNamespace().get("acceptButton");
            acceptButton.setText(client.getProperties().getProperty("acceptButtonText"));

            dialogController.getAcceptButton().setOnAction(new EventHandler<>() {
                @Override
                public void handle(ActionEvent actionEvent) {
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
                            notifier.showServerAnswerAlert(ans.getText());

                            refreshTable();

                            dialogStage.close();
                        } catch (IOException | ClassNotFoundException | NullPointerException e) {
                            notifier.showServerNotRespondingAlert();
                        }
                    } else {
                        notifier.showFilterSelectionAlert();
                    }
                }
            });

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        dialogStage.show();
    }

    protected void createRemoveConfirmationWindow(Route selectedRoute) {
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
                    client.sendObject(new CommandToSend("remove_by_id", new String[]{String.valueOf(selectedRoute.getId())}, client.getUsrLogin(), client.getUsrPassword()));
                    try {
                        Creator.drawnObjects.remove(selectedRoute.getId());
                        Creator.locations.remove(selectedRoute);

                        try {
                            group.getChildren().remove(drawnObjects.get(selectedRoute.getId()));
                        } catch (NullPointerException ignore) {}

                        dialogStage.close();
                        Notification ans = (Notification) client.receiveObject();
                        notifier.showServerAnswerAlert(ans.getText());

                        refreshTable();
                    } catch (IOException | ClassNotFoundException | NullPointerException e) {
                        e.printStackTrace();
                        notifier.showServerNotRespondingAlert();
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

    protected void createExecuteScriptWindow() {
        Stage dialogStage = new Stage();
        dialogStage.setTitle(client.getProperties().getProperty("executeScriptTitleText"));
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(Creator.stage);

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("execute_script.fxml"));
        try {
            dialogStage.setScene(new Scene(loader.load(), 300, 130));
            dialogStage.show();

            Button executeButton = (Button) loader.getNamespace().get("executeButton");
            executeButton.setText(client.getProperties().getProperty("executeButtonText"));
            TextField filename = (TextField) loader.getNamespace().get("filenameField");
            filename.setPromptText(client.getProperties().getProperty("filenamePrompt"));
            Label helpLabel = (Label) loader.getNamespace().get("helpLabel");
            helpLabel.setText(client.getProperties().getProperty("help"));

            executeButton.setOnAction(actionEvent -> {
                ArrayList<Route> result = commander.executeScript(filename.getText());
                collectionCopy = result;
                if ((result != null) && (result.size() != 0)) {
                    objectsTable.setItems(FXCollections.observableList(result));
                } else {
                    assert collectionCopy != null;
                    objectsTable.setItems(FXCollections.observableList(collectionCopy));
                }
                dialogStage.close();
            });

            helpLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    notifier.showHelpAlert();
                }
            });

        } catch (IOException ignored) {
            System.out.println("+");
        }
    }

    protected void createRemoveByDistanceWindow() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("removeByDistance.fxml"));

        Stage dialogStage = new Stage();
        dialogStage.setTitle(client.getProperties().getProperty("executeScriptTitleText"));
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(Creator.stage);
        dialogStage.setMinWidth(300);
        dialogStage.setMinHeight(120);

        try {
            dialogStage.setScene(new Scene(loader.load(), 300, 120));

            Button acceptButton = (Button) loader.getNamespace().get("acceptButton");
            acceptButton.setText(client.getProperties().getProperty("removeButtonText"));

            TextField distanceField = (TextField) loader.getNamespace().get("distanceField");
            distanceField.setPromptText(client.getProperties().getProperty("filenamePrompt"));

            dialogStage.show();

            acceptButton.setOnAction(actionEvent -> {
                if (!Pattern.compile("\\d+").matcher(distanceField.getText()).matches()) notifier.showIncorrectInputFormantAlert();
                else {
                    client.sendObject(new CommandToSend("remove_all_by_distance", new String[]{String.valueOf(distanceField.getText())}, client.getUsrLogin(), client.getUsrPassword()));
                    try {
                        Notification ans = (Notification) client.receiveObject();
                        objectsTable.setItems(FXCollections.observableList(ans.getCollection()));
                        refreshTable();
                        dialogStage.close();
                    } catch (IOException | ClassNotFoundException e ) {
                        notifier.showUnableToCompleteCommand();
                    } catch (NullPointerException e){
                        notifier.showServerNotRespondingAlert();
                    }
                }
            });

        } catch (IOException ignored) {}
    }

    protected void refreshTable() {
        client.sendObject(new CommandToSend("show", null, client.getUsrLogin(), client.getUsrPassword()));
        try {
            objectsTable.setItems(FXCollections.observableList(((Notification) client.receiveObject()).getCollection()));
        } catch (IOException | ClassNotFoundException | NullPointerException e) {
            e.printStackTrace();
        }
    }
    
    //НЕ ТРОГАТЬ НИКОГДА
    protected void refreshDrawnObjects() {
        Timeline refresher = new Timeline(
                new KeyFrame(Duration.seconds(1),
                        event -> {
                            client.sendObject(new CommandToSend("show", new String[]{}, client.getUsrLogin(), client.getUsrPassword()));
                            try {
                                Notification ans = (Notification) client.receiveObject();
                                painter.normalizeRadius(ans.getCollection());

                                if (collectionCopy.size() != ans.getCollection().size()) loadVisualScene();
                                
                                collectionCopy = ans.getCollection();
                                painter.normalizeRadius(collectionCopy);

                                for (Route route : collectionCopy) {
                                    painter.drawCircle(group, route);
                                }

                            } catch (IOException | ClassNotFoundException | NullPointerException e) {
                                notifier.showServerNotRespondingAlert();
                            }
                        }));

        refresher.setOnFinished(actionEvent -> {
            if (client.getCurrTab() == 1) {
                if (canvasRegion.isVisible()) refresher.play();
            }
        });
        refresher.play();
    }

    // <---> LOADERS <--->
    protected void loadTableScene() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("table_int.fxml"));
            stage.setScene(new Scene(fxmlLoader.load(), 1020, 600));

            objectsTable = (TableView<Route>) fxmlLoader.getNamespace().get("objectsTable");

            tableButton = (Button) fxmlLoader.getNamespace().get("tableButton");
            tableButton.setText(client.getProperties().getProperty("tableButtonText"));
            visualButton = (Button) fxmlLoader.getNamespace().get("visualButton");
            visualButton.setText(client.getProperties().getProperty("visualButtonText"));
            refreshButton = (Button) fxmlLoader.getNamespace().get("refreshButton");
            refreshButton.setText(client.getProperties().getProperty("refreshButtonText"));
            exitButton = (Button) fxmlLoader.getNamespace().get("exitButton");
            exitButton.setText(client.getProperties().getProperty("exitButtonText"));

            canvasRegion = (Pane) fxmlLoader.getNamespace().get("canvasRegion");
            group = (Group) fxmlLoader.getNamespace().get("group");
            currUserLabel = (Label) fxmlLoader.getNamespace().get("currUserLabel");
            currUserLabel.setText(client.getProperties().getProperty("currUsrLabel") + ": " + client.getUsrLogin());

            refreshButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    refreshTable();
                }
            });

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    protected void loadVisualScene() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("visual_int.fxml"));
            Creator.stage.setScene(new Scene(fxmlLoader.load(), 1020, 600));
            fxmlLoader.setController(this);

            tableButton = (Button) fxmlLoader.getNamespace().get("tableButton");
            tableButton.setText(client.getProperties().getProperty("tableButtonText"));
            visualButton = (Button) fxmlLoader.getNamespace().get("objectsButton");
            visualButton.setText(client.getProperties().getProperty("visualButtonText"));
            exitButton = (Button) fxmlLoader.getNamespace().get("exitButton");
            exitButton.setText(client.getProperties().getProperty("exitButtonText"));

            canvasRegion = (Pane) fxmlLoader.getNamespace().get("canvasRegion");
            group = (Group) fxmlLoader.getNamespace().get("group");
            currUserLabel = (Label) fxmlLoader.getNamespace().get("currUserLabel");
            currUserLabel.setText(client.getProperties().getProperty("currUsrLabel") + ": " + client.getUsrLogin());

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
