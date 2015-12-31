package net.roy.prototypes.pe.persist;

import net.roy.prototypes.pe.domain.Department;
import net.roy.prototypes.pe.domain.Privilege;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Roy on 2015/12/23.
 */
public class PrivilegePersister {
    private JdbcTemplate template;
    public static final RowMapper<Privilege> privilegeRowMapper =new RowMapper<Privilege>() {
        @Override
        public Privilege mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Privilege(rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("process_name"),
                    rs.getString("task_name"),
                    rs.getString("note"));
        }
    };

    public void setTemplate(JdbcTemplate template) {
        this.template = template;
    }

    public void create(Privilege privilege) {
        KeyHolder keyHolder=new GeneratedKeyHolder();
        template.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps=con.prepareStatement("insert into privilege(name,process_name" +
                        ",task_name,note) values (?,?,?,?)");
                ps.setString(1,privilege.getName());
                ps.setString(2,privilege.getProcessName());
                ps.setString(3,privilege.getTaskName());
                ps.setString(4,privilege.getNote());
                return ps;
            }
        },keyHolder);
        privilege.setId(keyHolder.getKey().longValue());
    }

    void update(Privilege privilege) {
        template.update("update privilege set name=?,process_name=?,task_name=?,note=? where id=?",
                ps -> {
                    ps.setString(1, privilege.getName());
                    ps.setString(2, privilege.getProcessName());
                    ps.setString(3, privilege.getTaskName());
                    ps.setString(4, privilege.getNote());
                    ps.setLong(5, privilege.getId());
                });
    }

    public void addPrivilegeToDepartment(Department department, Privilege privilege) {
        template.update("insert into department_privilege(department_id,privilege_id) values (?,?)",
                ps -> {
                    ps.setLong(1, department.getId());
                    ps.setLong(2, privilege.getId());
                });
    }

    public void removePrivilegeFromDepartment(Department department, Privilege privilege) {
        template.update("delete from department_privilege where department_id=?,privilege_id=?",
                ps->{
                    ps.setLong(1, department.getId());
                    ps.setLong(2,privilege.getId());
                });
    }

    public List<Privilege> listPrivilegesOfDepartment(Department department) {
        return template.query("select A.* from  privilege A join department_privilege B on A.id=B.privilege_id where B.department_id=? order by A.id",
                ps->{
                    ps.setLong(1,department.getId());
                },
                privilegeRowMapper);
    }

    public List<Privilege> listPrivilegesNotBelongToDepartment(Department department) {
        return template.query("select * from  privilege where id not in (select privilege_id from department_privilege where department_id=?) order by id",
                ps->{
                    ps.setLong(1,department.getId());
                },
                privilegeRowMapper);
    }

    public List<Privilege> listPrivileges() {
        return template.query("select * from privilege order by id",privilegeRowMapper);
    }

    public void remove(Privilege privilege) {
        template.update("delete from privilege where id=?", ps -> {
            ps.setLong(1, privilege.getId());
        });
    }

    public void assginPrivilegeToDepartment(Department department, List<Privilege> privilegeList) {
        template.batchUpdate("insert into department_privilege(department_id,privilege_id) values (?,?)",
                privilegeList, privilegeList.size(), (ps, privilege) -> {
                    ps.setLong(1,department.getId());
                    ps.setLong(2,privilege.getId());
                });
    }

    public int countPrivilegesOfDepartment(Department dept) {
        return template.query("select count(*) from department_privilege where department_id=?",
                ps->{
                    ps.setLong(1,dept.getId());
                },rss->{
                    return rss.getInt(1);
                });
    }
}
