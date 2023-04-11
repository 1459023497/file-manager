package jdbc;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSourceFactory;

public class JDBCConnector {
    private Connection connection;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private DataSource dataSource;
    private static final Logger logger = Logger.getLogger("JDBCConnector.class");

    /**
     * 数据库链接池的方式
     */
    public JDBCConnector() {
        // 创建properties对象，读取配置文件
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("src\\jdbc\\druid.properties"));
            // 创建指定参数的数据库连接池
            dataSource = DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            logger.info("创建数据库连接出错：" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /*
     * 直接连接的方式
     */
    public JDBCConnector(int i){
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:..\\file-manager\\src\\test.db");
            connection.setAutoCommit(false);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 增，删，改
     * 
     * @param sql
     */
    public void update(String sql) {
        logger.info("SQL语句: " + sql);
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(sql);
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查
     * 
     * @param sql
     * @return
     */
    public ResultSet select(String sql) {
        logger.info("SQL语句: " + sql);
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            connection.commit();
            return resultSet;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 关闭库连接
     */
    public void close() {
        try {
            if (resultSet != null)
                resultSet.close();
            if (statement != null)
                statement.close();
            if (connection != null)
                connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
