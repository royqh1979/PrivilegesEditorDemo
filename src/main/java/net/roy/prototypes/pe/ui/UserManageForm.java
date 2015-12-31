package net.roy.prototypes.pe.ui;

import net.roy.prototypes.pe.domain.Department;
import net.roy.prototypes.pe.domain.DepartmentManager;
import net.roy.prototypes.pe.domain.User;
import net.roy.prototypes.pe.domain.UserManager;
import net.roy.prototypes.pe.ui.model.DepartmentListModel;
import net.roy.prototypes.pe.ui.model.OrganizationTreeListModel;
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
    private JToolBar toolBar;
    private JSplitPane splitPane;
    private JCheckBox cbEnabled;
    private JComboBox cbDepartment;
    private JTextField txtAccount;
    private JTextField txtUsername;
    private JTextField txtIdCardNo;
    private JList lstDepartments;
    private JButton btnAddDepartment;
    private JPanel pnlBasicUserInfo;
    private JButton btnRemoveDepartment;
    private AddUserForm addUserForm;

    private UserManager userManager;

    public UserManageForm(UserManager userManager, DepartmentManager departmentManager) {
        this.userManager = userManager;
        tblUser.setModel(new UserTableModel(userManager));
        tblUser.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        cbDepartment.setModel(new OrganizationTreeListModel(departmentManager));
        cbDepartment.addActionListener(e->{
            System.out.println("oooo");
            if (cbDepartment.getSelectedItem()==null) {
                btnAddDepartment.setEnabled(false);
            } else {
                DepartmentListModel departmentListModel = (DepartmentListModel) lstDepartments.getModel();
                List<Department> departmentList = departmentListModel.getDepartmentList();
                Department department=(Department)cbDepartment.getSelectedItem();
                if (departmentList.stream().anyMatch(dept -> dept.getId() == department.getId())) {
                    btnAddDepartment.setEnabled(false);
                } else {
                    btnAddDepartment.setEnabled(true);
                }
            }
        });
        lstDepartments.setModel(new DepartmentListModel());
        lstDepartments.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstDepartments.addListSelectionListener(e -> {
            btnRemoveDepartment.setEnabled(lstDepartments.getSelectedIndex()>=0);
        });

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
                userTableModel.reload();
                tblUser.clearSelection();
            }
        };
        removeAction.setEnabled(false);
        toolBar.add(removeAction);
        setUserInfoPanelEnabled(false);

        tblUser.getSelectionModel().addListSelectionListener(
                e -> {
                    removeAction.setEnabled(tblUser.getSelectedRowCount() > 0);
                    setUserInfoPanelEnabled(tblUser.getSelectedRowCount() > 0) ;
                    if (tblUser.getSelectedRowCount() > 0) {
                        loadCurrentUserInfo();
                    } else {
                        clearUserInfoPane();
                    }
                });
        txtAccount.addActionListener(this::userInfoChanged);
        txtUsername.addActionListener(this::userInfoChanged);
        txtIdCardNo.addActionListener(this::userInfoChanged);
        cbEnabled.addActionListener(this::userInfoChanged);
        btnAddDepartment.addActionListener(e -> {
            Department department=(Department)cbDepartment.getSelectedItem();
            User user = getCurrentSelectionInTblUser();
            userManager.addUserDepartment(user,department);
            addDepartmentToLstDepartment(department);

        });

        btnRemoveDepartment.addActionListener(e->{
            Department department = getCurrentSelectionInLstDepartment();
            User user = getCurrentSelectionInTblUser();
            userManager.removeUserDepartment(user,department);
            removeDepartmentFromLstDepartment(department);
        });
    }

    private void clearUserInfoPane() {
        txtAccount.setText("");
        txtUsername.setText("");
        txtIdCardNo.setText("");
        cbEnabled.setSelected(true);
        cbDepartment.setSelectedItem(null);
        DepartmentListModel departmentListModel=(DepartmentListModel)lstDepartments.getModel();
        departmentListModel.clear();
    }

    private void removeDepartmentFromLstDepartment(Department department) {
        DepartmentListModel departmentListModel=(DepartmentListModel)lstDepartments.getModel();
        departmentListModel.removeAt(lstDepartments.getSelectedIndex());
    }

    private void addDepartmentToLstDepartment(Department department) {
        DepartmentListModel departmentListModel=(DepartmentListModel)lstDepartments.getModel();
        OrganizationTreeListModel organizationTreeListModel=(OrganizationTreeListModel)cbDepartment.getModel();
        departmentListModel.add(department,organizationTreeListModel.getDepartmentFullPath(department));
    }

    private User getCurrentSelectionInTblUser() {
        UserTableModel userTableModel=(UserTableModel)tblUser.getModel();
        return userTableModel.getUserAt(tblUser.getSelectedRow());
    }

    /**
     * 获取lstDepartment中当前选中的部门
     * @return
     */
    private Department getCurrentSelectionInLstDepartment() {
        DepartmentListModel departmentListModel=(DepartmentListModel)lstDepartments.getModel();
        return departmentListModel.getDepartmentList().get(lstDepartments.getSelectedIndex());
    }

    private void loadCurrentUserInfo() {
        User user = getCurrentSelectionInTblUser();
        if (user==null)
            return ;
        txtUsername.setText(user.getName());
        txtAccount.setText(user.getAccount());
        txtIdCardNo.setText(user.getIdCardNo());
        cbEnabled.setSelected(user.isEnabled());

        DepartmentListModel departmentListModel=(DepartmentListModel)lstDepartments.getModel();
        departmentListModel.clear();

        List<Department> departmentList=userManager.listUserDepartments(user);
        departmentList.stream().forEach(department -> {
            addDepartmentToLstDepartment(department);
        });
    }

    private void userInfoChanged(ActionEvent actionEvent) {

    }

    public Container getPanel() {
        return mainPanel;
    }

    public void reload() {
        //TODO: 需要在界面切换时刷新内容的处理放这里
    }

    public void createUser(String account, String username, String IdCardNo, List<Department> departmentList, boolean enabled) {
        userManager.createUser(account,username,IdCardNo,enabled,departmentList);
        splitPane.setRightComponent(pnlUserInfo);
        splitPane.setVisible(true);
        tblUser.setEnabled(true);
        UserTableModel userTableModel=(UserTableModel)tblUser.getModel();
        userTableModel.reload();
    }

    public void setUserInfoPanelEnabled(boolean enabled) {
        setChildComponentEnabled(pnlBasicUserInfo, enabled);
    }


}
