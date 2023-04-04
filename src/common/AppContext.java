package common;

import java.util.HashMap;
import java.util.Map;

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
    
}
