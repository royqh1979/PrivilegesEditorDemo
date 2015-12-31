package net.roy.prototypes.pe.ui;

import net.roy.prototypes.pe.domain.Department;
import net.roy.prototypes.pe.domain.DepartmentManager;
import net.roy.prototypes.pe.domain.UserManager;
import net.roy.prototypes.pe.ui.model.DepartmentListModel;
import net.roy.prototypes.pe.ui.model.OrganizationTreeListModel;
import net.roy.prototypes.pe.ui.model.OrganizationTreeModel;
import net.roy.prototypes.pe.ui.render.OrganizationTreeListRender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Roy on 2015/12/27.
 */
public class AddUserForm {
    private JCheckBox cbEnabled;
    private JComboBox cbDepartment;
    private JButton btnAdd;
    private JTextField txtAccount;
    private JTextField txtUsername;
    private JTextField txtIdCardNo;
    private JPanel mainPanel;
    private JList<String> lstDepartments;
    private JButton btnAddDepartment;

    public AddUserForm(UserManageForm userManageForm,DepartmentManager departmentManager) {
        cbDepartment.setModel(new OrganizationTreeListModel(departmentManager));
        cbDepartment.setRenderer(new OrganizationTreeListRender());
        lstDepartments.setModel(new DepartmentListModel());

        btnAdd.addActionListener(e -> {
            DepartmentListModel departmentListModel=(DepartmentListModel)lstDepartments.getModel();
            userManageForm.createUser(
                    txtAccount.getText(),
                    txtUsername.getText(),
                    txtIdCardNo.getText(),
                    departmentListModel.getDepartmentList(),
                    cbEnabled.isSelected()
            );
            clear();
        });
        btnAddDepartment.addActionListener(e -> {

            DepartmentListModel departmentListModel=(DepartmentListModel)lstDepartments.getModel();
            OrganizationTreeListModel organizationTreeListModel=(OrganizationTreeListModel)cbDepartment.getModel();
            Department department=(Department)organizationTreeListModel.getSelectedItem();
            departmentListModel.add(department,organizationTreeListModel.getDepartmentFullPath(department));
        });
    }



    private void clear() {
        txtAccount.setText("");
        txtUsername.setText("");
        txtIdCardNo.setText("");
        cbEnabled.setSelected(true);
        cbDepartment.setSelectedItem(null);
        DepartmentListModel departmentListModel=(DepartmentListModel)lstDepartments.getModel();
        departmentListModel.clear();
    }

    public Component getPanel() {
        return mainPanel;
    }
}
