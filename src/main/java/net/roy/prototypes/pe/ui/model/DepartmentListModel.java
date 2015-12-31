package net.roy.prototypes.pe.ui.model;

import net.roy.prototypes.pe.domain.Department;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于显示用户目前所属的所有部门的列表Model
 * Created by Roy on 2015/12/30.
 */
public class DepartmentListModel extends AbstractListModel<String> {
    List<Department> departmentList=new ArrayList<>();
    List<String> departmentFullPathList=new ArrayList<>();
    @Override
    public int getSize() {
        return departmentFullPathList.size();
    }

    @Override
    public String getElementAt(int index) {
        return departmentFullPathList.get(index);
    }

    /**
     * 清空部门列表
     */
    public void clear(){
        int oldSize=departmentFullPathList.size();
        departmentFullPathList.clear();
        departmentList.clear();
        fireIntervalRemoved(this,0,Math.max(oldSize-1,0));
    }

    /**
     * 向列表中增加新的部门
     * @param department 要添加的部门
     * @param fullPath 要添加的部门的完整路径
     */
    public void add(Department department,String fullPath) {
        departmentList.add(department);
        departmentFullPathList.add(fullPath);
        fireIntervalAdded(this,departmentList.size()-1,departmentList.size()-1);
    }

    /**
     * 返回部门列表
     * @return 部门列表
     */
    public List<Department> getDepartmentList() {
        return departmentList;
    }


    public void removeAt(int i) {
        departmentList.remove(i);
        departmentFullPathList.remove(i);
        fireIntervalRemoved(this,i,i);
    }

    public boolean isDepartmentInList(Department department) {
        return departmentList.stream().anyMatch(dept->department.getId()==dept.getId());
    }
}
