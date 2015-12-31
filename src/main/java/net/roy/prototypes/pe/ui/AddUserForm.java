package net.roy.prototypes.pe.ui;

import net.roy.prototypes.pe.domain.DepartmentManager;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Roy on 2015/12/27.
 */
public class AddUserForm {
    private JButton btnAdd;
    private JPanel mainPanel;
    private UserInfoForm userInfoForm;
    private JButton btnCancel;


    public AddUserForm(UserManageForm userManageForm,DepartmentManager departmentManager) {
        userInfoForm.init(departmentManager,null,true);
        btnAdd.addActionListener(e -> {
            userManageForm.createUser(
                    userInfoForm.getUserForAdd(),
                    userInfoForm.getDepartmentList()
            );
            clear();
        });

        btnCancel.addActionListener(e->{
            userManageForm.closeAddUserForm();
        });
    }

    private void clear() {
        userInfoForm.clear();
    }

    public Component getPanel() {
        return mainPanel;
    }
}
