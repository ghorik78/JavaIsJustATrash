package AbstrClasses;

public abstract class SceneObject {
    public String name;

    public SceneObject(String name) {
        this.name = name;
    }

    public void Say(String msg) {
        System.out.println(msg);
    }
}
