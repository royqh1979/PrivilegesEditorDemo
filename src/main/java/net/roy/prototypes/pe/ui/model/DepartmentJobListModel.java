package net.roy.prototypes.pe.ui.model;

import net.roy.prototypes.pe.domain.Department;
import net.roy.prototypes.pe.domain.DepartmentManager;
import net.roy.prototypes.pe.domain.Job;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roy on 2015/12/21.
 */
public class DepartmentJobListModel extends AbstractListModel<Job> {
    private DepartmentManager departmentManager;
    private Department department;
    private List<Job> jobs=new ArrayList<>();

    public DepartmentJobListModel(Department department, DepartmentManager departmentManager) {
        this.departmentManager =departmentManager;
        this.department=department;
        loadJobs(departmentManager,department);
    }

    private void loadJobs(DepartmentManager departmentManager, Department department) {
        jobs=departmentManager.listDepartmentJobs(department);
        this.fireContentsChanged(this, 0, getSize() - 1);
    }

    @Override
    public int getSize() {
        return jobs.size();
    }

    @Override
    public Job getElementAt(int index) {
        return jobs.get(index);
    }

    public void setDepartment(Department department) {
        this.department = department;
        loadJobs(departmentManager,department);

    }

    public Department getDepartment() {
        return department;
    }

    public void addJob() {
        departmentManager.addJob(department, "新岗位");
        loadJobs(departmentManager,department);
    }

    public void renameJobAt(int index,String newName) {
        Job job=jobs.get(index);
        if (job==null)
            return ;
        if (job.getName().equals(newName))
            return ;
        departmentManager.renameJob(job, newName);
        this.fireContentsChanged(this, index, index);
    }

    public void removeJobAt(int index) {
        Job job=jobs.get(index);
        if (job==null)
            return ;
        departmentManager.removeJob(job);
        loadJobs(departmentManager,department);
    }
}
