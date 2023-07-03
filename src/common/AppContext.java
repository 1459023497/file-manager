package common;

import java.util.HashMap;
import java.util.Map;

import gui.window.Home;
import gui.window.Tag;

/*
 * application context
 */
public class AppContext {
    // global settings
    public static boolean UI_TRANSPARENT = true;

    private static Map<String, Object> map;

    public static Object currentFrame;

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

    public static Home getHome() {
        return (Home) map.get(Home.WIN_NAME);
    }

    public static Tag getTag() {
        return (Tag) map.get(Tag.WIN_NAME);
    }
    
}
