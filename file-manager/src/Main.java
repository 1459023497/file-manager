import jdbc.JDBCConnector;
import main.Starter;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        Starter starter = new Starter();
        starter.scan("D:\\Downloads");
        //starter.init();
        starter.showFiles();
        starter.showRepeat();
        JDBCConnector conn= new JDBCConnector();
//        ResultSet rs = conn.select("select * from file;");
//        while(rs.next()){
//            System.out.println(rs.getString("path"));
//        }
        conn.close();
    }
}