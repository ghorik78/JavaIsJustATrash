package Commands;

import Classes.*;
import ClientUtils.Notifier;
import GUI.Creator;
import mainclasses.main.MainSceneController;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;

import static GUI.Creator.client;

public class ClientSideCommands {
    private Notifier notifier = new Notifier();
    private ArrayList<Route> errors = new ArrayList<>();

    public Route createRoute(String name, Long xCoord, long yCoord,
                             Integer xCoordFrom, Long yCoordFrom, String nameFrom,
                             Double xCoordTo, Float yCoordTo, Long zCoordTo, long distance) {
        Route routeToAdd = new Route();
        Coordinates coordinates = new Coordinates();
        LocationFrom locationFrom = new LocationFrom();
        LocationTo locationTo = new LocationTo();

        routeToAdd.setName(name);
        coordinates.setX(xCoord);
        coordinates.setY(yCoord);
        routeToAdd.setCoordinates(coordinates);
        routeToAdd.setCreationDate(LocalDate.now());
        locationFrom.setX(xCoordFrom);
        locationFrom.setY(yCoordFrom);
        locationFrom.setName(nameFrom);
        routeToAdd.setFrom(locationFrom);
        locationTo.setX(xCoordTo);
        locationTo.setY(yCoordTo);
        locationTo.setZ(zCoordTo);
        routeToAdd.setTo(locationTo);
        routeToAdd.setDistance(distance);
        routeToAdd.setOwner(Creator.client.getUsrLogin());
        return routeToAdd;
    }


    public Route updateRoute(Route route, String name, Long xCoord, long yCoord,
                            Integer xCoordFrom, Long yCoordFrom, String nameFrom,
                            Double xCoordTo, Float yCoordTo, Long zCoordTo, long distance) {
        route.setName(name);
        Coordinates coords = new Coordinates();
        coords.setX(xCoord);
        coords.setY(yCoord);
        route.setCoordinates(coords);
        LocationFrom locFrom = new LocationFrom();
        locFrom.setX(xCoordFrom);
        locFrom.setY(yCoordFrom);
        locFrom.setName(nameFrom);
        route.setFrom(locFrom);
        LocationTo locTo = new LocationTo();
        locTo.setX(xCoordTo);
        locTo.setY(yCoordTo);
        locTo.setZ(zCoordTo);
        route.setTo(locTo);
        route.setDistance(distance);
        return route;
    }

    public ArrayList<Route> executeScript(String filename) {
        ArrayList<Route> result = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = br.readLine()) != null) {

                String[] checkLine = line.split(" ");
                String cmdFromFile = checkLine[0];
                String[] cmdFromFileArgs = Arrays.copyOfRange(checkLine, 1, checkLine.length);

                if (cmdFromFile.equals("execute_script") && cmdFromFileArgs[0].equals(filename)) {
                    notifier.showExecuteScriptError();
                    continue;
                }

                if (cmdFromFile.equals("add") || cmdFromFile.equals("update") || cmdFromFile.equals("add_if_max")) {
                    boolean ableToCreate = true;

                    Route route = new Route();
                    Coordinates coordinates = new Coordinates();
                    LocationFrom locationFrom = new LocationFrom();
                    LocationTo locationTo = new LocationTo();

                    String lineForCheck = "";
                    for (int i = 0; i < 11; ++i) {
                        if (i != 0) lineForCheck = br.readLine();
                        switch (i) {
                            case 0 -> {
                                if (Objects.equals(cmdFromFile, "update")) {
                                    if (Pattern.compile("-*\\d+").matcher(cmdFromFileArgs[0]).matches()) {
                                        route.setIdManually(Long.parseLong(cmdFromFileArgs[0]));
                                    } else {
                                        notifier.showIncorrectInputFormantAlert();
                                        ableToCreate = false;
                                    }
                                }
                            }
                            case 1 -> {
                                if (lineForCheck.equals("") || !Pattern.compile(".*").matcher(lineForCheck).matches()) {
                                    notifier.showIncorrectInputFormantAlert();
                                    ableToCreate = false;
                                } else route.setName(lineForCheck);
                            }
                            case 2 -> {
                                if (lineForCheck.equals("") || !Pattern.compile("-*\\d+").matcher(lineForCheck).matches()) {
                                    notifier.showIncorrectInputFormantAlert();
                                    ableToCreate = false;
                                } else coordinates.setX(Long.parseLong(lineForCheck));
                            }
                            case 3 -> {
                                if (!Pattern.compile("-*\\d+").matcher(lineForCheck).matches()) {
                                    notifier.showIncorrectInputFormantAlert();
                                    ableToCreate = false;
                                } else {
                                    coordinates.setY(Long.parseLong(lineForCheck));
                                    route.setCoordinates(coordinates);
                                }
                            }
                            case 4 -> {
                                if (lineForCheck.equals("") || !Pattern.compile("-*\\d+").matcher(lineForCheck).matches()) {
                                    notifier.showIncorrectInputFormantAlert();
                                    ableToCreate = false;
                                } else locationFrom.setX(Integer.parseInt(lineForCheck));
                            }
                            case 5 -> {
                                if (lineForCheck.equals("") || !Pattern.compile("-*\\d+").matcher(lineForCheck).matches()) {
                                    notifier.showIncorrectInputFormantAlert();
                                    ableToCreate = false;
                                } else locationFrom.setY(Long.parseLong(lineForCheck));
                            }
                            case 6 -> {
                                if (lineForCheck.equals("") || !Pattern.compile(".*").matcher(lineForCheck).matches()) {
                                    notifier.showIncorrectInputFormantAlert();
                                    ableToCreate = false;
                                } else {
                                    locationFrom.setName(lineForCheck);
                                    route.setFrom(locationFrom);
                                }
                            }
                            case 7 -> {
                                if (!Pattern.compile("-*\\d*\\.*\\d*").matcher(lineForCheck).matches()) {
                                    notifier.showIncorrectInputFormantAlert();
                                    ableToCreate = false;
                                } else locationTo.setX(Double.parseDouble(lineForCheck));
                            }
                            case 8 -> {
                                if (lineForCheck.equals("") || !Pattern.compile("-*\\d*\\.*\\d*").matcher(lineForCheck).matches()) {
                                    notifier.showIncorrectInputFormantAlert();
                                    ableToCreate = false;
                                } else locationTo.setY(Float.parseFloat(lineForCheck));
                            }
                            case 9 -> {
                                if (!Pattern.compile("-*\\d*\\.*\\d*").matcher(lineForCheck).matches()) {
                                    notifier.showIncorrectInputFormantAlert();
                                    ableToCreate = false;
                                } else {
                                    locationTo.setZ(Long.parseLong(lineForCheck));
                                    route.setTo(locationTo);
                                }
                            }
                            case 10 -> {
                                if (!Pattern.compile("\\d+").matcher(lineForCheck).matches() || Long.parseLong(lineForCheck) <= 1) {
                                    notifier.showIncorrectInputFormantAlert();
                                    ableToCreate = false;
                                } else route.setDistance(Long.parseLong(lineForCheck));
                            }
                        }
                    }
                    route.setCreationDate(LocalDate.now());
                    if (ableToCreate) {
                        if (cmdFromFile.equals("add_if_max")) {
                            System.out.println(cmdFromFileArgs[0]);
                            AddCommand addCommand = new AddCommand("add_if_max", new String[]{cmdFromFileArgs[0]}, client.getUsrLogin(), client.getUsrPassword());
                            addCommand.setRoute(route);
                            client.sendObject(addCommand);
                        }
                        if (route.getId() == null) {
                            AddCommand addCommand = new AddCommand("add", new String[]{}, client.getUsrLogin(), client.getUsrPassword());
                            addCommand.setRoute(route);
                            client.sendObject(addCommand);
                        } else {
                            UpdateCommand updateCommand = new UpdateCommand("update", cmdFromFileArgs, client.getUsrLogin(), client.getUsrPassword());
                            updateCommand.setRoute(route);
                            client.sendObject(updateCommand);
                        }
                        try {
                            Notification ans = (Notification) client.receiveObject();
                            result = ans.getCollection();
                        } catch (NullPointerException e) {
                            notifier.showServerNotRespondingAlert();
                        }
                    } else notifier.showExecuteScriptFileCorrupted(cmdFromFile);
                } else {
                    client.sendObject(new CommandToSend(cmdFromFile, cmdFromFileArgs, client.getUsrLogin(), client.getUsrPassword()));
                    Notification ans = (Notification) client.receiveObject();
                    try {
                        if (ans.getArgs()[0].equals("0")) {
                            notifier.showExecuteScriptFileCorrupted(cmdFromFile);
                            return new ArrayList<>(MainSceneController.collectionCopy);
                        }
                        result = ans.getCollection();
                    } catch (Exception e) {
                        notifier.showServerNotRespondingAlert();
                    }

                }
            }
        } catch (IOException | ClassNotFoundException ignored) {}
        return result;
    }
}
