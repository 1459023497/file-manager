package entity;

import java.io.File;

public class IFile {
    private String id;
    private String name;
    private String path;
    private String size;
    private String belong;
    private boolean isDirectory = false;//默认是文件夹

    public IFile(){
        id = new String();
        name = new String();
        path = new String();
        size = new String();
        belong = new String();
    }

    /**
     * 文件夹构造器
     * @param path
     */
    public IFile(String path){
        this.path = path;
        isDirectory = true;
    }

    /**
     * 文件构造器
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
    
    
}
