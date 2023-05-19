package entity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

public class IFolder {
    String name;
    String path;
    List<IFolder> folders;

    public IFolder() {
    }

    public IFolder(String path) {
        this.path = path;
    }

    public IFolder(String path, String name) {
        this.path = path;
        this.name = name;
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
    public void generate(){
        File folder = new File(path);
        if(!folder.exists()){
            folder.mkdirs();
        }
        //recursion
        if(CollectionUtils.isNotEmpty(folders)){
            folders.forEach(IFolder::generate);
        }
        
    }
}