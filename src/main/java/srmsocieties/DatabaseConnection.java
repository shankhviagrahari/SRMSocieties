package srmsocieties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Online (Render + Aiven) the MYSQL* environment variables are set in Render.
 * On your own laptop they are missing, so the defaults below are used.
 */
public class DatabaseConnection {

    private static String env(String key, String fallback) {
        String v = System.getenv(key);
        return (v == null || v.isEmpty()) ? fallback : v;
    }

    public static Connection get() throws SQLException {
        String url = "jdbc:mysql://" + env("MYSQLHOST", "localhost") + ":" + env("MYSQLPORT", "3306")
                + "/" + env("MYSQLDATABASE", "srmsocieties")
                + "?sslMode=" + env("MYSQL_SSLMODE", "DISABLED") + "&allowPublicKeyRetrieval=true";
        String user = env("MYSQLUSER", "srm");           // local MySQL user
        String password = env("MYSQLPASSWORD", "srm1234"); // local MySQL password
        return DriverManager.getConnection(url, user, password);
    }
}
