package service;

import common.AppContext;
import common.tool.BeanUtils;
import common.tool.IdGenerator;
import entity.IFile;
import entity.ITag;
import jdbc.JDBCConnector;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FileService {
    private static final Logger logger = Logger.getLogger("FileService.class");
    private final JDBCConnector conn;
    private final IdGenerator idGenerator;

    public FileService() {
        conn = new JDBCConnector();
        idGenerator = new IdGenerator();
    }

    /**
     * adding file to database
     *
     * @param belong folder
     */
    public void addFile(File file, String belong) {
        // String regex = "/\''/g"; remove all '
        String name = file.getName().replace('\'', ' ');
        String path = file.getPath().replace('\'', ' ');
        // get last modified time
        long lastModified = file.lastModified();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = sdf.format(lastModified);
        String sql = "INSERT or ignore into file(id,name,path,size,belong,modify_time,space_id) values('" + idGenerator.next() + "','"
                + name + "','" + path + "','" + file.length() + "','" + belong + "','" + datetime + "','" + AppContext.currSpace + "');";
        conn.update(sql);
    }

    /**
     * rename the file
     */
    public void renameFile(IFile iFile) {
        // rename the real file
        File file = new File(iFile.getPath());
        File newFile = new File(file.getParent(), iFile.getName());
        file.renameTo(newFile);

        // rename the file in the database
        String id = iFile.getId();
        String t = "UPDATE file SET name = '%s',path='%s' WHERE id = '%s';";
        String sql = String.format(t,newFile.getName(),newFile.getPath(),id );
        conn.update(sql);
        // reset
        iFile.setPath(newFile.getPath());
    }

    /**
     * delete the file
     */
    public void removeFile(IFile file) {
        if (file.isDirectory()) {
            removeDir(file.getPath());
        } else {
            Path path = Paths.get(file.getPath());
            try {
                Files.delete(path);
            } catch (IOException e) {
                logger.info("删除文件失败：" + file.getName());
                throw new RuntimeException(e);
            }
            String t = "DELETE FROM file WHERE id = '%s'; DELETE FROM file_tag WHERE file_id = '%s';";
            String sql = String.format(t, file.getId(), file.getId());
            conn.update(sql);
        }
    }

    /**
     * delete the folder
     */
    public void removeDir(String dir) {
        // delete relative tags first
        String sql = "DELETE FROM file_tag WHERE file_id in (SELECT id FROM file WHERE belong = '" + dir + "');";
        String sql1 = "DELETE FROM file WHERE belong = '" + dir + "';";
        conn.update(sql);
        conn.update(sql1);

    }

    /**
     * open the file
     *
     * @return true: exist, false: not exist
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

    /*
     * check repeat file
     *
     * @return <same size，files>
     */
    public Map<String, List<IFile>> getRepeatMap(int pageSize, int pageNum) {
        Map<String, List<IFile>> map = new HashMap<>();
        List<IFile> files = getRepeatFiles(pageSize, pageNum);
        if (!files.isEmpty()) {
            map = files.stream().collect(Collectors.groupingBy(IFile::getSize));
        }
        return map;
    }

    public List<IFile> getRepeatFiles(int pageSize, int pageNum) {
        int offset = (pageNum - 1) * pageSize;
        String sql = String.format("SELECT * FROM file WHERE space_id='%s' AND size " +
                "IN (SELECT size FROM file WHERE space_id='%s' GROUP BY size " +
                "HAVING COUNT(*) > 1) ORDER BY size DESC LIMIT %s,%s;", AppContext.currSpace, AppContext.currSpace, offset, pageSize);
        List<Map<String, Object>> rows = conn.select(sql);
        return rows.stream().map(BeanUtils::setFile).collect(Collectors.toList());
    }

    /**
     * count files in the space
     */
    public int totalCount() {
        String sql = String.format("SELECT COUNT(1) AS total FROM file WHERE space_id = '%s';", AppContext.currSpace);
        List<Map<String, Object>> rs = conn.select(sql);
        return rs.isEmpty() ? 0 : (int) rs.get(0).get("total");
    }

    /**
     * get all files with tags
     */
    public List<IFile> getAllFiles() {
        String sql = String.format("SELECT * FROM file WHERE space_id = '%s' limit 1000;", AppContext.currSpace);
        return getFilesWithTags(sql);
    }

    /**
     * fuzzy search, find file's name OR file's tag name like the input text
     */
    public List<IFile> search(String text) {
        String sql = "SELECT * FROM file WHERE space_id ='" + AppContext.currSpace + "' and (id in (SELECT DISTINCT file_id FROM file_tag LEFT JOIN tag ON file_tag.tag_id = tag.id WHERE tag.name LIKE '%" + text + "%') OR file.name LIKE '%" + text + "%');";
        return getFilesWithTags(sql);
    }

    /**
     * get all files with tags
     *
     * @param sql select files sql
     */
    private List<IFile> getFilesWithTags(String sql) {
        List<Map<String, Object>> rs = conn.select(sql);
        Map<String, IFile> map = new HashMap<>();
        rs.forEach(e -> {
            IFile file = BeanUtils.setFile(e);
            map.put(file.getId(), file);
        });

        if (!map.isEmpty()) {
            String sqlFormat = "SELECT ft.file_id, ft.tag_id as id, t.name, t.'group', ft.is_main FROM file_tag ft LEFT JOIN tag t ON ft.tag_id = t.id WHERE file_id in (%s);";
            String sql2 = String.format(sqlFormat, String.join(",", map.keySet()));
            List<Map<String, Object>> rs2 = conn.select(sql2);
            rs2.forEach(e -> {
                String fileId = (String) e.get("file_id");
                ITag tag = BeanUtils.setTag(e);
                map.get(fileId).add(tag);
            });
            return new ArrayList<>(map.values());
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * @return untagged files
     */
    public List<IFile> getUntaggedFiles() {
        String sql = String.format("SELECT * FROM file WHERE id not in (SELECT DISTINCT file_id FROM file_tag) AND space_id = '%s';", AppContext.currSpace);
        List<IFile> files = new ArrayList<>();
        List<Map<String, Object>> rs = conn.select(sql);
        rs.forEach(e -> {
            IFile file = BeanUtils.setFile(e);
            files.add(file);
        });
        return files;
    }

    public void updateFiles(List<IFile> files) {
        StringBuffer buffer = new StringBuffer();
        files.forEach(file -> {
            String sql = "UPDATE file SET name = '" + file.getName() + "',path='" + file.getPath() + "', belong = '"
                    + file.getBelong() + "' WHERE id = '" + file.getId() + "';";
            buffer.append(sql);
        });
        conn.update(buffer.toString());
    }

    public List<IFile> getFilesByPage(int pageSize, int pageNum) {
        int offset = (pageNum - 1) * pageSize;
        String sql = String.format("SELECT * FROM file where space_id = '%s' limit %s,%s", AppContext.currSpace, offset, pageSize);
        return getFilesWithTags(sql);
    }

    /**
     * count the number of files with same size
     *
     */
    public int repeatTotalCount() {
        String sql = String.format("SELECT SUM(count) as total FROM (SELECT size, COUNT(1) AS count\n" +
                "FROM file WHERE space_id='%s' GROUP BY size HAVING COUNT(1) > 1) as t;", AppContext.currSpace);
        List<Map<String, Object>> rows = conn.select(sql);
        return rows.isEmpty() ? 0 : (int) rows.get(0).get("total");
    }

    public void removeFiles(List<IFile> list) {
        for (IFile item : list) {
            removeFile(item);
        }
    }
}
