package ClassesForColl;

public class LocationFrom {
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

    public String getName() { return this.name; }
}
