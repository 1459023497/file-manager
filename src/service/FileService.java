package service;

import jdbc.JDBCConnector;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class FileService {
    private JDBCConnector conn;

    public FileService() {
    }

    public FileService(JDBCConnector conn) {
        this.conn = conn;
    }

    /**
     * 添加文件
     *
     * @param file
     * @param belong 所属文件夹
     */
    public void addFile(File file, String belong) {
        String sql = "INSERT into file(name,path,size,belong) values('" + file.getName() + "','" + file + "','" + file.length() + "','" + belong + "');";
        conn.update(sql);
    }

    /**
     * 删除文件
     *
     * @param file
     */
    public void removeFile(File file) {
        String sql = "DELETE FROM file WHERE path = '" + file + "';";
        conn.update(sql);
    }

    /**
     * 打开文件夹
     *
     * @param path
     */
    public void openDir(String path) {
        try {
            //Desktop.getDesktop().open(new File("D:\\文件夹"));
            Desktop.getDesktop().open(new File(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查所有文件
     *
     * @return
     */
    public HashMap<String, Set<File>> getAllFiles() {
        String sql = "SELECT * FROM file;";
        ResultSet rs = conn.select(sql);
        HashMap<String, Set<File>> files = new HashMap<>();
        while (true) {
            try {
                if (!rs.next()) break;
                String dir = rs.getString("belong");
                String path = rs.getString("path");
                if (files.get(dir) == null) {
                    HashSet<File> set = new HashSet<>();
                    set.add(new File(path));
                    files.put(dir,set);
                }else{
                    files.get(dir).add(new File(path));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return files;
    }
}
