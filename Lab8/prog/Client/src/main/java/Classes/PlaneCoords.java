package Classes;

public class PlaneCoords {
    private double x;
    private double y;
    private double rad;

    public PlaneCoords(double x, double y, double rad) {
        this.x = x;
        this.y = y;
        this.rad = rad;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getRad() { return rad; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setRad(double rad) { this.rad = rad; }
}
