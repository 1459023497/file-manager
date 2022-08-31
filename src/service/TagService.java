package service;

import jdbc.JDBCConnector;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

//文件标签服务，复制标签增删改查
public class TagService {
    private JDBCConnector conn;


    //标签组：标签
    private HashMap<String, Set<String>> tagMap = new HashMap<>();

    public TagService() {
    }

    public TagService(JDBCConnector conn) {
        this.conn = conn;
    }

    /**
     * 新建标签
     *
     * @param tag
     */
    public void newTag(String tag, String group) {
        conn.update("insert into tag values('" + tag + "','" + group + "');");
    }

    /**
     * 查所有标签
     *
     * @return
     */
    public ResultSet getAllTags() {
        return conn.select("select * from tag where \"group\" NOTNULL;");
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
                if (!rs.next()) break;
                String group = rs.getString("group");
                String name = rs.getString("name");
                //set不为空
                if (tagMap.get(group) != null) {
                    tagMap.get(group).add(name);
                } else {
                    HashSet<String> set = new HashSet<>();
                    set.add(name);
                    tagMap.put(group, set);
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
                if (!rs.next()) break;
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

    public ArrayList<String> getTagsByFile(File file) {
        String sql = "SELECT * FROM tag WHERE id IN (\n" +
                "SELECT tag_id FROM file_tag WHERE file_id=(\n" +
                "SELECT id FROM file WHERE path = '" + file.getPath() + "'));";
        ResultSet rs = conn.select(sql);
        ArrayList<String> tags = new ArrayList<>();
        while (true) {
            try {
                if (!rs.next()) break;
                tags.add(rs.getString("name"));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return tags;
    }


    /**
     * 给文件打标签,支持多文件
     *
     * @param tag
     * @param files
     */
    public void tag(String tag, File... files) {
        for (File file : files) {
            String sql = "INSERT into file_tag(file_id,tag_id) VALUES(\n" +
                    "(SELECT id from file where file.path = '" + file.getPath() + "'),\n" +
                    "(SELECT id FROM tag where tag.name = '" + tag + "'));";
            conn.update(sql);
        }
    }

    public void tag(String tag, String dir) {
        // TODO: 2022/8/30 给文件夹下的所有加便签
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
