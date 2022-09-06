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
    public void newTag(ITag tag) {
        conn.update("insert into tag values('" + id.next() + "','" + tag.getName() + "','" + tag.getGroup() + "');");
    }

    /**
     * 查所有标签
     *
     * @return
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
     * @return
     */
    public HashMap<String, ITag> getTagsMap(){
        ResultSet rs = conn.select("select * from tag;");
        HashMap<String,ITag> tagMap = new HashMap<>();
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
     *标签分组字典《id，set《id》》
     *
     * @return
     */
    public HashMap<String, Set<String>> getGroupsMap() {
        HashMap<String, ITag> tagMap = getTagsMap();
        HashMap<String, Set<String>> grouMap = new HashMap<>();
        tagMap.values().forEach(tag->{
            String id = tag.getId();
            String group = tag.getGroup();
            if(grouMap.get(group) != null){
                grouMap.get(group).add(id);
            }else{
                HashSet<String> set = new HashSet<>();
                 // 如果是顶级便签（无分组）,id作为key,值为空集
                if(group.equals("无分组")){
                    grouMap.put(id, set);
                }else{
                    set.add(id);
                    grouMap.put(group, set);
                }
            }
        });
        return grouMap;
    }

    /**
     * 根据标签查文件
     *
     * @param tag
     * @return
     */
    public HashMap<String, Set<IFile>> getFilesByTag(ITag tag) {
        String sql = "SELECT * FROM file WHERE id IN (\n" +
                "SELECT file_id FROM file_tag WHERE tag_id='" + tag.getId() + "';";
        ResultSet rs = conn.select(sql);
        HashMap<String, Set<IFile>> files = new HashMap<>();
        while (true) {
            try {
                if (!rs.next())
                    break;
                String id = rs.getString("id");
                String name = rs.getString("name");
                String path = rs.getString("path");
                String size = rs.getString("path");
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
    public ArrayList<String> getTagsByFile(IFile file) {
        String sql = "SELECT * FROM tag WHERE id IN (\n" +
                "SELECT tag_id FROM file_tag WHERE file_id='" + file.getId() + "');";
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
    public void tag(String tag, IFile file) {
        if (!file.isDirectory()) {
            // 单个文件
            String sql = "INSERT into file_tag(id,file_id,tag_id) VALUES('" + id.next() + "','" +file.getId() + "'),\n" +
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
