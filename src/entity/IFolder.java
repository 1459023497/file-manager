package entity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.collections4.CollectionUtils;

import service.FileService;
import service.TagService;

public class IFolder {
    private static final Logger logger = Logger.getLogger("IFolder.class");

    // one tag for one folder
    String tagId;
    String name;
    String path;
    List<IFolder> folders;
    TagService tagService = new TagService();
    FileService fileService = new FileService();

    public IFolder() {
    }

    public IFolder(String path) {
        this.path = path;
    }

    public IFolder(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public IFolder(String path, String name, String tagId) {
        this.path = path;
        this.name = name;
        this.tagId = tagId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<IFolder> getFolders() {
        return folders;
    }

    public void setFolders(List<IFolder> folders) {
        this.folders = folders;
    }

    public void addSubFolder(IFolder folder) {
        if (folders == null) {
            folders = new ArrayList<IFolder>();
        }
        folders.add(folder);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * generate real folder
     */
    public void generate() {
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // recursion
        if (CollectionUtils.isNotEmpty(folders)) {
            folders.forEach(IFolder::generate);
        }

    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    /**
     * this is a recursive function that moves files to their folder and records the failure message
     * @param result failure files
     */
    public void moveFiles(List<String> result) {
        if (tagId != null && path != null) {
            List<IFile> files = tagService.getFilesByMainTag(tagId);
            // update file path and belong folder
            files.forEach(file -> {
                int index = 1;
                String oldName = file.getName();
                int dotIndex = oldName.lastIndexOf(".");
                String prefix = oldName.substring(0, dotIndex);
                String suffix = oldName.substring(dotIndex);
                String sourceFilePath = file.getPath();
                String targetFilePath = path + "\\" + oldName;
                // continue if it already moved
                if (sourceFilePath.equals(targetFilePath)) {
                    return;
                }
                File sourceFile = new File(sourceFilePath);
                // avoid duplicated name
                String newName = oldName;
                while (fileExists(targetFilePath)) {
                    newName = prefix + "(" + index + ")" + suffix;
                    targetFilePath = path + "\\" + newName;
                    index++;
                }
                File targetFile = new File(targetFilePath);
                // move file to new path
                boolean moveSuccess = sourceFile.renameTo(targetFile);
                if (moveSuccess) {
                    file.setName(newName);
                    file.setPath(targetFilePath);
                    file.setBelong(path);
                } else {
                    logger.severe("文件移动失败，请检查文件路径是否存在： " + sourceFilePath);
                }
            });
            fileService.updateFiles(files);
        }
        // recursion
        if (CollectionUtils.isNotEmpty(folders)) {
            folders.forEach(e->e.moveFiles(result));
        }
    }

    private boolean fileExists(String path) {
        File file = new File(path);
        return file.exists();
    }
}