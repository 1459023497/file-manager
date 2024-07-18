package common.tool;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import entity.IFile;
import entity.ISpace;
import entity.ITag;
import org.apache.commons.collections4.MapUtils;

public class BeanUtils {

    public static IFile setFile(Map<String,Object> map){
        String id = MapUtils.getString(map, "id");
        String name = MapUtils.getString(map, "name");
        String path = MapUtils.getString(map, "path");
        String size = MapUtils.getString(map, "size");
        String dir = MapUtils.getString(map, "belong");
        return new IFile(id, name, path, size, dir);
    }

    public static ITag setTag(Map<String,Object> map){
        String id = MapUtils.getString(map, "id");
        String name = MapUtils.getString(map, "name");
        String group = MapUtils.getString(map, "group");
        int isMain = 0;
        if(map.containsKey("is_main")){
            isMain = MapUtils.getInteger(map, "is_main");
        }
        return new ITag(id, name, group, isMain);
    }

    public static ITag setTagWithKey(Map<String,Object> map){
        String id = MapUtils.getString(map, "id");
        String name = MapUtils.getString(map, "name");
        String group = MapUtils.getString(map, "group");
        String keys = MapUtils.getString(map, "key");
        ITag tag = new ITag(id, name, group);
        List<String> list = new ArrayList<>();
        if(keys != null){
            list = Arrays.asList(keys.split(","));
        }
        tag.setKeys(list);
        return tag;
    }

    private static boolean containsNameColumn(ResultSet rs, String column){
        try{
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        //judge the column existed
        for (int i = 1; i <= columnCount; i++) {
            String columnName = rsmd.getColumnName(i);
            if (column.equals(columnName)) {
                return true;
            }
        }
        }catch(SQLException e){
            throw new RuntimeException("查询结果集元数据出错：" + e);
        }
        return false;
    }

    public static ISpace setSpace(Map<String,Object> map){
        String id = MapUtils.getString(map, "id");
        String name = MapUtils.getString(map, "name");
        return new ISpace(id, name);
    }
}
