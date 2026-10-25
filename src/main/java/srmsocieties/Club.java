package srmsocieties;

/** Base class (abstraction + inheritance). Each category extends it. */
public abstract class Club {
    private final int id;
    private final String name;
    private final String intro;

    public Club(int id, String name, String intro) {
        this.id = id; this.name = name; this.intro = intro;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getIntro() { return intro; }

    public abstract String getCategory();   // polymorphism
    public abstract String getFocus();

    public String getClassName() { return getClass().getSimpleName(); }
    public String getParentName() { return getClass().getSuperclass().getSimpleName(); }
}
