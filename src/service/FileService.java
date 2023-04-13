package service;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import entity.IFile;
import entity.ITag;
import jdbc.JDBCConnector;
import tool.IdGenerator;

public class FileService {
    private JDBCConnector conn;
    private IdGenerator id;

    public FileService() {
        conn = new JDBCConnector();
        id = new IdGenerator();
    }

    public void close() {
        conn.close();
    }

    /**
     * 添加文件
     *
     * @param file
     * @param belong 所属文件夹
     */
    public void addFile(File file, String belong) {
        // String regex = "/\''/g"; //去掉所有单引号
        String name = file.getName().replace('\'', ' ');
        String path = file.getPath().replace('\'', ' ');
        String sql = "INSERT or ignore into file(id,name,path,size,belong) values('" + id.next() + "','"
                + name + "','" + path + "','" + file.length() + "','" + belong + "');";
        System.out.println(sql);
        conn.update(sql);
    }

    /**
     * 重命名
     * 
     * @param file
     */
    public void renameFile(IFile file) {
        String name = file.getName();
        String id = file.getId();
        String sql = "UPDATE file SET name = '" + name + "' WHERE id = '" + id + "';";
        conn.update(sql);
    }

    /**
     * 删除文件
     *
     * @param file
     */
    public void removeFile(IFile file) {
        if (file.isDirectory()) {
            removeDir(file.getPath());
        } else {
            String sql = "DELETE FROM file WHERE id = '" + file.getId() + "';";
            conn.update(sql);
        }
    }

    /**
     * 删除文件夹
     * 
     * @param dir
     */
    public void removeDir(String dir) {
        String sql = "DELETE FROM file WHERE belong = '" + dir + "';";
        conn.update(sql);

    }

    /**
     * 打开文件
     *
     * @param path
     * @return true-打开路径, false-路径不存在
     */
    public boolean openDir(String path) {
        File file = new File(path);
        // 该路径不存在就返回
        if (!file.exists())
            return false;
        try {
            // 打开文件
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    /**
     * 查所有文件，按文件夹分类
     *
     * @return <文件夹，文件>
     */
    public HashMap<String, Set<IFile>> getAllFiles() {
        String sql = "SELECT * FROM file;";
        ResultSet rs = conn.select(sql);
        // 字典《目录：文件》
        HashMap<String, Set<IFile>> files = new HashMap<>();

        try {
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String path = rs.getString("path");
                String size = rs.getString("size");
                String dir = rs.getString("belong");
                IFile file = new IFile(id, name, path, size, dir);
                if (files.get(dir) == null) {
                    HashSet<IFile> set = new HashSet<>();
                    set.add(file);
                    files.put(dir, set);
                } else {
                    files.get(dir).add(file);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.close();
        }
        return files;
    }

    /*
     * 查所有文件
     */
    public List<IFile> getFiles() {
        String sql = "SELECT * FROM file;";
        ResultSet rs = conn.select(sql);
        List<IFile> files = new ArrayList<IFile>();
        while (true) {
            try {
                if (!rs.next())
                    break;
                String id = rs.getString("id");
                String name = rs.getString("name");
                String path = rs.getString("path");
                String size = rs.getString("size");
                String dir = rs.getString("belong");
                IFile file = new IFile(id, name, path, size, dir);
                files.add(file);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                conn.close();
            }
        }
        return files;
    }

    /*
     * 查重
     * 
     * @return <大小，文件>
     */
    public Map<String, List<IFile>> getRepeatMap() {
        Map<String, List<IFile>> map = new HashMap<String, List<IFile>>();
        List<IFile> files = getFiles();
        if (!files.isEmpty()) {
            map = files.stream().collect(Collectors.groupingBy(IFile::getSize));
            map = map.entrySet().stream().filter(e -> e.getValue().size() > 1)
                    .collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue()));
        }
        return map;
    }

    /*
     * 模糊搜索
     */
    public List<IFile> search(String text) {
        Map<String, IFile> map = new HashMap<>();
        String sql = "SELECT * FROM file WHERE name LIKE '%" + text + "%';";
        ResultSet rs = conn.select(sql);
        try {
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String path = rs.getString("path");
                String size = rs.getString("size");
                String dir = rs.getString("belong");
                IFile file = new IFile(id, name, path, size, dir);
                map.put(id, file);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
        if (!map.isEmpty()) {
            String sqlFormat = "SELECT ft.file_id, ft.tag_id, t.name, t.'group' FROM file_tag ft LEFT JOIN tag t ON ft.tag_id = t.id WHERE file_id in (%s);";
            String sql2 = String.format(sqlFormat, map.keySet().stream().collect(Collectors.joining(",")));
            ResultSet rs2 = conn.select(sql2);
            try {
                while (rs2.next()) {
                    String file_id = rs2.getString("file_id");
                    String tag_id = rs2.getString("tag_id");
                    String name = rs2.getString("name");
                    String group = rs2.getString("group");
                    ITag tag = new ITag(tag_id, name, group);
                    map.get(file_id).add(tag);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                conn.close();
            }
            return map.values().stream().collect(Collectors.toList());
        } else {
            return null;
        }
    }
}
