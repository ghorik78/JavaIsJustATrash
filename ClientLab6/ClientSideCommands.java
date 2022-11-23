package ServerPackages;

import Classes.*;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ClientSideCommands {
    public Route add(String[] args) {
        if (args.length != 0) System.out.println("У данной команды нет аргументов");
        else {
            Scanner scanner = new Scanner(System.in);
            Route routeToAdd = new Route();
            Coordinates coordinates = new Coordinates();
            LocationFrom locationFrom = new LocationFrom();
            LocationTo locationTo = new LocationTo();

            String inputArg = "";

            for (int i = 1; i < 11; ++i) {
                switch (i) {
                    case 1 -> {
                        System.out.println("Введите имя объекта: ");
                        inputArg = scanner.nextLine();
                        while (!Pattern.compile("\\S+").matcher(inputArg).matches()) {
                            System.out.println("Значение имени введено неправильно! Попробуйте ещё раз: ");
                            inputArg = scanner.nextLine();
                        }
                        routeToAdd.setName(inputArg);
                    }
                    case 2 -> {
                        System.out.println("Введите координату х: ");
                        inputArg = scanner.nextLine();
                        while (!Pattern.compile("-*\\d+").matcher(inputArg).matches()) {
                            System.out.println("Значение координаты Х введено неправильно! Попробуйте ещё раз: ");
                            inputArg = scanner.nextLine();
                        }
                        coordinates.setX(Long.parseLong(inputArg));
                    }
                    case 3 -> {
                        System.out.println("Введите координату y: ");
                        inputArg = scanner.nextLine();
                        while (!Pattern.compile("-*\\d+").matcher(inputArg).matches()) {
                            System.out.println("Значение координаты Y введено неправильно! Попробуйте ещё раз: ");
                            inputArg = scanner.nextLine();
                        }
                        coordinates.setY(Long.parseLong(inputArg));
                        routeToAdd.setCoordinates(coordinates);
                    }
                    case 4 -> {
                        System.out.println("Введите начальную координату х: ");
                        inputArg = scanner.nextLine();
                        while (!Pattern.compile("-*\\d+").matcher(inputArg).matches()) {
                            System.out.println("Значение начальной координаты X введено неправильно! Попробуйте ещё раз: ");
                            inputArg = scanner.nextLine();
                        }
                        locationFrom.setX(Integer.parseInt(inputArg));
                    }
                    case 5 -> {
                        System.out.println("Введите начальную координату y: ");
                        inputArg = scanner.nextLine();
                        while (!Pattern.compile("-*\\d+").matcher(inputArg).matches()) {
                            System.out.println("Значение начальной координаты Y ведено неправильно! Попробуйте ещё раз: ");
                            inputArg = scanner.nextLine();
                        }
                        locationFrom.setY(Long.parseLong(inputArg));
                    }
                    case 6 -> {
                        System.out.println("Введите название: ");
                        inputArg = scanner.nextLine();
                        while (!Pattern.compile(".+").matcher(inputArg).matches()) {
                            System.out.println("Введённая строка с названием не может быть пустой! Попробуйте ещё раз: ");
                            inputArg = scanner.nextLine();
                        }
                        locationFrom.setName(inputArg);

                        routeToAdd.setFrom(locationFrom);
                    }
                    case 7 -> {
                        System.out.println("Введите конечную координату х: ");
                        inputArg = scanner.nextLine();
                        if (inputArg.equals("")) {
                            try {
                                locationTo.setX(null);
                            } catch (NullPointerException | InputMismatchException e) {
                                System.out.println("Полю с конечной координатой X будет присвоено значние null.");
                            }
                        } else {
                            while (!Pattern.compile("-*\\d*\\.*\\d*").matcher(inputArg).matches()) {
                                System.out.println("Значение конечной координаты X введено неправильно! Попробуйте ещё раз: ");
                                inputArg = scanner.nextLine();
                            }
                            locationTo.setX(Double.parseDouble(inputArg));
                        }
                    }
                    case 8 -> {
                        System.out.println("Введите конечную координату y: ");
                        inputArg = scanner.nextLine();
                        while (!Pattern.compile("-*\\d+\\.*\\d*").matcher(inputArg).matches()) {
                            System.out.println("Значение конечной координаты Y введено неправильно! Попробуйте ещё раз: ");
                            inputArg = scanner.nextLine();
                        }
                        locationTo.setY(Float.parseFloat(inputArg));
                    }
                    case 9 -> {
                        System.out.println("Введите конечную координату z: ");
                        inputArg = scanner.nextLine();
                        if (inputArg.equals("")) {
                            try {
                                locationTo.setZ(null);
                            } catch (NullPointerException | InputMismatchException e) {
                                System.out.println("Полю со значением конечной координаты Z будет присвоено значние null.");
                            }
                        } else {
                            while (!Pattern.compile("-*\\d*\\.*\\d*").matcher(inputArg).matches()) {
                                System.out.println("Значение конечной координаты Z введено неправильно! Попробуйте ещё раз: ");
                                inputArg = scanner.nextLine();
                            }
                            locationTo.setZ(Long.parseLong(inputArg));
                        }
                        routeToAdd.setTo(locationTo);
                    }
                    case 10 -> {
                        System.out.println("Введите расстояние: ");
                        inputArg = scanner.nextLine();
                        while (!Pattern.compile("\\d+").matcher(inputArg).matches()) {
                            System.out.println("Значение расстояния введено неправильно! Попробуйте ещё раз: ");
                            inputArg = scanner.nextLine();
                        }
                        routeToAdd.setDistance(Long.parseLong(inputArg));
                    }
                    default -> System.out.println("Что-то пошло не так.");
                }
            }
            routeToAdd.setCreationDate(LocalDate.now());
            return routeToAdd;
        } return null;
    }

    public void exit(String[] args) {
        if (args.length != 0) System.out.println("У данной команды не может быть аргументов!");
        else {
            System.out.println("Завершение работы приложения.");
            System.exit(1);
        }
    }
}
