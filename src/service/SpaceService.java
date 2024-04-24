package service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.tool.BeanUtils;
import common.tool.IdGenerator;
import jdbc.JDBCConnector;

import entity.ISpace;

public class SpaceService {
    private JDBCConnector conn;
    private IdGenerator id;

    public SpaceService() {
        conn = new JDBCConnector();
        id = new IdGenerator();
    }

    public List<ISpace> getAllSpaces() {
        String sql = "SELECT * FROM space;";
        ResultSet rs = conn.select(sql);
        List<ISpace> spaces = new ArrayList<>();
        try {
            while (rs.next()) {
                ISpace space = BeanUtils.setSpace(rs);
                spaces.add(space);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
        return spaces;
    }

    public void add(String name) {
        String sql = String.format("INSERT into space(id,name) values('%s','%s');",id.next(), name);
        conn.update(sql);
    }

    public void deleteById(String id) {
        String sql = String.format("DELETE FROM file WHERE space_id = '%s';DELETE FROM space WHERE id = '%s';", id, id);
        conn.update(sql);
    }

    public ISpace getSpaceByName(String name){
        String sql = String.format("SELECT * FROM space WHERE name = '%s';", name);
        ResultSet rs = conn.select(sql);
        List<ISpace> spaces = new ArrayList<>();
        try {
            while (rs.next()) {
                ISpace space = BeanUtils.setSpace(rs);
                spaces.add(space);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
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
