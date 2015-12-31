package net.roy.prototypes.pe.ui;

import net.roy.prototypes.pe.domain.Privilege;
import org.springframework.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Roy on 2015/12/25.
 */
public class AddPrivilegeForm {
    private JTextField txtName;
    private JTextArea txtNote;
    private JButton btnAdd;
    private JTextField txtProcessName;
    private JTextField txtTaskName;
    private JPanel mainPanel;

    private PrivilegeManageForm privilegeManageForm;

    public AddPrivilegeForm(PrivilegeManageForm privilegeManageForm) {
        this.privilegeManageForm = privilegeManageForm;

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (StringUtils.isEmpty(txtName.getText()) )
                    return ;
                Privilege privilege = new Privilege(0, txtName.getText(),
                        txtProcessName.getText(), txtTaskName.getText(), txtNote.getText());
                privilegeManageForm.createPrivilege(privilege);
                clear();
            }
        });
    }

    private void clear() {
        txtName.setText("");
        txtProcessName.setText("");
        txtTaskName.setText("");
        txtNote.setText("");
    }

    public JPanel getPanel() {
        return mainPanel;
    }

}
