package net.roy.prototypes.pe.ui;

import net.roy.prototypes.pe.domain.Department;
import net.roy.prototypes.pe.domain.DepartmentManager;
import net.roy.prototypes.pe.domain.User;
import net.roy.prototypes.pe.domain.UserManager;
import net.roy.prototypes.pe.ui.model.UserTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

import static net.roy.tools.SwingTools.setChildComponentEnabled;

/**
 * Created by Roy on 2015/12/24.
 */
public class UserManageForm {
    private JPanel mainPanel;
    private JTable tblUser;
    private JTabbedPane pnlUserInfo;
    private JPanel pnlBasicUserInfo;
    private JToolBar toolBar;
    private JSplitPane splitPane;
    private UserInfoForm userInfoForm;
    private JButton btnUpdateUserInfo;
    private AddUserForm addUserForm;

    private UserManager userManager;
    private UserTableModel tblUserModel;

    public UserManageForm(UserManager userManager, DepartmentManager departmentManager) {
        this.userManager = userManager;
        tblUserModel=new UserTableModel();
        tblUserModel.setUserList(userManager.listUsers());
        tblUser.setModel(tblUserModel);
        tblUser.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        userInfoForm.init(departmentManager, userManager, false);
        this.addUserForm = new AddUserForm(this, departmentManager);
        final Action addAction = new AbstractAction("添加", new ImageIcon(this.getClass().getResource("/icons/Add.png"))) {
            @Override
            public void actionPerformed(ActionEvent e) {
                tblUser.clearSelection();
                tblUser.setEnabled(false);
                splitPane.setRightComponent(addUserForm.getPanel());
                splitPane.setVisible(true);
            }
        };
        toolBar.add(addAction);

        final Action removeAction = new AbstractAction("删除",
                new ImageIcon(this.getClass().getResource("/icons/Delete.png"))) {
            @Override
            public void actionPerformed(ActionEvent e) {
                UserTableModel userTableModel = (UserTableModel) tblUser.getModel();
                userManager.removeUser(userTableModel.getUserAt(tblUser.getSelectedRow()));
                userTableModel.removeAt(tblUser.getSelectedRow());
                tblUser.clearSelection();
            }
        };
        removeAction.setEnabled(false);
        toolBar.add(removeAction);
        setUserInfoPanelEnabled(false);

        tblUser.getSelectionModel().addListSelectionListener(e -> {
            removeAction.setEnabled(tblUser.getSelectedRowCount() > 0);
            setUserInfoPanelEnabled(tblUser.getSelectedRowCount() > 0);
            if (tblUser.getSelectedRowCount() > 0) {
                userInfoForm.setCurrentUser(getCurrentSelectionInTblUser());
            } else {
                userInfoForm.setCurrentUser(null);
            }
        });

        btnUpdateUserInfo.addActionListener(this::onUpdateUserInfo);
    }

    private void onUpdateUserInfo(ActionEvent actionEvent) {
        User user=userInfoForm.getUserForUpdate();
        userManager.update(user);
        userManager.updateDepartments(user,userInfoForm.getDepartmentList());
        UserTableModel userTableModel=(UserTableModel)tblUser.getModel();
        for (int i=0;i<userTableModel.getColumnCount();i++) {
            userTableModel.fireTableCellUpdated(tblUser.getSelectedRow(),i);
        }
    }

    private User getCurrentSelectionInTblUser() {
        UserTableModel userTableModel=(UserTableModel)tblUser.getModel();
        return userTableModel.getUserAt(tblUser.getSelectedRow());
    }

    public Container getPanel() {
        return mainPanel;
    }

    public void reload() {
        //TODO: 需要在界面切换时刷新内容的处理放这里
    }


    public void setUserInfoPanelEnabled(boolean enabled) {
        setChildComponentEnabled(pnlBasicUserInfo, enabled);
        userInfoForm.setEnabled(enabled);
        btnUpdateUserInfo.setEnabled(enabled);
    }


    public void createUser(User user, List<Department> departmentList) {
        User newUser=userManager.createUser(
                user.getAccount(),
                user.getName(),
                user.getIdCardNo(),
                user.isEnabled(), departmentList);
        UserTableModel userTableModel=(UserTableModel)tblUser.getModel();
        userTableModel.addUser(newUser);
        closeAddUserForm();
    }

    public void closeAddUserForm() {
        splitPane.setRightComponent(pnlUserInfo);
        splitPane.setVisible(true);
        tblUser.setEnabled(true);
    }
}
