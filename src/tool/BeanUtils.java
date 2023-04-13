package tool;

import java.sql.ResultSet;
import java.sql.SQLException;

import entity.IFile;
import entity.ITag;

public class BeanUtils {

    public static IFile getFile(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String name = rs.getString("name");
        String path = rs.getString("path");
        String size = rs.getString("size");
        String dir = rs.getString("belong");
        return new IFile(id, name, path, size, dir);
    }

    public static ITag getTag(ResultSet rs) throws SQLException{
        String id = rs.getString("id");
        String name = rs.getString("name");
        String group = rs.getString("group");
        return new ITag(id, name, group);
    }
    
}
