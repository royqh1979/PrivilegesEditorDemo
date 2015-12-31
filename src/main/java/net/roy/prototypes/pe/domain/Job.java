package net.roy.prototypes.pe.domain;

/**
 * Created by Roy on 2015/12/21.
 */
public class Job {
    private long id;
    private String name;
    private long department_id;

    public Job() {
    }

    public Job(long id, String name, long department_id) {
        this.id = id;
        this.name = name;
        this.department_id = department_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDepartmentId() {
        return department_id;
    }

    public void setDepartmentId(long department_id) {
        this.department_id = department_id;
    }

    @Override
    public String toString() {
        return name;
    }
}
