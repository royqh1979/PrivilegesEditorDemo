package net.roy.prototypes.pe.ui;

import net.roy.prototypes.pe.domain.Privilege;
import net.roy.prototypes.pe.domain.PrivilegeManager;
import net.roy.prototypes.pe.ui.model.PrivilegesTableModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Created by Roy on 2015/12/24.
 */
public class PrivilegeManageForm {
    private final PrivilegeManager privilegeManager;
    private JPanel mainPanel;
    private JTable tblPrivileges;
    private JTabbedPane privilegeInfoPane;
    private JTextField txtPrivilegeName;
    private JTextField txtDepartment;
    private JList lstJobs;
    private JList lstUsers;
    private JToolBar toolBar;
    private JSplitPane splitPane;
    private AddPrivilegeForm addPrivilegeForm;

    public PrivilegeManageForm(PrivilegeManager privilegeManager) {
        this.privilegeManager = privilegeManager;

        this.addPrivilegeForm = new AddPrivilegeForm(this);

        tblPrivileges.setModel(new PrivilegesTableModel(privilegeManager));

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
                PrivilegesTableModel privilegesTableModel=(PrivilegesTableModel)tblPrivileges.getModel();
                privilegesTableModel.removePrivilegAt(index);
            }
        };
        removeAction.setEnabled(false);
        toolBar.add(removeAction);


        tblPrivileges.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (tblPrivileges.getSelectedRowCount() > 0) {
                    removeAction.setEnabled(true);
                } else {
                    removeAction.setEnabled(false);
                }
            }
        });
    }

    public Container getPanel() {
        return mainPanel;
    }

    public void reload() {
        //NOTE: 需要在切换到前台后刷新内容的代码放这里
    }

    public void createPrivilege(Privilege privilege) {
        PrivilegesTableModel model = (PrivilegesTableModel) tblPrivileges.getModel();
        model.createPrivilege(privilege);
        tblPrivileges.setEnabled(true);
        splitPane.setRightComponent(privilegeInfoPane);
        splitPane.setVisible(true);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

}
