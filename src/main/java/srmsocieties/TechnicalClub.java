package srmsocieties;

public class TechnicalClub extends Club {
    public TechnicalClub(int id, String name, String intro) { super(id, name, intro); }
    @Override public String getCategory() { return "Technical"; }
    @Override public String getFocus() { return "Building, coding and innovating with technology"; }
}
