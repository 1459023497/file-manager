package jdbc;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSourceFactory;

public class DruidPool {
    
    public void testDruid() throws Exception {
        // 创建properties对象，读取配置文件
        Properties properties = new Properties();
        properties.load(new FileInputStream("src\\jdbc\\druid.properties"));
        // 创建指定参数的数据库连接池
        DataSource dataSource = DruidDataSourceFactory.createDataSource(properties);
        long start = System.currentTimeMillis();
        // 建立连接
        for (int i = 0; i < 10; i++) {
            Connection connection = dataSource.getConnection();
            connection.close();
        }
        long end = System.currentTimeMillis();
        System.out.println("数据库连接耗时(毫秒)：" + (end - start));
        Connection con = dataSource.getConnection();
        ResultSet rs = con.createStatement().executeQuery("select * from file limit 1");
        try {
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String group = rs.getString("belong");
                System.out.println("id: " + id + " name: " + name+" belong: " + group);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        new DruidPool().testDruid();
    }

}
