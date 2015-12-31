package net.roy.prototypes.pe.ui;

import com.google.common.base.Preconditions;
import net.roy.prototypes.pe.domain.Department;
import net.roy.prototypes.pe.domain.DepartmentManager;
import net.roy.prototypes.pe.domain.User;
import net.roy.prototypes.pe.domain.UserManager;
import net.roy.prototypes.pe.ui.model.DepartmentListModel;
import net.roy.prototypes.pe.ui.model.OrganizationTreeListModel;
import net.roy.prototypes.pe.ui.render.OrganizationTreeListRenderer;
import net.roy.tools.SwingTools;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by Roy on 2015/12/27.
 */
public class UserInfoForm {
    private JCheckBox cbEnabled;
    private JComboBox cbDepartment;
    private JButton btnAdd;
    private JTextField txtAccount;
    private JTextField txtUsername;
    private JTextField txtIdCardNo;
    private JList<String> lstDepartments;
    private JButton btnAddDepartment;
    private JPanel mainPanel;
    private JButton btnRemoveDepartment;
    private User currentUser;
    private UserManager userManager;

    private DepartmentListModel lstDepartmentsModel;
    private OrganizationTreeListModel cbDepartmentModel;
    private boolean isForAdd;


    public void init(DepartmentManager departmentManager,UserManager userManager,boolean isForAdd) {
        this.isForAdd=isForAdd;
        this.userManager=userManager;
        cbDepartmentModel =new OrganizationTreeListModel(departmentManager);
        lstDepartmentsModel =new DepartmentListModel();

        cbDepartment.setModel(cbDepartmentModel);
        cbDepartment.setRenderer(OrganizationTreeListRenderer.UIResource);

        cbDepartment.addActionListener(this::onCbSelect);
        lstDepartments.setModel(lstDepartmentsModel);
        lstDepartments.addListSelectionListener(this::onLstDepartmentsSelect);

        btnAddDepartment.addActionListener(this::doAddDepartmentToLstDepartments);
        btnRemoveDepartment.addActionListener(this::doRemoveDepartmentFromLstDepartments);
        if (!isForAdd) {
            setEnabled(false);
        } else {
            setEnabled(true);
        }

    }

    private void onCbSelect(ActionEvent actionEvent) {
        Department department=(Department)cbDepartment.getSelectedItem();
        if (department==null) {
            btnAddDepartment.setEnabled(false);
        } else {
            btnAddDepartment.setEnabled(!lstDepartmentsModel.isDepartmentInList(department));
        }
    }

    private void onLstDepartmentsSelect(ListSelectionEvent listSelectionEvent) {
        btnRemoveDepartment.setEnabled(lstDepartments.getSelectedIndex()>=0);
    }

    private void doRemoveDepartmentFromLstDepartments(ActionEvent actionEvent) {
        lstDepartmentsModel.removeAt(lstDepartments.getSelectedIndex());
    }

    private void doAddDepartmentToLstDepartments(ActionEvent actionEvent) {
        Department department = (Department) cbDepartment.getSelectedItem();
        addDepartmentToLstDepartment(department);
    }

    /**
     * 获取lstDepartment中当前选中的部门
     * @return
     */
    private Department getCurrentSelectionInLstDepartment() {
        return lstDepartmentsModel.getDepartmentList().get(lstDepartments.getSelectedIndex());
    }




    public User getUserForUpdate() {
        currentUser.setAccount(txtAccount.getText());
        currentUser.setName(txtUsername.getText());
        currentUser.setIdCardNo(txtIdCardNo.getText());
        currentUser.setEnabled(cbEnabled.isSelected());
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        Preconditions.checkState(!isForAdd);
        this.currentUser = currentUser;
        if (currentUser==null) {
            setEnabled(false);
            clear();
        } else {
            setEnabled(true);
            loadCurrentUserInfo();
        }
    }

    private void loadCurrentUserInfo() {
        if (currentUser==null)
            return ;
        txtUsername.setText(currentUser.getName());
        txtAccount.setText(currentUser.getAccount());
        txtIdCardNo.setText(currentUser.getIdCardNo());
        cbEnabled.setSelected(currentUser.isEnabled());

        lstDepartmentsModel.clear();

        List<Department> departmentList=userManager.listUserDepartments(currentUser);
        departmentList.stream().forEach(department -> {
            addDepartmentToLstDepartment(department);
        });
    }

    private void addDepartmentToLstDepartment(Department department) {
        lstDepartmentsModel.add(department, cbDepartmentModel.getDepartmentFullPath(department));
    }

    public void setEnabled(boolean enabled) {
        SwingTools.setChildComponentEnabled(mainPanel,enabled);
    }

    public void clear() {
        txtAccount.setText("");
        txtUsername.setText("");
        txtIdCardNo.setText("");
        cbEnabled.setSelected(true);
        cbDepartment.setSelectedItem(null);
        lstDepartmentsModel.clear();
    }

    public Component getPanel() {
        return mainPanel;
    }

    public User getUserForAdd() {
        return new User(0,txtAccount.getText(),
                txtUsername.getText(),
                txtIdCardNo.getText(),
                "",
                "",
                cbEnabled.isSelected());
    }

    public List<Department> getDepartmentList() {
        return lstDepartmentsModel.getDepartmentList();
    }


}


