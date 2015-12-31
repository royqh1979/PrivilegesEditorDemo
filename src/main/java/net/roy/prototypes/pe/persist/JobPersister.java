package net.roy.prototypes.pe.persist;

import net.roy.prototypes.pe.domain.Department;
import net.roy.prototypes.pe.domain.Job;
import net.roy.prototypes.pe.domain.Privilege;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Roy on 2015/12/23.
 */
public class JobPersister {
    JdbcTemplate template;

    public void create(Job newJob){
        KeyHolder keyHolder=new GeneratedKeyHolder();
        template.update(
            conn->{
                PreparedStatement preparedStatement=
                        conn.prepareStatement("insert into job(name,department_id) values(?,?)", Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, newJob.getName());
                preparedStatement.setLong(2, newJob.getDepartmentId());
                return preparedStatement;
            },keyHolder);
        newJob.setId(keyHolder.getKey().longValue());
    }


    public void update(Job job){
        template.update(
                "update job set name=?,department_id=? where id=?",
                ps -> {
                    ps.setString(1, job.getName());
                    ps.setLong(2, job.getDepartmentId());
                    ps.setLong(3, job.getId());
                }
        );
    }


    public List<Job> listJobsByDepartment(Department department) {
        return template.query("select * from job where department_id=?",
                ps->{
                    ps.setLong(1,department.getId());
                },
                (rs, rowNum) -> {
                    Job raw=new Job(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getLong("department_id")
                    );
                    return raw;
                });
    }

    public void setTemplate(JdbcTemplate template) {
        this.template = template;
    }

    public void remove(Job job) {
        template.update("delete from job where id=?",ps->{
           ps.setLong(1,job.getId());
        });
    }

    public int countJobWithPrivilege(Privilege privilege) {
        //TODO: 从数据库中读取
        return 0;
    }
}
