package net.roy.prototypes.pe.persist;

import net.roy.prototypes.pe.domain.Department;
import net.roy.prototypes.pe.domain.Job;
import net.roy.prototypes.pe.domain.Privilege;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Roy on 2015/12/23.
 */
public class PrivilegePersister {
    private JdbcTemplate template;
    private PlatformTransactionManager transactionManager;

    public static final RowMapper<Privilege> PRIVILEGE_ROW_MAPPER = (rs, rowNum) -> new Privilege(rs.getLong("id"),
            rs.getString("name"),
            rs.getString("process_name"),
            rs.getString("task_name"),
            rs.getString("note"));

    public void setTemplate(JdbcTemplate template) {
        this.template = template;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
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

    public void update(Privilege privilege) {
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
                PRIVILEGE_ROW_MAPPER);
    }

    public List<Privilege> listPrivilegesNotBelongToDepartment(Department department) {
        return template.query("select * from  privilege where id not in (select privilege_id from department_privilege where department_id=?) order by id",
                ps->{
                    ps.setLong(1,department.getId());
                },
                PRIVILEGE_ROW_MAPPER);
    }

    public List<Privilege> listPrivileges() {
        return template.query("select * from privilege order by id", PRIVILEGE_ROW_MAPPER);
    }

    public void remove(Privilege privilege) {
        TransactionDefinition definition=new DefaultTransactionDefinition();
        TransactionStatus status=transactionManager.getTransaction(definition);
        try {
            template.update("delete from department_privilege where privilege_id=?",ps->{
                ps.setLong(1,privilege.getId());
            });
            template.update("delete from privilege where id=?", ps -> {
                ps.setLong(1, privilege.getId());
            });
            transactionManager.commit(status);
        } catch (RuntimeException e) {
            transactionManager.rollback(status);
        }
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

    public List<Department> listDepartmentsHavingPrivilege(Privilege privilege) {
        return template.query("select A.* from Department A join department_privilege B on A.id = B.department_id where B.privilege_id=? ",ps->{
            ps.setLong(1, privilege.getId());
        },DepartmentPersister.DEPARTMENT_ROW_MAPPER);
    }

    public void updateDepartmentsHavingPrivilege(Privilege privilege, List<Department> departmentList) {
        TransactionDefinition definition=new DefaultTransactionDefinition();
        TransactionStatus status=transactionManager.getTransaction(definition);
        try {
            template.update("delete from department_privilege where privilege_id=?",ps->{
                ps.setLong(1,privilege.getId());
            });
            saveDepartmentsHavingPrivilege(privilege,departmentList);
            transactionManager.commit(status);
        } catch (RuntimeException e) {
            transactionManager.rollback(status);
            throw e;
        }
    }

    public void saveDepartmentsHavingPrivilege(Privilege privilege, List<Department> departmentList) {
        template.batchUpdate("insert into department_privilege(privilege_id,department_id) values (?,?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1,privilege.getId());
                        ps.setLong(2,departmentList.get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return departmentList.size();
                    }
                });
    }

    public List<Privilege> listJobPrivileges(Department department, Job job) {
        return template.query("select A.* from privilege A join job_privilege B on A.id = B.privilege_id where B.job_id=?",ps->{
            ps.setLong(1,job.getId());
        },PRIVILEGE_ROW_MAPPER);
    }

    public List<Privilege> listNonJobPrivileges(Department department, Job job) {
        return template.query("select * from privilege where id in (select privilege_id from department_privilege where department_id=?" +
                        " except select privilege_id from job_privilege where job_id=?)",
                ps->{
                    ps.setLong(1, department.getId());
                    ps.setLong(2,job.getId());
                },PRIVILEGE_ROW_MAPPER);
    }

    public void updateJobPrivileges(Department department, Job job, List<Privilege> assignList) {
        TransactionDefinition definition=new DefaultTransactionDefinition();
        TransactionStatus status=transactionManager.getTransaction(definition);
        try {
            template.update("delete from job_privilege where job_id=?",ps->{
                ps.setLong(1,job.getId());
            });
            List<String> privilegesIds=assignList.stream().map(privilege->Long.toString(privilege.getId())).distinct().collect(Collectors.toList());

            String query="select * from privilege where id in ("
                    +String.join(",",privilegesIds)
                    +") and id in (select privilege_id from department_privilege where department_id=?) ";
            List<Privilege> privileges=template.query(query,ps->{
                ps.setLong(1,department.getId());
            },PRIVILEGE_ROW_MAPPER);

            template.batchUpdate("insert into job_privilege(job_id, privilege_id) VALUES (?,?)", new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Privilege privilege=privileges.get(i);
                    ps.setLong(1,job.getId());
                    ps.setLong(2,privilege.getId());
                }

                @Override
                public int getBatchSize() {
                    return privileges.size();
                }
            });
            transactionManager.commit(status);
        } catch(RuntimeException e) {
            transactionManager.rollback(status);
            throw e;
        }
    }
}
