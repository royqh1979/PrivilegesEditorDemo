package net.roy.prototypes.pe.persist;

import net.roy.prototypes.pe.domain.Department;
import net.roy.prototypes.pe.domain.Job;
import net.roy.prototypes.pe.domain.Privilege;
import net.roy.prototypes.pe.domain.User;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Roy on 2015/12/26.
 */
public class UserPersister {
    private JdbcTemplate template;
    private PlatformTransactionManager transactionManager;
    public static final RowMapper<User> USER_ROW_MAPPER =(rs, rowNum) -> {
        return new User(rs.getLong("id"),
                rs.getString("account"),
                rs.getString("name"),
                rs.getString("id_card_no"),
                rs.getString("password"),
                rs.getString("salt"),
                rs.getBoolean("enabled")
        );
    };
    public int countUserWithPrivilege(Privilege privilege) {
        //TODO: 从数据库中读取
        return 0;
    }

    public void setTemplate(JdbcTemplate template) {
        this.template = template;
    }

    public List<User> listUsers() {
        return template.query("select * from user order by id", USER_ROW_MAPPER);
    }

    public void create(User user, List<Department> departmentList){
        TransactionDefinition definition=new DefaultTransactionDefinition();
        TransactionStatus status= transactionManager.getTransaction(definition);
        try {
            createUser(user);
            setUserDepartments(user, departmentList);
            transactionManager.commit(status);
        } catch (RuntimeException e) {
            transactionManager.rollback(status);
            throw e;
        }
    }
    private void createUser(User user) {
        KeyHolder keyHolder=new GeneratedKeyHolder();
        template.update(con -> {
            PreparedStatement statement=con.prepareStatement("insert into" +
                    " user(account,name,password,salt,id_card_no,enabled) values (?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getAccount());
            statement.setString(2,user.getName());
            statement.setString(3,user.getPassword());
            statement.setString(4,user.getSalt());
            statement.setString(5,user.getIdCardNo());
            statement.setBoolean(6,user.isEnabled());
            return statement;
        },keyHolder);
        user.setId(keyHolder.getKey().longValue());

    }

    private void setUserDepartments(User user, List<Department> departmentList) {
        template.batchUpdate("insert into department_user(department_id,user_id) values (?, ?)"
                , new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Department department=departmentList.get(i);
                ps.setLong(1,department.getId());
                ps.setLong(2,user.getId());
            }

            @Override
            public int getBatchSize() {
                return departmentList.size();
            }
        });
    }

    public void remove(User user) {
        TransactionDefinition definition=new DefaultTransactionDefinition();
        TransactionStatus status= transactionManager.getTransaction(definition);
        try {
            removeUserDepartments(user);
            removeUserJobs(user);
            removeUserPrivileges(user);
            removeUser(user);
            transactionManager.commit(status);
        } catch (RuntimeException e) {
            transactionManager.rollback(status);
        }
    }

    private void removeUser(User user) {
        template.update("delete from user where id=?",ps->{
            ps.setLong(1, user.getId());
        });
    }

    private void removeUserPrivileges(User user) {
        //TODO: 待实现,数据库结构未建
    }

    private void removeUserJobs(User user) {
        template.update("delete from user_job where user_id=?",ps->{
            ps.setLong(1, user.getId());
        });
    }

    private void removeUserDepartments(User user) {
        template.update("delete from department_user where user_id=?",ps->{
            ps.setLong(1, user.getId());
        });
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public int countUserInDepartment(Department dept) {
        return template.query("select count(*) from department_user where department_id=?",
                ps->{
                    ps.setLong(1,dept.getId());
                },rse->{
                    return rse.getInt(1);
                });
    }

    public List<Department> listUserDepartments(User user) {
        return template.query("select A.* from department A join department_user B on A.id = B.department_id where B.user_id=?",ps->{
            ps.setLong(1,user.getId());
        },DepartmentPersister.DEPARTMENT_ROW_MAPPER);
    }

    public void addUserDepartment(User user, Department department) {
        template.update("insert into department_user(department_id, user_id) values (?,?)",ps->{
            ps.setLong(1,department.getId());
            ps.setLong(2,user.getId());
        });
    }

    public void removeUserDepartment(User user, Department department) {
        template.update("delete from department_user where department_id=? and user_id=?",
                ps->{
                    ps.setLong(1,department.getId());
                    ps.setLong(2,user.getId());
                });
    }

    public void update(User user) {
        template.update("update user set account=?,name=?,id_card_no=?,enabled=? where id=?",
                ps->{
                    ps.setString(1, user.getAccount());
                    ps.setString(2, user.getName());
                    ps.setString(3, user.getIdCardNo());
                    ps.setBoolean(4, user.isEnabled());
                    ps.setLong(5,user.getId());
                });
    }

    public void updateDepartments(User user, List<Department> departmentList) {
        TransactionDefinition definition=new DefaultTransactionDefinition();
        TransactionStatus status= transactionManager.getTransaction(definition);

        try {
            template.update("delete from department_user where user_id=?",ps->{
                ps.setLong(1,user.getId());
            });
            setUserDepartments(user, departmentList);
            transactionManager.commit(status);
        } catch (RuntimeException e) {
            transactionManager.rollback(status);
            throw e;
        }
    }

    public List<User> listUsersInDepartment(Department department) {
        return template.query("select A.* from user A join department_user B on A.id = B.user_id where B.department_id=?",
                ps->{
                    ps.setLong(1,department.getId());
                },USER_ROW_MAPPER);
    }

    public List<User> listJobUsers(Department department, Job job) {
        return template.query("select A.* from User A join user_job B on A.id=B.user_id where B.job_id=?",ps->{
            ps.setLong(1,job.getId());
        },USER_ROW_MAPPER);
    }

    public List<User> listNonJobUsers(Department department, Job job) {
        return template.query("select * from user  where id in (select user_id from department_user where department_id=? " +
                "except select user_id from user_job where job_id=?)"
                ,ps->{
            ps.setLong(1,department.getId());
            ps.setLong(2,job.getId());
        },USER_ROW_MAPPER);    }

    public void updateJobUsers(Department department, Job job, List<User> assignList) {
        TransactionDefinition definition=new DefaultTransactionDefinition();
        TransactionStatus status=transactionManager.getTransaction(definition);
        try {
            template.update("delete from user_job where job_id=?",ps->{
                ps.setLong(1,job.getId());
            });
            List<String> userIds=assignList.stream().map(privilege->Long.toString(privilege.getId())).distinct().collect(Collectors.toList());

            String query="select * from user where id in ("
                    +String.join(",",userIds)
                    +") and id in (select user_id from department_user where department_id=?) ";
            List<User> users=template.query(query,ps->{
                ps.setLong(1,department.getId());
            },USER_ROW_MAPPER);

            template.batchUpdate("insert into user_job(job_id, user_id) VALUES (?,?)", new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    User user=users.get(i);
                    ps.setLong(1,job.getId());
                    ps.setLong(2, user.getId());
                }

                @Override
                public int getBatchSize() {
                    return users.size();
                }
            });
            transactionManager.commit(status);
        } catch(RuntimeException e) {
            transactionManager.rollback(status);
            throw e;
        }
    }
}
