package common.model.event;

/**
 * @author yty
 * @date 2024/5/16
 * @description
 */
public interface FireEvent {
    int getEventType();
    String getEventName();
    String getEventInfo();
    Object getEventData();

}
