package common.tool;

public interface KeyBuilder {
    String HOME = "home:";
    String TAG = "tag" ;
    String FILEMENU = "fileMune:";
    String TAGSELLECTOR = "tagSellector";

    static String home(String key){
        return HOME.concat(key);
    }

    static String tag(String key){
        return TAG.concat(key);
    }
    static String fileMune(String key){
        return FILEMENU.concat(key);
    }
    static String tagSellector(String key){
        return TAGSELLECTOR.concat(key);
    }
}
