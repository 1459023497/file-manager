package service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JTree;

import common.AppContext;
import org.apache.commons.collections4.CollectionUtils;

import common.myenum.Status;
import common.tool.BeanUtils;
import common.tool.IdGenerator;
import entity.IFile;
import entity.IFolder;
import entity.ITag;
import gui.base.TagTreeNode;
import jdbc.JDBCConnector;

public class TagService {
    private final JDBCConnector conn;
    private final IdGenerator idGenerator = new IdGenerator();

    public TagService() {
        conn = new JDBCConnector();
    }

    public void close() {
        conn.close();
    }

    public void newTag(ITag tag) {
        String sql = String.format("insert into tag values ('%s','%s','%s','%s');",idGenerator.next() ,tag.getName(), tag.getGroup(), AppContext.currSpace);
        conn.update(sql);
    }

    /**
     * delete tag ，notice：set sub-tags to no group，remove all file's tag
     *
     */
    public void deleteTag(ITag tag) {
        String sql = "DELETE FROM tag WHERE id = '" + tag.getId() + "';\n" +
                "DELETE FROM file_tag WHERE tag_id = '" + tag.getId() + "';\n" +
                "DELETE FROM tag_key WHERE tag_id = '" + tag.getId() + "';\n" +
                "UPDATE tag set \"group\" = '全部' WHERE \"group\" = '" + tag.getId() + "';";
        conn.update(sql);
    }

    /**
     * get all tags
     */
    public List<ITag> getAllTags() {
        List<Map<String, Object>> rs = conn.select("select * from tag;");
        ArrayList<ITag> tags = new ArrayList<>();
        rs.forEach(e->{
            ITag tag = BeanUtils.setTag(e);
            tags.add(tag);
        });
        return tags;
    }

    /**
     * map《id,tag》
     *
     */
    public Map<String, ITag> getTagsMap() {
        List<Map<String, Object>> rs = conn.select("select * from tag;");
        HashMap<String, ITag> tagMap = new HashMap<>();
        rs.forEach(e->{
            ITag tag = BeanUtils.setTag(e);
            tagMap.put(tag.getId(), tag);
        });
        return tagMap;
    }

    /**
     * all file with tags，map<fileId, tags>
     *
     */
    public Map<String, Set<ITag>> getFilesTagsMap() {
        HashMap<String, Set<ITag>> map = new HashMap<>();
        String sql = "SELECT f.file_id,f.tag_id as id,t.name,t.\"group\" FROM file_tag as f join tag  as t ON f.tag_id = t.id; ";
        List<Map<String, Object>> rs = conn.select(sql);
        rs.forEach(e->{
            String fileId = (String) e.get("file_id");
            ITag tag = BeanUtils.setTag(e);
            map.computeIfAbsent(fileId, k -> new HashSet<>()).add(tag);
        });
        return map;
    }

    /**
     * generates folders according to the tag structure
     *
     */
    public IFolder getTagFolder(String topPath) {
        IFolder topFolder = new IFolder(topPath);
        Map<String, ITag> tagMap = getTagsMap();
        Map<String, Set<String>> groupMap = getGroupsMap();
        // only recursive top tag
        groupMap.forEach((tagId, subTagIds) -> {
            ITag tag = tagMap.get(tagId);
            if (tag.getGroup().equals("全部")) {
                String path = topPath + "\\" + tag.getName();
                IFolder folder = new IFolder(path, tag.getName(), tag.getId());
                topFolder.addSubFolder(folder);
                // find sub-tags by recursion
                subTagIds.forEach(subTagId -> findSub(groupMap, tagMap, subTagId, folder));
            }
        });
        return topFolder;
    }

    /**
     * this method will create a JTree of the tags according to the group structure
     * 
     * @return JTree of the ITag
     */
    public JTree getTagTree() {
        // root node
        ITag rootTag = new ITag("全部", "全部");
        TagTreeNode root = new TagTreeNode(rootTag);
        Map<String, ITag> tagMap = getTagsMap();
        Map<String, Set<String>> groupMap = getGroupsMap();
        groupMap.forEach((tagId, subTagIds) -> {
            ITag tag = tagMap.get(tagId);
            if (tag.getGroup().equals("全部")) {
                // sub node
                TagTreeNode sub = new TagTreeNode(tag);
                root.add(sub);
                // find sub-tags by recursion
                subTagIds.forEach(subTagId -> findSub(groupMap, tagMap, subTagId, sub));
            }
        });
        return new JTree(root);
    }

    /**
     * recursion method for JTree
     */
    private void findSub(Map<String, Set<String>> groupMap, Map<String, ITag> tagMap, String tagId,
            TagTreeNode node) {
        ITag tag = tagMap.get(tagId);
        TagTreeNode subNode = new TagTreeNode(tag);
        if (CollectionUtils.isNotEmpty(groupMap.get(tagId))) {
            groupMap.get(tagId).forEach(subTagId -> {
                // if it has sub-tag, inner recursion
                findSub(groupMap, tagMap, subTagId, subNode);
            });
        }
        // add itself to father node
        node.add(subNode);
    }

    /**
     * recursion method for IFolder
     */
    public void findSub(Map<String, Set<String>> groupMap, Map<String, ITag> tagMap, String tagId, IFolder folder) {
        ITag tag = tagMap.get(tagId);
        String path = folder.getPath() + "\\" + tag.getName();
        IFolder subFolder = new IFolder(path, tag.getName(), tag.getId());
        if (CollectionUtils.isNotEmpty(groupMap.get(tagId))) {
            groupMap.get(tagId).forEach(subTagId -> {
                // if it has sub-tag, inner recursion
                findSub(groupMap, tagMap, subTagId, subFolder);
            });
        }
        // add itself to father folder
        folder.addSubFolder(subFolder);
    }

    /**
     * tag group, map《tagId，sub-tagId》
     */
    public Map<String, Set<String>> getGroupsMap() {
        Map<String, ITag> tagMap = getTagsMap();
        Map<String, Set<String>> groupMap = new HashMap<>();
        tagMap.values().forEach(tag -> {
            String id = tag.getId();
            String group = tag.getGroup();
            if (groupMap.containsKey(group)) {
                // already grouped
                groupMap.get(group).add(id);
            } else {
                // not grouped
                HashSet<String> set = new HashSet<>();
                if (group.equals("全部")) {
                    // here is to avoid : put top tag will cover old group set
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
     * get file's tags map by a tag
     * 
     * @return map 【fileId,tags】
     */
    public Map<String, Set<ITag>> getFileTagMapByTag(ITag tag) {
        String sql = "SELECT f.file_id,f.tag_id as id,t.name,t.\"group\",is_main FROM file_tag as f join tag  as t ON f.tag_id = t.id WHERE f.file_id IN (\n"
                + "SELECT file_id FROM file_tag WHERE tag_id='" + tag.getId() + "');";
        List<Map<String, Object>> rs = conn.select(sql);
        HashMap<String, Set<ITag>> map = new HashMap<>();
        rs.forEach(e->{
            String fileId = (String) e.get("file_id");
            ITag tag2 = BeanUtils.setTag(e);
            map.computeIfAbsent(fileId, key -> new HashSet<>()).add(tag2);
        });
        return map;
    }

    /**
     * get file with tags by a specific tag
     *
     * @return map 【folder,files】
     */
    public List<IFile> getFilesMapByTag(ITag tag) {
        String sql = "SELECT * FROM file WHERE id IN (\n" +
                "SELECT file_id FROM file_tag WHERE tag_id='" + tag.getId() + "');";
        List<Map<String, Object>> rs = conn.select(sql);
        // <id, file>
        Map<String, IFile> fileMap = new HashMap<>();
        rs.forEach(e->{
            IFile file = BeanUtils.setFile(e);
            fileMap.put(file.getId(), file);
        });
        Map<String, Set<ITag>> tagMap = getFileTagMapByTag(tag);
        tagMap.forEach((k, v) -> {
            IFile file = fileMap.get(k);
            if (file != null) {
                v.forEach(file::add);
            }
        });
        return new ArrayList<>(fileMap.values());
    }

    /**
     * get file's tags
     *
     */
    public Set<ITag> getTagsByFile(IFile file) {
        String sql = "SELECT * FROM tag WHERE id IN (\n" +
                "SELECT tag_id FROM file_tag WHERE file_id='" + file.getId() + "');";
        List<Map<String, Object>> rs = conn.select(sql);
        HashSet<ITag> tags = new HashSet<>();
        rs.forEach(e->{
            ITag tag = BeanUtils.setTag(e);
            tags.add(tag);
        });
        return tags;
    }

    /**
     * batch tag
     */
    public void tags(Collection<ITag> tags, IFile file) {
        if (!file.isDirectory()) {
            StringBuffer sb = new StringBuffer();
            tags.forEach(tag -> {
                // here case when : if first tag a file, it will select the first tag as default
                // main tag
                String sql = "INSERT into file_tag(id,file_id,tag_id,is_main) VALUES('" + idGenerator.next() + "','"
                        + file.getId() + "','" + tag.getId()
                        + "',(case when (select count(*) from file_tag where file_id = '" + file.getId()
                        + "' and is_main = 1) > 0 then 0 else 1 end));";
                sb.append(sql);
            });
            conn.update(sb.toString());
        } else {
            // folder
            tag(tags, file.getPath());
        }
    }

    /**
     * batch add tags to all files i the folder
     *
     * @param tags
     * @param dir
     */
    public void tag(Collection<ITag> tags, String dir) {
        String sql = "SELECT id FROM file WHERE file.belong ='" + dir + "';";
        List<Map<String, Object>> rs = conn.select(sql);

        StringBuffer batch = new StringBuffer();
        rs.forEach(e->{
            String fileId = (String) e.get("id");
            tags.forEach(tag -> {
                batch.append("INSERT INTO file_tag VALUES ('" + idGenerator.next() + "','" + fileId + "','"
                        + tag.getId() + "',(case when (select count(*) from file_tag where file_id = '" + fileId
                        + "' and is_main = 1) > 0 then 0 else 1 end));");
            });
        });
        conn.update(batch.toString());
    }

    /**
     * deletes tag from files
     *
     */
    public void removeTag(ITag tag, IFile... files) {
        for (IFile file : files) {
            String sql = "DELETE FROM file_tag WHERE file_id='" + file.getId() + "' AND tag_id ='" + tag.getId() + "';";
            conn.update(sql);
        }
    }

    /**
     * add file's new tags if its status is inserted
     *
     */
    public void tag(List<IFile> files) {
        for (IFile file : files) {
            List<ITag> tags = file.getTags().stream().filter(e -> e.getStatus() == Status.INSERT).collect(Collectors.toList());
            tags(tags, file);
        }
    }

    /**
     * add keys to tag
     *
     */
    public void tagKeys(String tagId, String[] keys) {
        StringBuffer batch = new StringBuffer();
        for (String key : keys) {
            batch.append("INSERT INTO tag_key VALUES ('" + idGenerator.next() + "','" + tagId + "','" + key + "');");
        }
        conn.update(batch.toString());
    }

    /**
     * get all tags with keys
     * 
     */
    public List<ITag> getAllTagsWithKeys() {
        // concat keys
        List<Map<String, Object>> rs = conn.select(
                "select t.id,t.name,t.'group', GROUP_CONCAT(tk.key) as key from tag t left join tag_key tk on t.id = tk.tag_id group by t.id;");
        List<ITag> tags = new ArrayList<>();
        rs.forEach(e->{
            ITag tag = BeanUtils.setTagWithKey(e);
            tags.add(tag);
        });
        return tags;
    }

    /**
     * delete key from tag
     */
    public void deleteTagKey(String tagId, String key) {
        String sql = "delete from tag_key where tag_id = '" + tagId + "' and key = '" + key + "';";
        conn.update(sql);
    }

    public List<IFile> getFilesByTag(String tagId) {
        String sql = "SELECT * FROM file WHERE id IN (SELECT file_id FROM file_tag WHERE tag_id='" + tagId + "');";
        return getFiles(sql);
    }

    public void renameTag(ITag tag) {
        String sql = "UPDATE tag set name = '" + tag.getName() + "' WHERE id = '" + tag.getId() + "'";
        conn.update(sql);
    }

    public void setMainTag(ITag tag, IFile file) {
        String sql = "update file_tag set is_main = 0 where file_id = '" + file.getId()
                + "' and is_main = 1; update file_tag set is_main = 1 where file_id = '"
                + file.getId() + "' and tag_id = '" + tag.getId() + "';";
        conn.update(sql);
    }

    /**
     * return the files that its main tag is this tag
     *
     */
    public List<IFile> getFilesByMainTag(String tagId) {
        String sql = "SELECT * FROM file WHERE id IN (SELECT file_id FROM file_tag WHERE tag_id='" + tagId
                + "' and is_main = 1);";
        return getFiles(sql);
    }

    private List<IFile> getFiles(String sql) {
        List<Map<String, Object>> rs = conn.select(sql);
        List<IFile> files = new ArrayList<>();
        rs.forEach(e-> files.add(BeanUtils.setFile(e)));
        return files;
    }
}
