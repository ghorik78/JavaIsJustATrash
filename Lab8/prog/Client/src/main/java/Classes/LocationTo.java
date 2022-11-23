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

    public double getX() {
        return this.x;
    }
    public Float getY() {
        return this.y;
    }
    public long getZ() {
        return this.z;
    }

    public void setX(Double x) {
        this.x = x;
    }
    public void setY(Float y) {
        this.y = y;
    }
    public void setZ(Long z) {
        this.z = z;
    }
}