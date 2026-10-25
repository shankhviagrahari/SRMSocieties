package srmsocieties;

public class SportsClub extends Club {
    public SportsClub(int id, String name, String intro) { super(id, name, intro); }
    @Override public String getCategory() { return "Sports"; }
    @Override public String getFocus() { return "Fitness, teamwork and competition"; }
}
