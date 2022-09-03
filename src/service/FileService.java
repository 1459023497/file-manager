package service;

import jdbc.JDBCConnector;
import tool.IdGenerator;

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
    private IdGenerator id;

    public FileService() {
        conn = new JDBCConnector();
        id = new IdGenerator();
    }

    public void close(){
        conn.close();
    }

    /**
     * 添加文件
     *
     * @param file
     * @param belong 所属文件夹
     */
    public void addFile(File file, String belong) {
        String sql = "INSERT or ignore into file(id,name,path,size,belong) values('"+id.next()+"','" + file.getName() + "','" + file + "','" + file.length() + "','" + belong + "');";
        System.out.println(sql);
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
     * @return true-打开路径, false-路径不存在
     */
    public boolean openDir(String path) {
        File file = new File(path);
        //该路径不存在就返回
        if(!file.exists()) return false;
        try {
            //打开文件
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
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
