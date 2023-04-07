package common;

import java.util.HashMap;
import java.util.Map;

import gui.window.Home;

/*
 * 应用上下文
 */
public class AppContext {
    private static Map<String, Object> map;

    public static void init(){
        map = new HashMap<String,Object>();
    }

    public static Object getKey(String key) {
        return map.get(key);
    }

    public static void setKey(String key, Object value) {
        map.put(key, value);
    }
    
    public static void removeKey(String key) {
        map.remove(key);
    }

    /*
     * 获取主页
     */
    public static Home getHome() {
        return (Home) map.get(Home.WIN_NAME);
    }
}
