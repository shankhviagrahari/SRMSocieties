package srmsocieties;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClubDAO {

    private Club build(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name"), intro = rs.getString("intro");
        return switch (rs.getString("category")) {
            case "Cultural" -> new CulturalClub(id, name, intro);
            case "Technical" -> new TechnicalClub(id, name, intro);
            default -> new SportsClub(id, name, intro);
        };
    }

    public List<Club> findByCategory(String category) throws SQLException {
        List<Club> list = new ArrayList<>();
        String sql = "SELECT * FROM clubs WHERE category = ? ORDER BY name";
        try (Connection c = DatabaseConnection.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(build(rs)); }
        }
        return list;
    }

    public Club findClub(int id) throws SQLException {
        try (Connection c = DatabaseConnection.get(); PreparedStatement ps = c.prepareStatement("SELECT * FROM clubs WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? build(rs) : null; }
        }
    }

    private Team team(ResultSet rs) throws SQLException {
        return new Team(rs.getInt("id"), rs.getInt("club_id"), rs.getString("name"),
                rs.getString("intro"), rs.getString("heads"), rs.getString("apply_info"));
    }

    public List<Team> findTeams(int clubId) throws SQLException {
        List<Team> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.get(); PreparedStatement ps = c.prepareStatement("SELECT * FROM teams WHERE club_id = ? ORDER BY id")) {
            ps.setInt(1, clubId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(team(rs)); }
        }
        return list;
    }

    public Team findTeam(int id) throws SQLException {
        try (Connection c = DatabaseConnection.get(); PreparedStatement ps = c.prepareStatement("SELECT * FROM teams WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? team(rs) : null; }
        }
    }

    // ---------- admin: create / update / delete ----------
    private void run(String sql, Object... args) throws SQLException {
        try (Connection c = DatabaseConnection.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) ps.setObject(i + 1, args[i]);
            ps.executeUpdate();
        }
    }
    public void addClub(String category, String name, String intro) throws SQLException {
        run("INSERT INTO clubs(category,name,intro) VALUES (?,?,?)", category, name, intro);
    }
    public void updateClub(int id, String name, String intro) throws SQLException {
        run("UPDATE clubs SET name=?, intro=? WHERE id=?", name, intro, id);
    }
    public void deleteClub(int id) throws SQLException {
        run("DELETE FROM teams WHERE club_id=?", id);
        run("DELETE FROM clubs WHERE id=?", id);
    }
    public void addTeam(int clubId, String name, String intro, String heads, String apply) throws SQLException {
        run("INSERT INTO teams(club_id,name,intro,heads,apply_info) VALUES (?,?,?,?,?)", clubId, name, intro, heads, apply);
    }
    public void updateTeam(int id, String name, String intro, String heads, String apply) throws SQLException {
        run("UPDATE teams SET name=?, intro=?, heads=?, apply_info=? WHERE id=?", name, intro, heads, apply, id);
    }
    public void deleteTeam(int id) throws SQLException {
        run("DELETE FROM teams WHERE id=?", id);
    }
}
