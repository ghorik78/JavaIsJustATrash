package Classes;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.regex.Pattern;


public class Route implements Serializable {
    @Serial
    private static final long serialVersionUID = 18749182798712973L;

    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.time.LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private LocationFrom from; //Поле может быть null
    private LocationTo to; //Поле может быть null
    private long distance; //Значение поля должно быть больше 1

    public Long getId() {
        return id;
    }

    public String getName() { return this.name; }

    public Coordinates getCoordinates() { return this.coordinates; }

    public LocalDate getCreationDate() { return this.creationDate; }

    public LocationFrom getFrom() { return this.from; }

    public LocationTo getTo() { return this.to; }

    public long getDistance() {return this.distance; }

    public void setId() throws NoSuchFieldException, IllegalAccessException {}

    public void setIdManually(Long id) {
        this.id = id;
    }

    public void setName(String name) {

        while (name == null) {
            System.out.println("Ошибка. Введите имя ещё раз: ");
            Scanner scanner = new Scanner(System.in);
            name = scanner.nextLine();
        }

        this.name = name;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setCreationDate(LocalDate localDate) { this.creationDate = localDate; }

    public void setFrom(LocationFrom locationFrom) { this.from = locationFrom; }

    public void setTo(LocationTo locationTo) { this.to = locationTo; }

    public void setDistance(long distance) {
        Scanner scanner = new Scanner(System.in);
        String input = "";
        while (distance <= 1) {
            System.out.println("Значение distance должно быть больше 1! Введите ещё раз: ");
            input = scanner.nextLine();
            while (!Pattern.compile("\\d+").matcher(input).matches()) {
                System.out.println("Значение distance введено неправильно! Попробуйте ещё раз: ");
                input = scanner.nextLine();
            }
            distance = Long.parseLong(input);
        }
        this.distance = distance;
    }

    @Override
    public java.lang.String toString() {
        return "Route:\n" +
                "\tid = " + id +
                "\n\t, name = '" + name + '\'' +
                "\n\t, coordinates = " + coordinates.toString() +
                "\n\t, creationDate = " + creationDate +
                "\n\t, from = " + from.toString() +
                "\n\t, to = " + to.toString() +
                "\n\t, distance= " + distance + "\n";
    }
}
