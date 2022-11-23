package Classes;


import java.io.Serial;
import java.io.Serializable;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Coordinates implements Serializable {
    @Serial
    private static final long serialVersionUID = 91823987986123123L;

    private Long x; //Поле не может быть null
    private long y; //Значение поля должно быть больше -382

    public Long getX() { return this.x; }
    public long getY() { return this.y; }

    public void setX(Long x) {
        this.x = x;
    }
    public void setY(Long y) {
        while (y <= -382) {
            System.out.println("Значение поля должно быть больше -382! Попробуйте ещё раз: ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();

            while (!Pattern.compile("-*\\d+").matcher(input).matches()) {
                System.out.println("Значение введено неправильно! Попробуйте ещё раз: ");
                input = scanner.nextLine();
            }
            y = Long.parseLong(input);
        }
        this.y = y;
    }

    @Override
    public java.lang.String toString() {
        return "Coordinates{" +
                "x = " + x +
                ", y = " + y + "}";
    }

}

