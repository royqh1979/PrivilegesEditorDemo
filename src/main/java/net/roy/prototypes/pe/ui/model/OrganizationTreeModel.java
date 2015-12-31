package net.roy.prototypes.pe.ui.model;

import net.roy.prototypes.pe.domain.Department;
import net.roy.prototypes.pe.domain.DepartmentManager;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roy on 2015/12/21.
 */
public class OrganizationTreeModel implements TreeModel {
    private DepartmentManager departmentManager;
    private List<TreeModelListener> listenerList=new ArrayList<>();

    public OrganizationTreeModel(DepartmentManager departmentManager) {
        this.departmentManager = departmentManager;
    }

    @Override
    public Object getRoot() {
        return departmentManager.getRoot();
    }

    @Override
    public Object getChild(Object parent, int index) {
        Department department=(Department)parent;
        return departmentManager.getDepartmentChild(department, index);
    }

    @Override
    public int getChildCount(Object parent) {
        Department department=(Department)parent;
        return departmentManager.getDepartmentChildCount(department);
    }

    @Override
    public boolean isLeaf(Object node) {
        Department department=(Department)node;
        return departmentManager.getDepartmentChildCount(department)==0;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        Department dept=(Department)path.getLastPathComponent();
        departmentManager.renameDepartment(dept,newValue.toString());

        TreeModelEvent e=new TreeModelEvent(dept,path);

        for (TreeModelListener listener:listenerList){
            listener.treeNodesChanged(e);
        }
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return departmentManager.getIndexOfDepartmentChild((Department)parent,(Department)child);
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
          listenerList.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
          listenerList.remove(l);
    }


    public void addDepartmentNode(TreePath path, String name) {
        int index;
        String deptName=name;
        Department parent=null;
        if (path==null) {
            parent = departmentManager.getRoot();
        } else {
            parent = (Department) path.getLastPathComponent();
        }
        Department newDept=departmentManager.addDepartment(parent,deptName);
        index= departmentManager.getIndexOfDepartmentChild(parent, newDept);


        TreeModelEvent e=new TreeModelEvent(this,path,new int[]{index},new Object[]{newDept});

        for (TreeModelListener listener:listenerList){
            listener.treeNodesInserted(e);
        }
    }

    public void removeDepartmentNode(TreePath path) {
        if (path==null)
            return ;
        if (path.getPathCount()<=1)
            return;
        Department  dept = (Department) path.getLastPathComponent();
        Department parent=departmentManager.getDepartmentParent(dept);
        int index = departmentManager.getIndexOfDepartmentChild(parent, dept);
        departmentManager.removeDepartment(dept);

        TreeModelEvent e=new TreeModelEvent(this,path.getParentPath(),new int[]{index},new Object[]{dept});

        for (TreeModelListener listener:listenerList){
            listener.treeNodesRemoved(e);
        }
    }
}
