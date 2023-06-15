package entity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.Desktop;

public class IFile {
    private String id;
    private String name;
    private String path;
    private String size;
    private String belong;
    private boolean isDirectory = false;//default is folder
    private List<ITag> tags;

    public IFile(){
        id = new String();
        name = new String();
        path = new String();
        size = new String();
        belong = new String();
        tags = new ArrayList<ITag>();
    }

    /**
     * folder constructor
     * @param path
     */
    public IFile(String path){
        this.path = path;
        isDirectory = true;
    }

    /**
     * file constructor
     * @param id
     * @param name
     * @param path
     * @param size
     * @param belong
     */
    public IFile(String id, String name, String path, String size, String belong) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.size = size;
        this.belong = belong;
        tags = new ArrayList<ITag>();
    }

    /**
     * 文件转化自定义
     * @param file
     */
    public IFile(File file){
        this.name = file.getName();
        this.path = file.getPath();
        this.size = String.valueOf(file.length());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getBelong() {
        return belong;
    }

    public void setBelong(String belong) {
        this.belong = belong;
    }
    
    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    public List<ITag> getTags() {
        return tags;
    }

    public void setTags(List<ITag> tags) {
        this.tags = tags;
    }

    public void add(ITag tag){
        tags.add(tag);
    }

    /**
    * @param file
    * @return true if file exists, false otherwise
    */
   public boolean isExist() {
       File f = new File(path);
       if (f.exists()){
           return true;
       }
       return false;
   }

   /**
    * open file
    */
   public boolean open(){
    File file = new File(path);
    if (!file.exists())
        return false;
    try {
        Desktop.getDesktop().open(file);
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
    return true;
   }
    
}
