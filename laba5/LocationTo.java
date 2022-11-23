package ClassesForColl;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.Scanner;

@XStreamAlias("LocationTo")
public class LocationTo {
    private Double x;
    private Float y; //Поле не может быть null
    private Long z;

    public double getX() {
        return x;
    }

    public Float getY() {
        return y;
    }

    public long getZ() {
        return z;
    }

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

}
