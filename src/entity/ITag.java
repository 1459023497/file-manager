package entity;

import java.util.List;

import common.myenum.Status;

public class ITag {
    private String id;
    private String name;
    private String group;
    // the main tag of a file will be the folder where the file will be moved to
    private int isMain;
    private List<String> keys;
    private int status = Status.BROWSE;

    public ITag(String name){
        this.name = name;
    }

    public ITag(String name, String group){
        this.name = name;
        this.group = group;
    }

    public ITag(String id, String name, String group) {
        this.id = id;
        this.name = name;
        this.group = group;
    }

    public ITag(String id, String name, String group, int isMain) {
        this.id = id;
        this.name = name;
        this.group = group;
        this.isMain = isMain;
    }

    public ITag() {
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

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return name;
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    /**
     * judge it is main tag or not
     * @return 1: it is main tag 0: it is not main tag
     */
    public int getIsMain() {
        return isMain;
    }

    public void setIsMain(int isMain) {
        this.isMain = isMain;
    }
    
}
