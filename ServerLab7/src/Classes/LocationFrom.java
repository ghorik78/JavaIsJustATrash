package Classes;

import java.io.Serial;
import java.io.Serializable;

public class LocationFrom implements Serializable {
    @Serial
    private static final long serialVersionUID = 19284098128781723L;

    private Integer x; //Поле не может быть null
    private Long y; //Поле не может быть null
    private String name; //Строка не может быть пустой, Поле может быть null

    public void setX(Integer x) {
        this.x = x;
    }
    public void setY(Long y) {
        this.y = y;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Integer getX() { return this.x; }
    public Long getY() { return this.y; }
    public String getName() { return this.name; }

    @Override
    public java.lang.String toString() {
        return "LocationFrom{" +
                "x = " + x +
                ", y = " + y +
                ", name = " + name + "}";
    }
}
