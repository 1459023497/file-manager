package service;

import common.tool.BeanUtils;
import common.tool.IdGenerator;
import entity.ISpace;
import jdbc.JDBCConnector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpaceService {
    private JDBCConnector conn;
    private IdGenerator idGenerator;

    public SpaceService() {
        conn = new JDBCConnector();
        idGenerator = new IdGenerator();
    }

    public List<ISpace> getAllSpaces() {
        String sql = "SELECT * FROM space;";
        List<Map<String, Object>> rs = conn.select(sql);
        List<ISpace> spaces = new ArrayList<>();
        rs.forEach(e->{
            ISpace space = BeanUtils.setSpace(e);
            spaces.add(space);
        });
        return spaces;
    }

    public ISpace add(String name) {
        String id = idGenerator.next();
        String sql = String.format("INSERT into space(id,name) values('%s','%s');", id, name);
        conn.update(sql);
        return getSpaceById(id);
    }

    public void deleteById(String id) {
        String sql = String.format("DELETE FROM file WHERE space_id = '%s';DELETE FROM space WHERE id = '%s';", id, id);
        conn.update(sql);
    }

    public ISpace getSpaceByName(String name){
        String sql = String.format("SELECT * FROM space WHERE name = '%s';", name);
        List<Map<String, Object>> rs = conn.select(sql);
        List<ISpace> spaces = new ArrayList<>();
        rs.forEach(e->{
            ISpace space = BeanUtils.setSpace(e);
            spaces.add(space);
        });
        return spaces.isEmpty()? null : spaces.get(0);
    }

    public ISpace getSpaceById(String id){
        String sql = String.format("SELECT * FROM space WHERE name = '%s';", id);
        List<Map<String, Object>> rs = conn.select(sql);
        List<ISpace> spaces = new ArrayList<>();
        rs.forEach(e->{
            ISpace space = BeanUtils.setSpace(e);
            spaces.add(space);
        });
        return spaces.isEmpty()? null : spaces.get(0);
    }

    public void deleteByName(String name) {
        ISpace space = getSpaceByName(name);
        if (space == null) {
            return;
        }
        String sql = String.format("DELETE FROM file WHERE space_id = '%s';DELETE FROM space WHERE id = '%s';", space.getId(), space.getId());
        conn.update(sql);
    }

}
