package net.roy.prototypes.pe.ui;

import net.roy.prototypes.pe.domain.*;
import net.roy.prototypes.pe.ui.model.DepartmentJobListModel;
import net.roy.prototypes.pe.ui.model.OrganizationTreeModel;
import net.roy.prototypes.pe.ui.model.PrivilegeListModel;
import net.roy.prototypes.pe.ui.model.UserTableModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Collections;
import java.util.List;

/**
 * 部门组织结构管理
 * Created by Roy on 2015/12/20.
 */
public class DepartmentManageForm {
    private JPanel panel;
    private JTree organizationTree;
    private JSplitPane mainPanel;
    private JTabbedPane tabbedPane1;
    private JToolBar organizationTreeToolbar;
    private JList lstJobs;
    private JButton btnAddJob;
    private JTextField txtJobName;
    private JButton btnRemoveJob;
    private JList lstDepartmentPrivileges;
    private JList lstNonDepartmentPrivileges;
    private JButton btnAddPrivilege;
    private JButton btnRemovePrivilege;
    private JTable tblUsers;
    private ListAssignForm<Privilege> privilegeAssignForm;
    private ListAssignForm<User> userAssignForm;
    private JButton btnUpdateJobPrivileges;
    private JButton btnUpdateJobUsers;

    private UserTableModel tblUsersModel;

    private DepartmentManager departmentManager;
    private PrivilegeManager privilegeManager;
    private UserManager userManager;
    private JPopupMenu popupMenu;

    public DepartmentManageForm(DepartmentManager departmentManager,PrivilegeManager privilegeManager, UserManager userManager) {
        this.departmentManager = departmentManager;
        this.privilegeManager=privilegeManager;
        this.userManager=userManager;

        initOrganizationTree(departmentManager);

        initPopupMenu();
        organizationTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        lstJobs.setModel(new DepartmentJobListModel(null, departmentManager));

        btnAddJob.addActionListener(e -> {
            DepartmentJobListModel model = (DepartmentJobListModel) lstJobs.getModel();
            model.addJob();
        });
        btnRemoveJob.addActionListener(e -> {
            DepartmentJobListModel model = (DepartmentJobListModel) lstJobs.getModel();
            model.removeJobAt(lstJobs.getSelectedIndex());
            btnRemoveJob.setEnabled(false);
        });

        lstJobs.addListSelectionListener(e -> {
            if (lstJobs.getSelectedValue() == null) {
                txtJobName.setText("");
                txtJobName.setEnabled(false);
                btnRemoveJob.setEnabled(false);
                privilegeAssignForm.clear();
                userAssignForm.clear();
            } else {
                Job job=(Job)lstJobs.getSelectedValue();
                Department department=(Department) (organizationTree.getSelectionPath().getLastPathComponent());
                txtJobName.setEnabled(true);
                txtJobName.setText(job.toString());
                btnRemoveJob.setEnabled(true);
                privilegeAssignForm.init(privilegeManager.listJobPrivileges(department,job),privilegeManager.listNonJobPrivileges(department,job));
                userAssignForm.init(userManager.listJobUsers(department,job),userManager.listNonJobUsers(department,job));
            }
        });

        txtJobName.addActionListener(e -> {
            DepartmentJobListModel model = (DepartmentJobListModel) lstJobs.getModel();
            model.renameJobAt(lstJobs.getSelectedIndex(), e.getActionCommand());
        });

        lstDepartmentPrivileges.setModel(new PrivilegeListModel());
        lstNonDepartmentPrivileges.setModel(new PrivilegeListModel());
        lstDepartmentPrivileges.addListSelectionListener(e -> {
            btnRemovePrivilege.setEnabled(!lstDepartmentPrivileges.isSelectionEmpty());
        });
        lstNonDepartmentPrivileges.addListSelectionListener(e -> {
            btnAddPrivilege.setEnabled(!lstNonDepartmentPrivileges.isSelectionEmpty());
        });
        btnAddPrivilege.addActionListener(e -> {
            Department department = (Department) (organizationTree.getSelectionPath().getLastPathComponent());
            List<Privilege> lst = lstNonDepartmentPrivileges.getSelectedValuesList();
            departmentManager.assignPrivilegeTo(department, lst);
            PrivilegeListModel deptPrivilegeListModel = (PrivilegeListModel) lstDepartmentPrivileges.getModel();
            PrivilegeListModel nonDeptPrivilegeListModel = (PrivilegeListModel) lstNonDepartmentPrivileges.getModel();
            deptPrivilegeListModel.setPrivilegeList(departmentManager.listDepartmentPrivileges(department));
            nonDeptPrivilegeListModel.setPrivilegeList(departmentManager.listPrivilegesNotBelongTo(department));
        });
        lstDepartmentPrivileges.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                lstDepartmentPrivileges.clearSelection();
            }
        });
        lstNonDepartmentPrivileges.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                lstNonDepartmentPrivileges.clearSelection();
            }
        });

        tblUsersModel=new UserTableModel();
        tblUsers.setModel(tblUsersModel);

        btnUpdateJobPrivileges.addActionListener(this::onUpdateJobPrivileges);
        btnUpdateJobUsers.addActionListener(this::onUpdateJobUsers);
    }

    private void onUpdateJobUsers(ActionEvent actionEvent) {
        Job job=(Job)lstJobs.getSelectedValue();
        Department department=(Department) (organizationTree.getSelectionPath().getLastPathComponent());
        userManager.updateJobUsers(department,job,userAssignForm.getAssignList());
    }

    private void onUpdateJobPrivileges(ActionEvent actionEvent) {
        Job job=(Job)lstJobs.getSelectedValue();
        Department department=(Department) (organizationTree.getSelectionPath().getLastPathComponent());
        privilegeManager.updateJobPrivileges(department,job,privilegeAssignForm.getAssignList());
    }

    private void initPopupMenu() {
        popupMenu = new JPopupMenu();
        final Action addAction = new AbstractAction("增加部门",
                new ImageIcon(this.getClass().getResource("/icons/Add.png"))) {
            @Override
            public void actionPerformed(ActionEvent e) {
                addDepartmentInTree();
            }
        };
        addAction.setEnabled(false);
        organizationTreeToolbar.add(addAction);
        popupMenu.add(addAction);

        final Action deleteAction = new AbstractAction("删除部门",
                new ImageIcon(this.getClass().getResource("/icons/Delete.png"))) {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeDepartmentInTree();
            }
        };
        deleteAction.setEnabled(false);
        organizationTreeToolbar.add(deleteAction);
        popupMenu.add(deleteAction);
        organizationTree.setComponentPopupMenu(popupMenu);
        organizationTree.addTreeSelectionListener(e -> {
            if (organizationTree.getSelectionCount() == 0) {
                addAction.setEnabled(false);
                deleteAction.setEnabled(false);
                btnAddJob.setEnabled(false);
                loadDepartmentInfo(null);
            } else {
                addAction.setEnabled(true);
                deleteAction.setEnabled(true);
                btnAddJob.setEnabled(true);

                Department department = (Department) (organizationTree.getSelectionPath().getLastPathComponent());
                loadDepartmentInfo(department);
            }
        });
    }



    private void loadDepartmentInfo(Department department) {
        DepartmentJobListModel jobListModel = (DepartmentJobListModel) lstJobs.getModel();
        PrivilegeListModel deptPrivilegeListModel = (PrivilegeListModel) lstDepartmentPrivileges.getModel();
        PrivilegeListModel nonDeptPrivilegeListModel = (PrivilegeListModel) lstNonDepartmentPrivileges.getModel();
        jobListModel.setDepartment(department);
        if (department==null) {
            deptPrivilegeListModel.setPrivilegeList(Collections.emptyList());
            nonDeptPrivilegeListModel.setPrivilegeList(Collections.emptyList());
            tblUsersModel.setUserList(Collections.emptyList());
        }  else {
            deptPrivilegeListModel.setPrivilegeList(departmentManager.listDepartmentPrivileges(department));
            nonDeptPrivilegeListModel.setPrivilegeList(departmentManager.listPrivilegesNotBelongTo(department));
            tblUsersModel.setUserList(departmentManager.listUsersInDepartment(department));
        }
    }

    /**
     * 在OrganizationTree中删除一个部门
     */
    private void removeDepartmentInTree() {
        OrganizationTreeModel organizationTreeModel = (OrganizationTreeModel) organizationTree.getModel();
        organizationTreeModel.removeDepartmentNode(organizationTree.getSelectionPath());
    }

    /**
     * 在OrganizationTree中增加一个新部门
     */
    private void addDepartmentInTree() {
        OrganizationTreeModel organizationTreeModel = (OrganizationTreeModel) organizationTree.getModel();
        organizationTreeModel.addDepartmentNode(organizationTree.getSelectionPath(), "新建部门");
        organizationTree.expandPath(organizationTree.getSelectionPath());
    }

    private void initOrganizationTree(DepartmentManager departmentManager) {
        TreeModel model = new OrganizationTreeModel(departmentManager);
        organizationTree.setModel(model);
    }

    public JPanel getPanel() {
        return panel;
    }


    public void reload() {
        //NOTE: 切换后需要重新刷新的代码放这里
    }

}
