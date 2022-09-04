package service;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import jdbc.JDBCConnector;
import tool.IdGenerator;

//文件标签服务，复制标签增删改查
public class TagService {
    private JDBCConnector conn;
    // 标签组：标签
    private HashMap<String, Set<String>> tagMap = new HashMap<>();
    private IdGenerator id = new IdGenerator();

    public TagService() {
        conn = new JDBCConnector();
    }

    public void close() {
        conn.close();
    }

    /**
     * 新建标签
     *
     * @param tag
     * @param group
     */
    public void newTag(String tag, String group) {
        if (group == null) {
            conn.update("insert into tag(id,name) values('" + id.next() + "','" + tag + "');");
        } else {
            conn.update("insert into tag values('" + id.next() + "','" + tag + "','" + group + "');");
        }
    }

    /**
     * 查所有标签
     *
     * @return
     */
    public ResultSet getAllTags() {
        return conn.select("select * from tag;");
    }

    /**
     * 获取所有标签组
     * 
     * @return
     */
    public ArrayList<String> getAllGroups() {
        ResultSet rs = conn.select("SELECT * FROM tag;");
        ArrayList<String> groups = new ArrayList<>();
        try {
            while (rs.next()) {
                groups.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }

    /**
     * 标签字典
     *
     * @return
     */
    public HashMap<String, Set<String>> getTagsMap() {
        ResultSet rs = getAllTags();
        while (true) {
            try {
                if (!rs.next())
                    break;
                String name = rs.getString("name");
                String group = rs.getString("group");
                // set不为空
                if (tagMap.get(group) != null) {
                    tagMap.get(group).add(name);
                } else {
                    HashSet<String> set = new HashSet<>();
                    // 如果是顶级便签（无分组）,放入名,空集
                    if (group.equals("无分组")) {
                        tagMap.put(name, set);
                    } else {
                        set.add(name);
                        tagMap.put(group, set);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return tagMap;
    }

    /**
     * 根据标签查文件
     *
     * @param tag
     * @return
     */
    public HashMap<String, Set<File>> getFilesByTag(String tag) {
        String sql = "SELECT * FROM file WHERE id IN (\n" +
                "SELECT file_id FROM file_tag WHERE tag_id=(\n" +
                "SELECT id FROM tag WHERE name = '" + tag + "'));";
        ResultSet rs = conn.select(sql);
        HashMap<String, Set<File>> files = new HashMap<>();
        while (true) {
            try {
                if (!rs.next())
                    break;
                String dir = rs.getString("belong");
                String path = rs.getString("path");
                if (files.get(dir) == null) {
                    Set<File> fileSet = new HashSet<>();
                    fileSet.add(new File(path));
                    files.put(dir, fileSet);
                } else {
                    files.get(dir).add(new File(path));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return files;
    }

    /**
     * 获取文件的标签
     * 
     * @param file
     * @return
     */
    public ArrayList<String> getTagsByFile(File file) {
        String sql = "SELECT * FROM tag WHERE id IN (\n" +
                "SELECT tag_id FROM file_tag WHERE file_id=(\n" +
                "SELECT id FROM file WHERE path = '" + file.getPath() + "'));";
        ResultSet rs = conn.select(sql);
        ArrayList<String> tags = new ArrayList<>();
        while (true) {
            try {
                if (!rs.next())
                    break;
                tags.add(rs.getString("name"));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return tags;
    }

    /**
     * 给文件打标签
     *
     * @param tag
     * @param file
     */
    public void tag(String tag, File file) {
        if (!file.isDirectory()) {
            // 单个文件
            String sql = "INSERT into file_tag(id,file_id,tag_id) VALUES('" + id.next() + "',\n" +
                    "(SELECT id from file where file.path = '" + file.getPath() + "'),\n" +
                    "(SELECT id FROM tag where tag.name = '" + tag + "'));";
            conn.update(sql);
        } else {
            // 文件夹
            tag(tag, file.getPath());
        }
    }

    /**
     * 给文件夹下的文件打标签
     *
     * @param tag
     * @param dir
     */
    public void tag(String tag, String dir) {
        String sql = "SELECT * FROM file WHERE file.belong ='" + dir + "'";
        ResultSet rs = conn.select(sql);
        while (true) {
            try {
                if (!rs.next())
                    break;
                tag(tag, new File(rs.getString("path")));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 删除文件的标签
     *
     * @param tag
     * @param files
     */
    public void removeTag(String tag, File... files) {
        for (File file : files) {
            String sql = "DELETE FROM file_tag WHERE file_id=\n" +
                    "(SELECT id from file where file.path = '" + file + "') AND tag_id =\n" +
                    "(SELECT id FROM tag where tag.name = '" + tag + "');";
            conn.update(sql);
        }

    }

}
