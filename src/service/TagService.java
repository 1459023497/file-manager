package service;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import entity.IFile;
import entity.ITag;
import jdbc.JDBCConnector;
import tool.IdGenerator;

//文件标签服务，复制标签增删改查
public class TagService {
    private JDBCConnector conn;
    private IdGenerator idGenerator = new IdGenerator();

    public TagService() {
        conn = new JDBCConnector();
    }

    public void close() {
        conn.close();
    }

    /**
     * 新建标签
     *
     */
    public void newTag(ITag tag) {
        conn.update("insert into tag values('" + idGenerator.next() + "','" + tag.getName() + "','" + tag.getGroup() + "');");
    }

    /**
     * 查所有标签
     *
     */
    public ArrayList<ITag> getAllTags() {
        ResultSet rs = conn.select("select * from tag;");
        ArrayList<ITag> tags = new ArrayList<>();
        try {
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String group = rs.getString("group");
                ITag tag = new ITag(id, name, group);
                tags.add(tag);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tags;
    }

    /**
     * 标签字典《id，标签》
     *
     */
    public HashMap<String, ITag> getTagsMap() {
        ResultSet rs = conn.select("select * from tag;");
        HashMap<String, ITag> tagMap = new HashMap<>();
        try {
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String group = rs.getString("group");
                ITag tag = new ITag(id, name, group);
                tagMap.put(id, tag);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tagMap;

    }

    /**
     * 标签分组字典《id，set《id》》
     */
    public HashMap<String, Set<String>> getGroupsMap() {
        HashMap<String, ITag> tagMap = getTagsMap();
        HashMap<String, Set<String>> groupMap = new HashMap<>();
        tagMap.values().forEach(tag -> {
            String id = tag.getId();
            String group = tag.getGroup();
            if (groupMap.containsKey(group)) {
                //已经放入该组
                groupMap.get(group).add(id);
            } else {
                //还未放入组
                HashSet<String> set = new HashSet<>();
                if (group.equals("无分组")) {
                    //这步判断是为了避免=》顶级标签后放入id会覆盖之前group对应的集合
                    if(groupMap.containsKey(id)) return;
                    groupMap.put(id, set);
                }else{
                    set.add(id);
                    groupMap.put(group, set);
                }  
            }
        });
        return groupMap;
    }

    /**
     * 根据标签查文件
     *
     * @param tag
     * @return
     */
    public HashMap<String, Set<IFile>> getFilesByTag(ITag tag) {
        String sql = "SELECT * FROM file WHERE id IN (\n" +
                "SELECT file_id FROM file_tag WHERE tag_id='" + tag.getId() + "');";
        ResultSet rs = conn.select(sql);
        HashMap<String, Set<IFile>> files = new HashMap<>();
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
                if (files.get(dir) == null) {
                    Set<IFile> fileSet = new HashSet<>();
                    fileSet.add(file);
                    files.put(dir, fileSet);
                } else {
                    files.get(dir).add(file);
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
    public HashSet<ITag> getTagsByFile(IFile file) {
        String sql = "SELECT * FROM tag WHERE id IN (\n" +
                "SELECT tag_id FROM file_tag WHERE file_id='" + file.getId() + "');";
        ResultSet rs = conn.select(sql);
        HashSet<ITag> tags = new HashSet<>();
        while (true) {
            try {
                if (!rs.next())
                    break;
                String id = rs.getString("id");
                String name = rs.getString("name");
                String group = rs.getString("group");
                ITag tag = new ITag(id, name, group);
                tags.add(tag);
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
    public void tag(ITag tag, IFile file) {
        if (!file.isDirectory()) {
            // 单个文件
            String sql = "INSERT into file_tag(id,file_id,tag_id) VALUES('" + idGenerator.next() + "','" + file.getId() + "','"+tag.getId()+"');";
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
    public void tag(ITag tag, String dir) {
        String sql = "SELECT id FROM file WHERE file.belong ='" + dir + "'";
        ResultSet rs = conn.select(sql);
        while (true) {
            try {
                if (!rs.next())
                    break;
                IFile file = new IFile();
                file.setId(rs.getString("id"));
                tag(tag, file);
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
