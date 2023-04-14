package service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import common.tool.BeanUtils;
import common.tool.IdGenerator;
import entity.IFile;
import entity.ITag;
import jdbc.JDBCConnector;

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
        conn.update("insert into tag values('" + idGenerator.next() + "','" + tag.getName() + "','" + tag.getGroup()
                + "');");
    }

    /**
     * 删除标签，注意：需要相关标签的组设为无分组，移除相关文件的该标签
     * 
     * @param tag
     */
    public void deleteTag(ITag tag) {
        String sql = "DELETE FROM tag WHERE id = '" + tag.getId() + "';\n" +
                "DELETE FROM file_tag WHERE tag_id = '" + tag.getId() + "';\n" +
                "UPDATE tag set \"group\" = '无分组' WHERE \"group\" = '" + tag.getId() + "';";
        conn.update(sql);
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
                ITag tag = BeanUtils.getTag(rs);
                tags.add(tag);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
        return tags;
    }

    /**
     * 标签字典《标签id，标签》
     *
     */
    public HashMap<String, ITag> getTagsMap() {
        ResultSet rs = conn.select("select * from tag;");
        HashMap<String, ITag> tagMap = new HashMap<>();
        try {
            while (rs.next()) {
                ITag tag = BeanUtils.getTag(rs);
                tagMap.put(tag.getId(), tag);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
        return tagMap;

    }

    /**
     * 全部文件标签字典，map<文件id, set<ITag>>
     * 
     * @return
     */
    public HashMap<String, Set<ITag>> getFilesTagsMap() {
        HashMap<String, Set<ITag>> map = new HashMap<>();
        String sql = "SELECT f.file_id,f.tag_id as id,t.name,t.\"group\" FROM file_tag as f join tag  as t ON f.tag_id = t.id; ";
        ResultSet rs = conn.select(sql);
        try {
            while (rs.next()) {
                String fileId = rs.getString("file_id");
                ITag tag = BeanUtils.getTag(rs);
                map.computeIfAbsent(fileId, k-> new HashSet<>()).add(tag);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
        return map;
    }

    /**
     * 标签分组字典《标签id，set《标签id》》
     */
    public HashMap<String, Set<String>> getGroupsMap() {
        HashMap<String, ITag> tagMap = getTagsMap();
        HashMap<String, Set<String>> groupMap = new HashMap<>();
        tagMap.values().forEach(tag -> {
            String id = tag.getId();
            String group = tag.getGroup();
            if (groupMap.containsKey(group)) {
                // 已经放入该组
                groupMap.get(group).add(id);
            } else {
                // 还未放入组
                HashSet<String> set = new HashSet<>();
                if (group.equals("无分组")) {
                    // 这步判断是为了避免=》顶级标签后放入id会覆盖之前group对应的集合
                    if (groupMap.containsKey(id))
                        return;
                    groupMap.put(id, set);
                } else {
                    set.add(id);
                    groupMap.put(group, set);
                }
            }
        });
        return groupMap;
    }

    /**
     * 批量查询该标签下的文件及其标签，组成字典
     * 
     * @param tag
     * @return map 【文件id,【标签】】
     */
    public HashMap<String, Set<ITag>> getFileMapByTag(ITag tag) {
        String sql = "SELECT f.file_id,f.tag_id as id,t.name,t.\"group\" FROM file_tag as f join tag  as t ON f.tag_id = t.id WHERE f.file_id IN (\n"
                + "SELECT file_id FROM file_tag WHERE tag_id='" + tag.getId() + "');";
        ResultSet rs = conn.select(sql);
        HashMap<String, Set<ITag>> map = new HashMap<>();
        try {
            while (rs.next()) {
                String fileId = rs.getString("file_id");
                ITag tag2 = BeanUtils.getTag(rs);
                map.computeIfAbsent(fileId, key -> new HashSet<>()).add(tag2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
        return map;
    }

    /**
     * 根据标签查文件
     *
     * @param tag
     * @return map 【目录,【文件】】
     */
    public HashMap<String, Set<IFile>> getFilesByTag(ITag tag) {
        String sql = "SELECT * FROM file WHERE id IN (\n" +
                "SELECT file_id FROM file_tag WHERE tag_id='" + tag.getId() + "');";
        ResultSet rs = conn.select(sql);
        HashMap<String, Set<IFile>> files = new HashMap<>();
        try {
            while (rs.next()) {
                IFile file = BeanUtils.getFile(rs);
                String dir = file.getBelong();

                // 方法返回set
                files.computeIfAbsent(dir, k -> new HashSet<>()).add(file);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.close();
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
        try {
            while (rs.next()) {
                ITag tag = BeanUtils.getTag(rs);
                tags.add(tag);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.close();
        }
        return tags;
    }

    /**
     * 给单个文件打标签
     *
     * @param tag
     * @param file
     */
    public void tag(ITag tag, IFile file) {
        if (!file.isDirectory()) {
            // 单个文件
            String sql = "INSERT into file_tag(id,file_id,tag_id) VALUES('" + idGenerator.next() + "','" + file.getId()
                    + "','" + tag.getId() + "');";
            conn.update(sql);
        } else {
            // 文件夹
            // tag(tag, file.getPath());
        }
    }

    /**
     * 批量打标签
     */
    public void tags(Collection<ITag> tags, IFile file) {
        if (!file.isDirectory()) {
            // 单个文件
            StringBuffer sb = new StringBuffer();
            tags.forEach(tag -> {
                String sql = "INSERT into file_tag(id,file_id,tag_id) VALUES('" + idGenerator.next() + "','"
                        + file.getId() + "','" + tag.getId() + "');";
                sb.append(sql);
            });
            conn.update(sb.toString());
        } else {
            // 文件夹
            tag(tags, file.getPath());
        }
    }

    /**
     * 给文件夹下的文件打标签，批量操作
     *
     * @param tag
     * @param dir
     */
    public void tag(Collection<ITag> tags, String dir) {
        String sql = "SELECT id FROM file WHERE file.belong ='" + dir + "';";
        ResultSet rs = conn.select(sql);

        StringBuffer batch = new StringBuffer();
        try {
            while (rs.next()) {
                String fileId = rs.getString("id");
                tags.forEach(tag -> {
                    batch.append("INSERT INTO file_tag VALUES ('" + idGenerator.next() + "','" + fileId + "','"
                            + tag.getId() + "');");
                });
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.close();
        }
        conn.update(batch.toString());
    }

    /**
     * 删除文件的标签
     *
     * @param tag
     * @param files
     */
    public void removeTag(ITag tag, IFile... files) {
        for (IFile file : files) {
            String sql = "DELETE FROM file_tag WHERE file_id='" + file.getId() + "' AND tag_id ='" + tag.getId() + "';";
            conn.update(sql);
        }
    }

}
