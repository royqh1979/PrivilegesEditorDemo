package net.roy.prototypes.pe.domain;

import net.roy.prototypes.pe.persist.JobPersister;
import net.roy.prototypes.pe.persist.PrivilegePersister;
import net.roy.prototypes.pe.persist.UserPersister;

import java.util.*;

/**
 * Created by Roy on 2015/12/23.
 */
public class PrivilegeManager {
    private PrivilegePersister privilegePersister;
    private JobPersister jobPersister;
    private UserPersister userPersister;

   public void PrivilegeManager() {

   }


    public List<Privilege> listAllPrivileges() {
        return privilegePersister.listPrivileges();
    }

    public void setPrivilegePersister(PrivilegePersister privilegePersister) {
        this.privilegePersister = privilegePersister;
    }

    public void create(Privilege privilege) {
        privilegePersister.create(privilege);

    }

    public void removePrivilege(Privilege privilege) {
        if (countJobWithPrivilege(privilege)>0) {
             throw new OperationException("还有岗位拥有该权限");
        }

        if (countUserWithStandingPrivilege(privilege)>0) {
            throw new OperationException("还有员工被单独授予了该权限");
        }
        privilegePersister.remove(privilege);
    }

    private int countUserWithStandingPrivilege(Privilege privilege) {
        return userPersister.countUserWithPrivilege(privilege);
    }

    private int countJobWithPrivilege(Privilege privilege) {
        return jobPersister.countJobWithPrivilege(privilege) ;
    }

    public void setJobPersister(JobPersister jobPersister) {
        this.jobPersister = jobPersister;
    }

    public void setUserPersister(UserPersister userPersister) {
        this.userPersister = userPersister;
    }

    public List<Department> listDepartmentsHavingPrivilege(Privilege privilege) {
        return privilegePersister.listDepartmentsHavingPrivilege(privilege);
    }

    public void update(Privilege privilege) {
        privilegePersister.update(privilege);
    }

    public void updateDepartmentsHavingPrivilege(Privilege privilege, List<Department> departmentList) {
        privilegePersister.updateDepartmentsHavingPrivilege(privilege,departmentList);

    }

    public void setDepartmentsHavingPrivilege(Privilege privilege, List<Department> departmentList) {
        privilegePersister.saveDepartmentsHavingPrivilege(privilege,departmentList);
    }
}
