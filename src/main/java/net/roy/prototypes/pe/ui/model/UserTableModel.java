package net.roy.prototypes.pe.ui.model;

import net.roy.prototypes.pe.domain.User;
import net.roy.prototypes.pe.domain.UserManager;

import javax.swing.table.AbstractTableModel;
import java.util.Collections;
import java.util.List;

/**
 * Created by Roy on 2015/12/28.
 */
public class UserTableModel extends AbstractTableModel {
    private UserManager userManager;
    private List<User> userList= Collections.emptyList();

    public UserTableModel(UserManager userManager) {
        this.userManager = userManager;
        reload();
    }

    public void reload() {
        int oldSize=userList.size();
        userList=userManager.listUsers();
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return userList.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "ID";
            case 1:
                return "帐号";
            case 2:
                return "姓名";
            case 3:
                return "身份证号";
            case 4:
                return "是否启用";
        }
        return "";
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        User user=userList.get(rowIndex);
        switch(columnIndex){
            case 0:
                return user.getId();
            case 1:
                return user.getAccount();
            case 2:
                return user.getName();
            case 3:
                return user.getIdCardNo();
            case 4:
                return user.isEnabled()?"是":"否";
        }
        return null;
    }

    /**
     * 获取表中位于第index行的User对象
     * @param index 表中第几行
     * @return 该行对应的User对象
     */
    public User getUserAt(int index) {
        return userList.get(index);
    }
}
