package srmsocieties;

import java.sql.*;

public class StudentDAO {
    /** Returns the student's name if regNo exists, else null. */
    public String findName(String regNo) throws SQLException {
        String sql = "SELECT name FROM students WHERE reg_no = ?";
        try (Connection c = DatabaseConnection.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, regNo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("name") : null;
            }
        }
    }

    public boolean isAdmin(String regNo) throws SQLException {
        String sql = "SELECT is_admin FROM students WHERE reg_no = ?";
        try (Connection c = DatabaseConnection.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, regNo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getBoolean("is_admin");
            }
        }
    }

    /** Admin login: returns the admin's name if regNo is an admin and the password matches, else null. */
    public String checkAdmin(String regNo, String password) throws SQLException {
        String sql = "SELECT name FROM students WHERE reg_no = ? AND is_admin = 1 AND admin_password = ?";
        try (Connection c = DatabaseConnection.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, regNo);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("name") : null;
            }
        }
    }
}
