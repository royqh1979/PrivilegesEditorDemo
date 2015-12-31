package net.roy.prototypes.pe.persist;

import net.roy.prototypes.pe.domain.Department;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.util.List;

/**
 * Created by Roy on 2015/12/23.
 */
public class DepartmentPersister {
    private JdbcTemplate template;
    public static final RowMapper<Department> DEPARTMENT_ROW_MAPPER =new RowMapper<Department>() {
        @Override
        public Department mapRow(ResultSet rs, int rowNum) throws SQLException {
            Department department=new Department(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getLong("parent_id"),
                    rs.getBoolean("is_root")
            );
            return department;
        }
    };


    public void create(Department newDepartment) {
        KeyHolder keyHolder=new GeneratedKeyHolder();
        template.update(
                conn -> {
                    PreparedStatement statement=conn.prepareStatement(
                            "insert into department(name,parent_id,is_root) values (?,?,?)",
                            Statement.RETURN_GENERATED_KEYS);
                    statement.setString(1, newDepartment.getName());
                    if (newDepartment.getParentId()==0) {
                        statement.setNull(2, Types.INTEGER);
                    } else {
                        statement.setLong(2, newDepartment.getParentId());
                    }
                    statement.setBoolean(3,newDepartment.isRoot());
                    return statement;
                },keyHolder);
        newDepartment.setId(keyHolder.getKey().longValue());
    }

    public void update(Department department) {
        template.update("update department set name=?,parent_id=?,is_root=? where id=?",
                ps -> {
                    ps.setString(1, department.getName());
                    if (department.getParentId() == 0) {
                        ps.setNull(2, Types.INTEGER);
                    } else {
                        ps.setLong(2, department.getParentId());
                    }
                    ps.setBoolean(3, department.isRoot());
                    ps.setLong(4, department.getId());
                });
    }


    public List<Department> listDepartments() {
        return template.query("select * from department",
                DEPARTMENT_ROW_MAPPER);
    }

    public Department loadRoot() {
        return template.queryForObject("select * from department where is_root=1 limit 1",
                DEPARTMENT_ROW_MAPPER);
    }

    public List<Department> listChilds(Department department) {
        return template.query("select * from department where parent_id=? order by id",
                ps -> {
                    ps.setLong(1, department.getId());
                },
                DEPARTMENT_ROW_MAPPER);
    }

    public int countChilds(Department department) {
        return template.query(
                "select count(*) from department where parent_id=?",
                ps -> {
                    ps.setLong(1, department.getId());
                },
                rs -> {
                    return rs.getInt(1);
                }
        );
    }

    public void setTemplate(JdbcTemplate template) {
        this.template = template;
    }

    public Department getDepartmentById(long id) {
        return template.query("select * from department where id=?",
                ps -> {
                    ps.setLong(1, id);
                },
                rs -> {
                    Department dept=new Department(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getLong("parent_id"),
                            rs.getBoolean("is_root")
                    );
                    return dept;
                });
    }

    public void remove(Department dept) {
        template.update("delete from department where id=?",ps->{
            ps.setLong(1,dept.getId());
        });
    }
}
