package mainclasses.main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

public class DialogController {
    // <--> FILTER DIALOG WINDOW <--->
    @FXML
    private MenuButton selectColumn;
    @FXML
    private MenuButton selectMethod;
    @FXML
    private TextField filterTextField;
    @FXML
    private Button filterButton;
    @FXML
    private MenuItem less;
    @FXML
    private MenuItem lessEq;
    @FXML
    private MenuItem more;
    @FXML
    private MenuItem moreEq;
    @FXML
    private MenuItem equals;

    // <---> UPDATE DIALOG WINDOW <--->
    @FXML
    private TextField nameField;
    @FXML
    private TextField xCoordField;
    @FXML
    private TextField yCoordField;
    @FXML
    private TextField xCoordFromField;
    @FXML
    private TextField yCoordFromField;
    @FXML
    private TextField nameFromField;
    @FXML
    private TextField xCoordToField;
    @FXML
    private TextField yCoordToField;
    @FXML
    private TextField zCoordToField;
    @FXML
    private TextField distanceField;
    @FXML
    private Button acceptButton;

    // <---> CONFIRM DIALOG WINDOW <--->
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    // <---> INFO WINDOW <--->
    @FXML
    private Button closeButton;

    public MenuButton getSelectColumn() { return selectColumn; }
    public MenuButton getSelectMethod() { return selectMethod; }
    public TextField getFilterTextField() { return filterTextField; }
    public Button getFilterButton() { return filterButton; }

    public TextField getNameField() {
        return nameField;
    }
    public TextField getXCoordField() {
        return xCoordField;
    }
    public TextField getYCoordField() {
        return yCoordField;
    }
    public TextField getXCoordFromField() {
        return xCoordFromField;
    }
    public TextField getYCoordFromField() {
        return yCoordFromField;
    }
    public TextField getNameFromField() {
        return nameFromField;
    }
    public TextField getXCoordToField() {
        return xCoordToField;
    }
    public TextField getYCoordToField() {
        return yCoordToField;
    }
    public TextField getZCoordToField() {
        return zCoordToField;
    }
    public TextField getDistanceField() {
        return distanceField;
    }
    public Button getAcceptButton() { return acceptButton; }

    public Button getOkButton() {
        return okButton;
    }
    public Button getCancelButton() {
        return cancelButton;
    }

    public MenuItem getLess() {
        return less;
    }
    public MenuItem getLessEq() {
        return lessEq;
    }
    public MenuItem getMore() {
        return more;
    }
    public MenuItem getMoreEq() {
        return moreEq;
    }
    public MenuItem getEquals() {
        return equals;
    }

    public Button getCloseButton() { return closeButton; }
}
