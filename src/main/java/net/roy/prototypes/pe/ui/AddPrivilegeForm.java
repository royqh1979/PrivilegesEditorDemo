package net.roy.prototypes.pe.ui;

import net.roy.prototypes.pe.domain.DepartmentManager;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by Roy on 2015/12/25.
 */
public class AddPrivilegeForm {
    private JButton btnAdd;
    private JPanel mainPanel;
    private PrivilegeInfoForm privilegeInfoForm;
    private JButton btnCancel;

    private PrivilegeManageForm privilegeManageForm;

    public AddPrivilegeForm(PrivilegeManageForm privilegeManageForm,DepartmentManager departmentManager) {
        this.privilegeManageForm = privilegeManageForm;

        privilegeInfoForm.init(null,departmentManager,true);

        btnAdd.addActionListener(this::onAdd);
        btnCancel.addActionListener(this::onCancel);
    }

    private void onCancel(ActionEvent actionEvent) {
        clear();
        privilegeManageForm.closeAddPrivilegeForm();
    }

    private void onAdd(ActionEvent actionEvent) {
        privilegeManageForm.createPrivilege(
                privilegeInfoForm.getPrivilegeForAdd(),
                privilegeInfoForm.getDepartmentList());
        clear();
    }

    private void clear() {
        privilegeInfoForm.clear();
    }

    public JPanel getPanel() {
        return mainPanel;
    }

}
