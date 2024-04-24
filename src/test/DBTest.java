package test;
import java.sql.*;

public class DBTest {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        Connection c = DriverManager.getConnection("jdbc:sqlite:D:\\project\\file-manager\\src\\test.db");
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery("select * from tag;");
        while (rs.next()){
            System.out.println(rs.getString("name"));
        }
    }
}
