package entity;

import java.util.List;

import common.myenum.Status;

/* 
 * 标签
 */
public class ITag {
    private String id;
    private String name;
    private String group;
    private List<String> keys;
    private Status status = Status.BROWSE;

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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
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

    
    
}
