package net.roy.prototypes.pe.ui;

import net.roy.prototypes.pe.domain.Department;
import net.roy.prototypes.pe.domain.DepartmentManager;
import net.roy.prototypes.pe.domain.Privilege;
import net.roy.prototypes.pe.domain.PrivilegeManager;
import net.roy.prototypes.pe.ui.model.PrivilegesTableModel;
import net.roy.tools.SwingTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Created by Roy on 2015/12/24.
 */
public class PrivilegeManageForm {
    private final PrivilegeManager privilegeManager;
    private JPanel mainPanel;
    private JTable tblPrivileges;
    private JTabbedPane privilegeInfoPane;
    private JToolBar toolBar;
    private JSplitPane splitPane;
    private JButton btnUpdatePriviligeInfo;
    private PrivilegeInfoForm privilegeInfoForm;
    private AddPrivilegeForm addPrivilegeForm;

    private PrivilegesTableModel tblPrivilegesModel;

    public PrivilegeManageForm(PrivilegeManager privilegeManager,DepartmentManager departmentManager) {
        this.privilegeManager = privilegeManager;

        this.addPrivilegeForm = new AddPrivilegeForm(this,departmentManager);

        privilegeInfoForm.init(privilegeManager,departmentManager,false);

        btnUpdatePriviligeInfo.addActionListener(this::onUpdatePrivilegeInfo);

        tblPrivilegesModel=new PrivilegesTableModel(privilegeManager);
        tblPrivileges.setModel(tblPrivilegesModel);
        tblPrivileges.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        final Action addAction = new AbstractAction("添加", new ImageIcon(this.getClass().getResource("/icons/Add.png"))) {
            @Override
            public void actionPerformed(ActionEvent e) {
                tblPrivileges.clearSelection();
                tblPrivileges.setEnabled(false);
                splitPane.setRightComponent(addPrivilegeForm.getPanel());
                splitPane.setVisible(true);
            }
        };
        toolBar.add(addAction);

        final Action removeAction =new AbstractAction("删除",new ImageIcon(this.getClass().getResource("/icons/Delete.png"))) {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index=tblPrivileges.getSelectedRow();
                tblPrivilegesModel.removePrivilegAt(index);
            }
        };
        removeAction.setEnabled(false);
        toolBar.add(removeAction);


        tblPrivileges.getSelectionModel().addListSelectionListener(e -> {
            removeAction.setEnabled(tblPrivileges.getSelectedRowCount() > 0);
            setInfoPaneEnable(tblPrivileges.getSelectedRowCount() > 0);
            if (tblPrivileges.getSelectedRowCount()>0) {
                privilegeInfoForm.setCurrentPrivilege(tblPrivilegesModel.getPrivilegeAt(tblPrivileges.getSelectedRow()));
            } else {
                privilegeInfoForm.setCurrentPrivilege(null);
            }
        });
    }

    private void setInfoPaneEnable(boolean enabled) {
        privilegeInfoPane.setEnabled(enabled);
        privilegeInfoForm.setEnabled(enabled);
    }

    private void onUpdatePrivilegeInfo(ActionEvent actionEvent) {
        Privilege privilege=privilegeInfoForm.getPrivilegeForUpdate();
        privilegeManager.update(privilegeInfoForm.getPrivilegeForUpdate());
        privilegeManager.updateDepartmentsHavingPrivilege(privilege, privilegeInfoForm.getDepartmentList());
        for (int i=0;i<tblPrivilegesModel.getColumnCount();i++) {
            tblPrivilegesModel.fireTableCellUpdated(tblPrivileges.getSelectedRow(), i);
        }
    }

    public Container getPanel() {
        return mainPanel;
    }

    public void reload() {
        //NOTE: 需要在切换到前台后刷新内容的代码放这里
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    public void createPrivilege(Privilege privilege, List<Department> departmentList) {
        privilegeManager.create(privilege);
        privilegeManager.setDepartmentsHavingPrivilege(privilege,departmentList);
        tblPrivilegesModel.addPrivilege(privilege);
        closeAddPrivilegeForm();
    }

    public void closeAddPrivilegeForm() {
        tblPrivileges.setEnabled(true);
        splitPane.setRightComponent(privilegeInfoPane);
        splitPane.setVisible(true);
    }
}
