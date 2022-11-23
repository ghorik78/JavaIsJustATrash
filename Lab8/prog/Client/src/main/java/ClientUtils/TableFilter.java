package ClientUtils;

import Classes.Route;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TableFilter {
    private MenuButton menuButton;
    private MenuButton filterPredicate;
    private TextField textField;

    private String userChoice;
    private int predicate;

    private Checker checker = new Checker();
    private Notifier notifier = new Notifier();

    public TableFilter(MenuButton menuButton, MenuButton filterPredicate, TextField textField) {
        this.menuButton = menuButton;
        this.filterPredicate = filterPredicate;
        this.textField = textField;
    }

    public TableFilter() {}

    public ArrayList<Route> filterByUserChoice(TableView<Route> tableView, String choice) {
        ArrayList<Route> result = new ArrayList<>();
        try {
            switch (choice) {
                case "id" -> result = filterTableById(tableView, getFilterPredicate());
                case "name" -> result = filterTableByName(tableView, getFilterPredicate());
                case "xCoord" -> result = filterTableByXCoordinate(tableView, getFilterPredicate());
                case "yCoord" -> result = filterTableByYCoordinate(tableView, getFilterPredicate());
                case "creationDate" -> result = filterTableByCreationDate(tableView, getFilterPredicate());
                case "xCoordFrom" -> result = filterTableByXCoordinateFrom(tableView, getFilterPredicate());
                case "yCoordFrom" -> result = filterTableByYCoordinateFrom(tableView, getFilterPredicate());
                case "nameFrom" -> result = filterTableByNameFrom(tableView, getFilterPredicate());
                case "xCoordTo" -> result = filterTableByXCoordinateTo(tableView, getFilterPredicate());
                case "yCoordTo" -> result = filterTableByYCoordinateTo(tableView, getFilterPredicate());
                case "zCoordTo" -> result = filterTableByZCoordinateTo(tableView, getFilterPredicate());
                case "distance" -> result = filterTableByDistance(tableView, getFilterPredicate());
                case "owner" -> result = filterTableByOwner(tableView, getFilterPredicate());
            }
            return result;
        } catch (NullPointerException e) {
            notifier.showFilterChoiceAlert();
            return null;
        }
    }

    public ArrayList<Route> filterTableById(TableView<Route> tableView, int filterPredicate) {
        List<Route> objects = tableView.getItems();
        ArrayList<Route> filtered = new ArrayList<>();
        if (checker.IsIntOrLong(textField.getText())) {
            switch (filterPredicate) {
                case 0 -> filtered = objects.stream().filter(route -> route.getId() < Long.parseLong(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 1 -> filtered = objects.stream().filter(route -> route.getId() <= Long.parseLong(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 2 -> filtered = objects.stream().filter(route -> route.getId() > Long.parseLong(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 3 -> filtered = objects.stream().filter(route -> route.getId() >= Long.parseLong(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 4 -> filtered = objects.stream().filter(route -> route.getId() == Long.parseLong(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
            }
            return filtered;
        } else {
            notifier.showFilterChoiceAlert();
            return null;
        }
    }

    public ArrayList<Route> filterTableByName(TableView<Route> tableView, int filterPredicate) {
        List<Route> objects = tableView.getItems();
        ArrayList<Route> filtered = new ArrayList<>();
        if (checker.IsString(textField.getText())) {
            switch (filterPredicate) {
                case 0 -> filtered = objects.stream().filter(route -> route.getName().compareTo(textField.getText()) < 0).collect(Collectors.toCollection(ArrayList::new));
                case 1 -> filtered = objects.stream().filter(route -> route.getName().compareTo(textField.getText()) <= 0).collect(Collectors.toCollection(ArrayList::new));
                case 2 -> filtered = objects.stream().filter(route -> route.getName().compareTo(textField.getText()) > 0).collect(Collectors.toCollection(ArrayList::new));
                case 3 -> filtered = objects.stream().filter(route -> route.getName().compareTo(textField.getText()) >= 0).collect(Collectors.toCollection(ArrayList::new));
                case 4 -> filtered = objects.stream().filter(route -> route.getName().equals(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
            }
            return filtered;
        } else {
            notifier.showFilterChoiceAlert();
            return null;
        }
    }

    public ArrayList<Route> filterTableByXCoordinate(TableView<Route> tableView, int filterPredicate) {
        List<Route> objects = tableView.getItems();
        ArrayList<Route> filtered = new ArrayList<>();
        if (checker.IsIntOrLong(textField.getText())) {
            switch (filterPredicate) {
                case 0 -> filtered = objects.stream().filter(route -> route.getCoordinates().getX() < Long.parseLong(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 1 -> filtered = objects.stream().filter(route -> route.getCoordinates().getX() <= Long.parseLong(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 2 -> filtered = objects.stream().filter(route -> route.getCoordinates().getX() > Long.parseLong(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 3 -> filtered = objects.stream().filter(route -> route.getCoordinates().getX() >= Long.parseLong(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 4 -> filtered = objects.stream().filter(route -> route.getCoordinates().getX() == Long.parseLong(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
            }
            return filtered;
        } else {
            notifier.showFilterChoiceAlert();
            return null;
        }
    }

    public ArrayList<Route> filterTableByYCoordinate(TableView<Route> tableView, int filterPredicate) {
        List<Route> objects = tableView.getItems();
        ArrayList<Route> filtered = new ArrayList<>();
        if (checker.IsIntOrLong(textField.getText())) {
            switch (filterPredicate) {
                case 0 -> filtered = objects.stream().filter(route -> route.getCoordinates().getY() < Long.parseLong(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 1 -> filtered = objects.stream().filter(route -> route.getCoordinates().getY() <= Long.parseLong(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 2 -> filtered = objects.stream().filter(route -> route.getCoordinates().getY() > Long.parseLong(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 3 -> filtered = objects.stream().filter(route -> route.getCoordinates().getY() >= Long.parseLong(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 4 -> filtered = objects.stream().filter(route -> route.getCoordinates().getY() == Long.parseLong(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
            }
            return filtered;
        } else {
            notifier.showFilterChoiceAlert();
            return null;
        }
    }

    public ArrayList<Route> filterTableByCreationDate(TableView<Route> tableView, int filterPredicate) {
        List<Route> objects = tableView.getItems();
        ArrayList<Route> filtered = new ArrayList<>();
        if (checker.IsString(textField.getText())) {
            switch (filterPredicate) {
                case 0 -> filtered = objects.stream().filter(route -> route.getCreationDate().compareTo(LocalDate.parse(textField.getText())) < 0).collect(Collectors.toCollection(ArrayList::new));
                case 1 -> filtered = objects.stream().filter(route -> route.getCreationDate().compareTo(LocalDate.parse(textField.getText())) <= 0).collect(Collectors.toCollection(ArrayList::new));
                case 2 -> filtered = objects.stream().filter(route -> route.getCreationDate().compareTo(LocalDate.parse(textField.getText())) > 0).collect(Collectors.toCollection(ArrayList::new));
                case 3 -> filtered = objects.stream().filter(route -> route.getCreationDate().compareTo(LocalDate.parse(textField.getText())) >= 0).collect(Collectors.toCollection(ArrayList::new));
                case 4 -> filtered = objects.stream().filter(route -> route.getCreationDate().compareTo(LocalDate.parse(textField.getText())) == 0).collect(Collectors.toCollection(ArrayList::new));
            }
            return filtered;
        } else {
            notifier.showFilterChoiceAlert();
            return null;
        }
    }

    public ArrayList<Route> filterTableByXCoordinateFrom(TableView<Route> tableView, int filterPredicate) {
        List<Route> objects = tableView.getItems();
        ArrayList<Route> filtered = new ArrayList<>();
        if (checker.IsIntOrLong(textField.getText())) {
            System.out.println("+");
            switch (filterPredicate) {
                case 0 -> filtered = objects.stream().filter(route -> route.getFrom().getX() < Integer.parseInt(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 1 -> filtered = objects.stream().filter(route -> route.getFrom().getX() <= Integer.parseInt(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 2 -> filtered = objects.stream().filter(route -> route.getFrom().getX() > Integer.parseInt(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 3 -> filtered = objects.stream().filter(route -> route.getFrom().getX() >= Integer.parseInt(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 4 -> filtered = objects.stream().filter(route -> route.getFrom().getX() == Integer.parseInt(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
            }
            return filtered;
        } else {
            notifier.showFilterChoiceAlert();
            return null;
        }
    }

    public ArrayList<Route> filterTableByYCoordinateFrom(TableView<Route> tableView, int filterPredicate) {
        List<Route> objects = tableView.getItems();
        ArrayList<Route> filtered = new ArrayList<>();
        if (checker.IsIntOrLong(textField.getText())) {
            switch (filterPredicate) {
                case 0 -> filtered = objects.stream().filter(route -> route.getFrom().getY() < Integer.parseInt(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 1 -> filtered = objects.stream().filter(route -> route.getFrom().getY() <= Integer.parseInt(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 2 -> filtered = objects.stream().filter(route -> route.getFrom().getY() > Integer.parseInt(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 3 -> filtered = objects.stream().filter(route -> route.getFrom().getY() >= Integer.parseInt(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 4 -> filtered = objects.stream().filter(route -> route.getFrom().getY() == Integer.parseInt(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
            }
            return filtered;
        } else {
            notifier.showFilterChoiceAlert();
            return null;
        }
    }

    public ArrayList<Route> filterTableByNameFrom(TableView<Route> tableView, int filterPredicate) {
        List<Route> objects = tableView.getItems();
        ArrayList<Route> filtered = new ArrayList<>();
        if (checker.IsString(textField.getText())) {
            switch (filterPredicate) {
                case 0 -> filtered = objects.stream().filter(route -> route.getFrom().getName().compareTo(textField.getText()) < 0).collect(Collectors.toCollection(ArrayList::new));
                case 1 -> filtered = objects.stream().filter(route -> route.getFrom().getName().compareTo(textField.getText()) <= 0).collect(Collectors.toCollection(ArrayList::new));
                case 2 -> filtered = objects.stream().filter(route -> route.getFrom().getName().compareTo(textField.getText()) > 0).collect(Collectors.toCollection(ArrayList::new));
                case 3 -> filtered = objects.stream().filter(route -> route.getFrom().getName().compareTo(textField.getText()) >= 0).collect(Collectors.toCollection(ArrayList::new));
                case 4 -> filtered = objects.stream().filter(route -> route.getFrom().getName().equals(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
            }
            return filtered;
        } else {
            notifier.showFilterChoiceAlert();
            return null;
        }
    }

    public ArrayList<Route> filterTableByXCoordinateTo(TableView<Route> tableView, int filterPredicate) {
        List<Route> objects = tableView.getItems();
        ArrayList<Route> filtered = new ArrayList<>();
        if (checker.IsFloatOrDouble(textField.getText())) {
            switch (filterPredicate) {
                case 0 -> filtered = objects.stream().filter(route -> route.getTo().getX() < Double.parseDouble(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 1 -> filtered = objects.stream().filter(route -> route.getTo().getX() <= Double.parseDouble(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 2 -> filtered = objects.stream().filter(route -> route.getTo().getX() > Double.parseDouble(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 3 -> filtered = objects.stream().filter(route -> route.getTo().getX() >= Double.parseDouble(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 4 -> filtered = objects.stream().filter(route -> route.getTo().getX() == Double.parseDouble(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
            }
            return filtered;
        } else {
            notifier.showFilterChoiceAlert();
            return null;
        }
    }

    public ArrayList<Route> filterTableByYCoordinateTo(TableView<Route> tableView, int filterPredicate) {
        List<Route> objects = tableView.getItems();
        ArrayList<Route> filtered = new ArrayList<>();
        if (checker.IsIntOrLong(textField.getText())) {
            switch (filterPredicate) {
                case 0 -> filtered = objects.stream().filter(route -> route.getTo().getY() < Float.parseFloat(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 1 -> filtered = objects.stream().filter(route -> route.getTo().getY()<= Float.parseFloat(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 2 -> filtered = objects.stream().filter(route -> route.getTo().getY() > Float.parseFloat(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 3 -> filtered = objects.stream().filter(route -> route.getTo().getY() >= Float.parseFloat(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 4 -> filtered = objects.stream().filter(route -> route.getTo().getY() == Float.parseFloat(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
            }
            return filtered;
        } else {
            notifier.showFilterChoiceAlert();
            return null;
        }
    }

    public ArrayList<Route> filterTableByZCoordinateTo(TableView<Route> tableView, int filterPredicate) {
        List<Route> objects = tableView.getItems();
        ArrayList<Route> filtered = new ArrayList<>();
        if (checker.IsIntOrLong(textField.getText())) {
            switch (filterPredicate) {
                case 0 -> filtered = objects.stream().filter(route -> route.getTo().getZ() < Long.parseLong(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 1 -> filtered = objects.stream().filter(route -> route.getTo().getZ() <= Long.parseLong(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 2 -> filtered = objects.stream().filter(route -> route.getTo().getZ() > Long.parseLong(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 3 -> filtered = objects.stream().filter(route -> route.getTo().getZ() >= Long.parseLong(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 4 -> filtered = objects.stream().filter(route -> route.getTo().getZ() == Long.parseLong(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
            }
            return filtered;
        } else {
            notifier.showFilterChoiceAlert();
            return null;
        }
    }

    public ArrayList<Route> filterTableByDistance(TableView<Route> tableView, int filterPredicate) {
        List<Route> objects = tableView.getItems();
        ArrayList<Route> filtered = new ArrayList<>();
        if (checker.IsIntOrLong(textField.getText())) {
            switch (filterPredicate) {
                case 0 -> filtered = objects.stream().filter(route -> route.getDistance() < Long.parseLong(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 1 -> filtered = objects.stream().filter(route -> route.getDistance() <= Long.parseLong(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 2 -> filtered = objects.stream().filter(route -> route.getDistance() > Long.parseLong(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 3 -> filtered = objects.stream().filter(route -> route.getDistance() >= Long.parseLong(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
                case 4 -> filtered = objects.stream().filter(route -> route.getDistance() == Long.parseLong(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
            }
            return filtered;
        } else {
            notifier.showFilterChoiceAlert();
            return null;
        }
    }

    public ArrayList<Route> filterTableByOwner(TableView<Route> tableView, int filterPredicate) {
        List<Route> objects = tableView.getItems();
        ArrayList<Route> filtered = new ArrayList<>();
        if (checker.IsString(textField.getText())) {
            switch (filterPredicate) {
                case 0 -> filtered = objects.stream().filter(route -> route.getOwner().compareTo(textField.getText()) < 0).collect(Collectors.toCollection(ArrayList::new));
                case 1 -> filtered = objects.stream().filter(route -> route.getOwner().compareTo(textField.getText()) <= 0).collect(Collectors.toCollection(ArrayList::new));
                case 2 -> filtered = objects.stream().filter(route -> route.getOwner().compareTo(textField.getText()) > 0).collect(Collectors.toCollection(ArrayList::new));
                case 3 -> filtered = objects.stream().filter(route -> route.getOwner().compareTo(textField.getText()) >= 0).collect(Collectors.toCollection(ArrayList::new));
                case 4 -> filtered = objects.stream().filter(route -> route.getOwner().equals(textField.getText())).collect(Collectors.toCollection(ArrayList::new));
            }
            return filtered;
        } else {
            notifier.showFilterChoiceAlert();
            return null;
        }
    }

    public ArrayList<Route> filterByUniqueDistances(TableView<Route> tableView) {
        return tableView.getItems().stream().filter(distinctByKey(Route::getDistance)).collect(Collectors.toCollection(ArrayList::new));
    }

    public int getFilterPredicate() { return predicate; }
    public String getUserChoice() { return this.userChoice; }

    public void setPredicate(String predicate) {
        switch (predicate) {
            case "less" -> this.predicate = 0;
            case "lessEq" -> this.predicate = 1;
            case "more" -> this.predicate = 2;
            case "moreEq" -> this.predicate = 3;
            case "equals" -> this.predicate = 4;
        }
        System.out.println(this.predicate);
    }

    public void setChoice(String choice) {
        this.userChoice = choice;
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
