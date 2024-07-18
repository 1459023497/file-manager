package common.model.observer;

import common.model.event.FireEvent;

/**
 * @author yty
 * @date 2024/5/16
 * @description Observer Pattern
 */
public interface  Observer {
    void update(FireEvent event);
}
