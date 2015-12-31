package net.roy.prototypes.pe.ui.model;

import net.roy.prototypes.pe.domain.Privilege;
import net.roy.prototypes.pe.domain.PrivilegeManager;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Created by Roy on 2015/12/25.
 */
public class PrivilegesTableModel extends AbstractTableModel {

    private PrivilegeManager privilegeManager;
    private List<Privilege> privilegeList;

    public PrivilegesTableModel(PrivilegeManager privilegeManager) {
        this.privilegeManager = privilegeManager;
        loadPrivilegeList(privilegeManager);
    }

    private void loadPrivilegeList(PrivilegeManager privilegeManager) {
        privilegeList= privilegeManager.listAllPrivileges();
    }

    @Override
    public int getRowCount() {
        return privilegeList.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public String getColumnName(int column) {
        switch(column) {
            case 0:
                return "id";
            case 1:
                return "名称";
            case 2:
                return "业务流程";
            case 3:
                return "任务";
            case 4:
                return "备注";
        }
        return "";
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Privilege privilege=privilegeList.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return privilege.getId();
            case 1:
                return privilege.getName();
            case 2:
                return privilege.getProcessName();
            case 3:
                return privilege.getTaskName();
            case 4:
                return privilege.getNote();
        }
        return "";
    }

    public void createPrivilege(Privilege privilege) {
        privilegeManager.create(privilege);
        privilegeList.add(privilege);
        fireTableRowsInserted(privilegeList.size()-1,privilegeList.size()-1);
    }

    public void removePrivilegAt(int index) {
        Privilege privilege=privilegeList.get(index);
        if (privilege==null)
            return ;
        privilegeManager.removePrivilege(privilege);
        privilegeList.remove(privilege);
        fireTableRowsDeleted(index,index);
    }
}
