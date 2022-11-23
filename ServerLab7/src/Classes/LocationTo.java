package Classes;

import java.io.Serial;
import java.io.Serializable;
import java.util.Scanner;

public class LocationTo implements Serializable {
    @Serial
    private static final long serialVersionUID = 528374816767623123L;

    private Double x;
    private Float y; //Поле не может быть null
    private Long z;

    public void setX(Double x) {
        this.x = x;
    }
    public void setY(Float y) {
        while (y == null) {
            System.out.println("Это значение не может быть пустым! Введите координату Y ещё раз: ");
            Scanner scanner = new Scanner(System.in);
            y = Float.parseFloat(scanner.nextLine());
        }

        this.y = y;
    }
    public void setZ(Long z) {
        this.z = z;
    }

    public double getX() {
        return this.x;
    }
    public Float getY() {
        return this.y;
    }
    public long getZ() {
        return this.z;
    }


    @Override
    public java.lang.String toString() {
        return "LocationTo{" +
                "x = " + x +
                ", y = " + y +
                ", z = " + z + "}";
    }
}