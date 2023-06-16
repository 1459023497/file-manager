package entity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import service.FileService;
import service.TagService;

public class IFolder {
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

    public void moveFiles() {
        if (tagId != null && path != null) {
            List<IFile> files = tagService.getFilesByMainTag(tagId);
            // update file path and belong folder
            files.forEach(file -> {
                String sourceFilePath = file.getPath();
                String targetFilePath = path + "\\" + file.getName();
                File sourceFile = new File(sourceFilePath);
                File targetFile = new File(targetFilePath);
                // move file to new path
                boolean success = sourceFile.renameTo(targetFile);
                if (success) {
                    file.setPath(path + "\\" + file.getName());
                    file.setBelong(path);
                } else {
                    System.out.println("文件移动失败: " + sourceFilePath);
                }
            });
            fileService.updateFiles(files);
        }
        // recursion
        if (CollectionUtils.isNotEmpty(folders)) {
            folders.forEach(IFolder::moveFiles);
        }
    }
}