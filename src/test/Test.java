package test;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class Test {
    public static void main(String[] args) throws IOException, SQLException {

//        JDBCConnector connector = new JDBCConnector();
//        FileService s = new FileService();
//        s.openDir("D:\\Downloads");
//        connector.close();
        File file = new File("D:\\Downloads\\苹果.txt");
        System.out.println(file.getName());
    }
}