package srmsocieties;

public class Team {
    private final int id, clubId;
    private final String name, intro, heads, applyInfo;

    public Team(int id, int clubId, String name, String intro, String heads, String applyInfo) {
        this.id = id; this.clubId = clubId; this.name = name;
        this.intro = intro; this.heads = heads; this.applyInfo = applyInfo;
    }
    public int getId() { return id; }
    public int getClubId() { return clubId; }
    public String getName() { return name; }
    public String getIntro() { return intro; }
    public String getHeads() { return heads; }
    public String getApplyInfo() { return applyInfo; }
}
