package net.roy.prototypes.pe.ui;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import net.roy.prototypes.pe.domain.DepartmentManager;
import net.roy.prototypes.pe.domain.PrivilegeManager;
import net.roy.prototypes.pe.domain.UserManager;
import net.roy.prototypes.pe.persist.DepartmentPersister;
import net.roy.prototypes.pe.persist.JobPersister;
import net.roy.prototypes.pe.persist.PrivilegePersister;
import net.roy.prototypes.pe.persist.UserPersister;
import org.hsqldb.cmdline.SqlToolError;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by Roy on 2015/12/20.
 */
public class MainFrame extends JFrame {
    private DepartmentManageForm organizationStructureManageForm=null;
    private PrivilegeManageForm privilegeManageForm=null;
    private UserManageForm userManageForm=null;
    private DepartmentManager departmentManager =null;
    private PrivilegeManager privilegeManager=null;
    private UserManager userManager=null;

    public MainFrame(DepartmentManager departmentManager, PrivilegeManager privilegeManager, UserManager userManager) throws HeadlessException {
        super();
        this.departmentManager = departmentManager;
        this.privilegeManager = privilegeManager;
        this.userManager = userManager;

        this.setTitle("权限编辑器");

        JMenuBar menuBar=new JMenuBar();
        JMenu deptMenu=new JMenu("部门管理");
        Action manageDepartmentAction=new AbstractAction("部门设置") {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchOrganizationManageForm();
            }
        };
        JMenuItem item=new JMenuItem(manageDepartmentAction);
        deptMenu.add(item);
        menuBar.add(deptMenu);

        JMenu privilegeMenu=new JMenu("权限管理");
        Action managePrivilegeAction=new AbstractAction("权限管理") {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchPrivilegeManageForm();
            }
        };
        privilegeMenu.add(new JMenuItem(managePrivilegeAction));
        menuBar.add(privilegeMenu);

        JMenu userMenu=new JMenu("用户管理");
        Action manageUserAction=new AbstractAction("用户管理") {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchUserManageForm();
            }
        } ;
        userMenu.add(manageUserAction);
        menuBar.add(userMenu);
        this.setJMenuBar(menuBar);

        this.setBounds(0, 0, 800, 600);

        switchOrganizationManageForm();
    }

    private void switchUserManageForm() {
        //TODO: 显示用户管理界面
        if (userManageForm==null) {
            userManageForm=new UserManageForm(userManager,departmentManager) ;
        }
        setContentPane(userManageForm.getPanel());
        userManageForm.reload();
        setTitle("权限编辑器 - 用户管理");
        setVisible(true);
    }

    private void switchPrivilegeManageForm() {
        if (privilegeManageForm==null) {
            privilegeManageForm=new PrivilegeManageForm(privilegeManager,departmentManager);
        }
        setContentPane(privilegeManageForm.getPanel());
        privilegeManageForm.reload();
        setTitle("权限编辑器 - 权限管理");
        setVisible(true);
    }

    private void switchOrganizationManageForm() {
        if (organizationStructureManageForm==null) {
            organizationStructureManageForm=new DepartmentManageForm(departmentManager,privilegeManager,userManager);
        }
        setContentPane(organizationStructureManageForm.getPanel());
        organizationStructureManageForm.reload();
        setTitle("权限编辑器 - 组织结构管理");
        setVisible(true);
    }

    public void setDepartmentManager(DepartmentManager departmentManager) {
        this.departmentManager = departmentManager;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(()->{

            try {
                MainFrame frame = MainFrame.create();
                frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                SwingUtilities.updateComponentTreeUI(frame);
                //frame.pack();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public void setPrivilegeManager(PrivilegeManager privilegeManager) {
        this.privilegeManager = privilegeManager;
    }

    private static MainFrame create() throws IOException, SQLException, SqlToolError {
        ComboPooledDataSource dataSource=new ComboPooledDataSource();
        dataSource.setJdbcUrl("jdbc:sqlite:e:/test.db");
        Properties properties = new Properties();
        properties.setProperty("PRAGMA foreign_keys", "ON");
        dataSource.setProperties(properties);
        /*
        SqlFile sqlFile=new SqlFile(
                new File(MainFrame.class.getResource("/initScript.sql").getFile()),
                "UTF-8");
        sqlFile.setConnection(dataSource.getConnection());
        sqlFile.execute();
        */
        JdbcTemplate template=new JdbcTemplate();
        template.setDataSource(dataSource);

        DataSourceTransactionManager transactionManager=new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);


        DepartmentManager departmentManager=new DepartmentManager("林口林业局");

        DepartmentPersister departmentPersister=new DepartmentPersister();
        departmentPersister.setTemplate(template);
        JobPersister jobPersister=new JobPersister();
        jobPersister.setTemplate(template);
        UserPersister userPersister=new UserPersister();
        userPersister.setTemplate(template);
        userPersister.setTransactionManager(transactionManager);
        PrivilegePersister privilegePersister=new PrivilegePersister();
        privilegePersister.setTemplate(template);
        privilegePersister.setTransactionManager(transactionManager);
        departmentManager.setDepartmentPersister(departmentPersister);
        departmentManager.setJobPersister(jobPersister);
        departmentManager.setPrivilegePersister(privilegePersister);
        departmentManager.setUserPersister(userPersister);


        PrivilegeManager privilegeManager=new PrivilegeManager();
        privilegeManager.setPrivilegePersister(privilegePersister);
        privilegeManager.setJobPersister(jobPersister);
        privilegeManager.setUserPersister(userPersister);

        UserManager userManager=new UserManager();
        userManager.setUserPersister(userPersister);

        MainFrame frame=new MainFrame(departmentManager,privilegeManager,userManager);
        return frame;
    }
}
