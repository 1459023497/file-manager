package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class JDBCConnector {
    private Connection connection;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private static final Logger logger = Logger.getLogger("JDBCConnector.class");

    /**
     * 新建数据库链接
     */
    public JDBCConnector() {
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
     * @param sql
     */
    public void update(String sql) {
        logger.info("SQL语句： "+sql);
        try {
            statement = connection.createStatement();
            statement.executeUpdate(sql);
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查
     * @param sql
     * @return
     */
    public ResultSet select(String sql) {
        logger.info("SQL语句： "+sql);
        try {
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
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
