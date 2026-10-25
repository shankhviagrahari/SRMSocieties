package srmsocieties;

public class CulturalClub extends Club {
    public CulturalClub(int id, String name, String intro) { super(id, name, intro); }
    @Override public String getCategory() { return "Cultural"; }
    @Override public String getFocus() { return "Expression through art, performance and creativity"; }
}
