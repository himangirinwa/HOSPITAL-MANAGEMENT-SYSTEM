import java.sql.*;

public class ConnectionProvider {

    // connecting to database
    public static Connection getConn() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:8080/hospital", "root", "root");
            // conn = DriverManager.getConnection("DATABASE_URL", "USERNAME", "PASSWORD")
            // System.out.println("Connection established");
            return conn;
        } catch (Exception e) {
            // System.out.println(e);
            return null;
        }
    }

}
