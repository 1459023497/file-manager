package common.model.event.impl;

import common.model.event.FireEvent;
import common.myenum.EventType;

/**
 * @author yty
 * @date 2024/5/16
 * @description
 */
public class PagerEvent implements FireEvent {
    private String eventName;
    private String eventInfo;

    private Object object;
    private int pageAction;
    private int pageSize;
    private int pageNum;

    public PagerEvent(){
        this.eventName = null;
        this.eventInfo = null;
        this.object = null;
    }

    public PagerEvent(String eventName, String eventInfo, Object object) {
        this.eventName = eventName;
        this.eventInfo = eventInfo;
        this.object = object;
    }

    public PagerEvent(int pageAction, int pageNum, int pageSize) {
        this.pageAction = pageAction;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setEventInfo(String eventInfo) {
        this.eventInfo = eventInfo;
    }

    public Object getData() {
        return object;
    }

    public void setData(Object object) {
        this.object = object;
    }

    @Override
    public int getEventType() {
        return EventType.PAGE_EVENT;
    }

    @Override
    public String getEventName() {
        return eventName;
    }

    @Override
    public String getEventInfo() {
        return eventInfo;
    }

    @Override
    public Object getEventData() {
        return object;
    }

    public int getPageAction() {
        return pageAction;
    }

    public void setPageAction(int pageAction) {
        this.pageAction = pageAction;
    }
}
