package net.roy.prototypes.pe.domain;

import net.roy.prototypes.pe.persist.DepartmentPersister;
import net.roy.prototypes.pe.persist.JobPersister;
import net.roy.prototypes.pe.persist.PrivilegePersister;
import net.roy.prototypes.pe.persist.UserPersister;

import java.util.Collections;
import java.util.List;

/**
 * Created by Roy on 2015/12/21.
 */
public class DepartmentManager {
    private DepartmentPersister departmentPersister;
    private JobPersister jobPersister;
    private UserPersister userPersister;
    private PrivilegePersister privilegePersister;
    private String name;


    public DepartmentManager(String name) {
        this.name=name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDepartmentPersister(DepartmentPersister departmentPersister) {
        this.departmentPersister = departmentPersister;
    }

    public Department getRoot() {
         return departmentPersister.loadRoot();
    }


    public Department getDepartmentChild(Department department, int index) {
        List<Department> childs= departmentPersister.listChilds(department);
        return childs.get(index);
    }

    public int getDepartmentChildCount(Department department) {
        return departmentPersister.countChilds(department);
    }

    public void renameDepartment(Department dept, String name) {
        dept.setName(name);
        departmentPersister.update(dept);
    }

    public int getIndexOfDepartmentChild(Department parent, Department child) {
        List<Department> childs= departmentPersister.listChilds(parent);
        return childs.indexOf(child);
    }

    public Department addDepartment(Department parent, String deptName) {
        Department department=new Department(0,deptName,parent.getId(),false);
        departmentPersister.create(department);
        return department;
    }

    public Department getDepartmentParent(Department dept) {
        return departmentPersister.getDepartmentById(dept.getParentId());
    }

    public void removeDepartment(Department dept) {
        if (dept.isRoot())
            return ;
        if (getDepartmentChildCount(dept)>0)
            return ;
        if (userPersister.countUserInDepartment(dept)>0)
            return ;
        if (privilegePersister.countPrivilegesOfDepartment(dept)>0)
            return;
        departmentPersister.remove(dept);
    }

    public List<Job> listDepartmentJobs(Department department) {
        if (department==null) {
            return Collections.emptyList();
        }
        return jobPersister.listJobsByDepartment(department);
    }

    public void setJobPersister(JobPersister jobPersister) {
        this.jobPersister = jobPersister;
    }

    public Job addJob(Department department, String jobName) {
        Job job=new Job(0,jobName,department.getId());
        jobPersister.create(job);
        return job;
    }

    public void renameJob(Job job, String newName) {
        job.setName(newName);
        jobPersister.update(job);
    }

    public void removeJob(Job job) {
        jobPersister.remove(job);
    }

    public void setUserPersister(UserPersister userPersister) {
        this.userPersister = userPersister;
    }

    public void setPrivilegePersister(PrivilegePersister privilegePersister) {
        this.privilegePersister = privilegePersister;
    }

    public List<Privilege> listDepartmentPrivileges(Department department) {
        return privilegePersister.listPrivilegesOfDepartment(department);
    }

    public List<Privilege> listPrivilegesNotBelongTo(Department department) {
        return privilegePersister.listPrivilegesNotBelongToDepartment(department);
    }

    public void assignPrivilegeTo(Department department, List<Privilege> privilegeList) {
        privilegePersister.assginPrivilegeToDepartment(department,privilegeList);
    }

    public List<Department> listChilds(Department department) {
        return departmentPersister.listChilds(department);
    }

    public List<User> listUsersInDepartment(Department department) {
        return userPersister.listUsersInDepartment(department);
    }
}
