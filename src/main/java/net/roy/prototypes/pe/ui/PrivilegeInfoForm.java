package net.roy.prototypes.pe.ui;

import com.google.common.base.Preconditions;
import net.roy.prototypes.pe.domain.Department;
import net.roy.prototypes.pe.domain.DepartmentManager;
import net.roy.prototypes.pe.domain.Privilege;
import net.roy.prototypes.pe.domain.PrivilegeManager;
import net.roy.prototypes.pe.ui.model.DepartmentListModel;
import net.roy.prototypes.pe.ui.model.OrganizationTreeListModel;
import net.roy.prototypes.pe.ui.render.OrganizationTreeListRenderer;
import net.roy.tools.SwingTools;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by Roy on 2015/12/25.
 */
public class PrivilegeInfoForm {
    private JTextField txtName;
    private JTextArea txtNote;
    private JTextField txtProcessName;
    private JTextField txtTaskName;
    private JPanel mainPanel;
    private JList lstDepartments;
    private JComboBox cbDepartment;
    private JButton btnAddDepartment;
    private JButton btnRemoveDepartment;

    private boolean isForAdd=true;

    private PrivilegeManager privilegeManager;
    private DepartmentManager departmentManager;

    private DepartmentListModel lstDepartmentsModel;
    private OrganizationTreeListModel cbDepartmentModel;

    private Privilege currentPrivilege;

    public void init(PrivilegeManager privilegeManager,DepartmentManager departmentManager,boolean isForAdd) {
        this.privilegeManager=privilegeManager;
        this.departmentManager=departmentManager;
        this.isForAdd=isForAdd;
        cbDepartmentModel =new OrganizationTreeListModel(departmentManager);
        lstDepartmentsModel =new DepartmentListModel();
        cbDepartment.setModel(cbDepartmentModel);
        lstDepartments.setModel(lstDepartmentsModel);
        cbDepartment.setRenderer(OrganizationTreeListRenderer.UIResource);

        cbDepartment.addActionListener(this::onCbDepartmentSelect);
        lstDepartments.addListSelectionListener(this::onLstDepartmentsSelect);

        btnAddDepartment.addActionListener(this::onAddDepartment);
        btnRemoveDepartment.addActionListener(this::onRemoveDepartment);

        if (isForAdd) {
            setEnabled(true);
        } else {
            setEnabled(false);
        }
    }

    private void onPrivilegeInfoChanged(ActionEvent actionEvent) {
    }


    private void onRemoveDepartment(ActionEvent actionEvent) {
        lstDepartmentsModel.removeAt(lstDepartments.getSelectedIndex());
    }


    public void setEnabled(boolean enabled) {
        SwingTools.setChildComponentEnabled(mainPanel,enabled);
    }

    private void onAddDepartment(ActionEvent actionEvent) {
        Department department=(Department)cbDepartment.getSelectedItem();
        lstDepartmentsModel.add(department,cbDepartmentModel.getDepartmentFullPath(department));
    }

    private void onLstDepartmentsSelect(ListSelectionEvent listSelectionEvent) {
        btnRemoveDepartment.setEnabled(lstDepartments.getSelectedIndex()>=0);
    }

    private void onCbDepartmentSelect(ActionEvent actionEvent) {
        Department department=(Department)cbDepartment.getSelectedItem();
        if (department==null) {
            btnAddDepartment.setEnabled(false);
        } else {
            btnAddDepartment.setEnabled(!lstDepartmentsModel.isDepartmentInList(department));
        }
    }


    void clear() {
        txtName.setText("");
        txtProcessName.setText("");
        txtTaskName.setText("");
        txtNote.setText("");
        cbDepartment.setSelectedItem(null);
        lstDepartmentsModel.clear();
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public Privilege getPrivilegeForUpdate() {
        currentPrivilege.setName(txtName.getText());
        currentPrivilege.setProcessName(txtProcessName.getText());
        currentPrivilege.setTaskName(txtTaskName.getText());
        currentPrivilege.setNote(txtNote.getText());
        return currentPrivilege;
    }

    public void setCurrentPrivilege(Privilege currentPrivilege) {
        this.currentPrivilege = currentPrivilege;
        if (currentPrivilege==null) {
            clear();
            setEnabled(false);
        } else {
            loadPrivilegeInfo();
            setEnabled(true);
        }
    }

    private void loadPrivilegeInfo() {
        Preconditions.checkState(!isForAdd);
        txtName.setText(currentPrivilege.getName());
        txtProcessName.setText(currentPrivilege.getProcessName());
        txtTaskName.setText(currentPrivilege.getTaskName());
        txtNote.setText(currentPrivilege.getNote());

        cbDepartment.setSelectedItem(null);
        lstDepartmentsModel.clear();

        List<Department> departmentList=privilegeManager.listDepartmentsHavingPrivilege(currentPrivilege);

        for (Department department:departmentList) {
            lstDepartmentsModel.add(department,cbDepartmentModel.getDepartmentFullPath(department));
        }
    }

    public List<Department> getDepartmentList() {
        return lstDepartmentsModel.getDepartmentList();
    }

    public Privilege getPrivilegeForAdd() {
        return new Privilege(0,txtName.getText(),
                txtProcessName.getText(),txtTaskName.getText(),
                txtNote.getText());
    }
}
