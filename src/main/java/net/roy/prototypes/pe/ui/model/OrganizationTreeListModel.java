package net.roy.prototypes.pe.ui.model;

import net.roy.prototypes.pe.domain.Department;
import net.roy.prototypes.pe.domain.DepartmentManager;

import javax.swing.*;
import java.util.*;

/**
 * Created by Roy on 2015/12/27.
 */
public class OrganizationTreeListModel extends AbstractListModel<Department> implements ComboBoxModel<Department>{
    private DepartmentManager departmentManager;
    private List<Department> departmentList=new ArrayList<>();
    private Map<Long,Integer> departmentDepthMap =new HashMap<>();
    private Map<Long,String> departmentFullPathMap=new HashMap<>();
    private Department selectedItem;

    public OrganizationTreeListModel(DepartmentManager departmentManager) {
        this.departmentManager = departmentManager;
        loadList(departmentManager);

    }

    /**
     * 载入组织结构树状列表
     * @param departmentManager
     */
    private void loadList(DepartmentManager departmentManager) {
        int oldLen = departmentList.size();
        departmentList.clear();
        departmentDepthMap.clear();
        departmentFullPathMap.clear();
        Department root = departmentManager.getRoot();
        traverseTree(root, 0,"");
        fireContentsChanged(this, 0, Math.max(oldLen, departmentList.size()));
    }

    /**
     * 递归实现先序对组织结构树的遍历,以生成树装列表
     * @param department
     * @param level
     * @param parentPath
     */
    private void traverseTree(Department department, int level,String parentPath) {
        String path;
        if (parentPath.equals("") ) {
            path = department.getName();
        } else {
            path=parentPath+"-"+department.getName();
        }
        departmentList.add(department);
        departmentDepthMap.put(department.getId(), level);
        departmentFullPathMap.put(department.getId(),path);

        List<Department> childs=departmentManager.listChilds(department);
        childs.stream().forEach(dept -> {
            traverseTree(dept,level+1,path);
        });
    }

    @Override
    public void setSelectedItem(Object anItem) {
        if ((selectedItem != null && !selectedItem.equals( anItem )) ||
                selectedItem == null && anItem != null) {
            selectedItem = (Department)anItem;
            fireContentsChanged(this, -1, -1);
        }
    }

    @Override
    public Object getSelectedItem() {
        return selectedItem;
    }

    public String getDepartmentFullPath(Department department) {
        return departmentFullPathMap.get(department.getId());
    }

    @Override
    public int getSize() {
        return departmentList.size();
    }

    @Override
    public Department getElementAt(int index) {
        return departmentList.get(index);
    }

    public int getDepartmentLevel(Department dept) {
        return departmentDepthMap.get(dept.getId());
    }
}
