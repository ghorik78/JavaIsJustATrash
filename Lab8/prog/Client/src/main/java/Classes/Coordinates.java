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

    public void setX(Long x) {
        this.x = x;
    }
    public void setY(Long y) {
        this.y = y;
    }

    public Long getX() { return this.x; }
    public long getY() { return this.y; }
}

