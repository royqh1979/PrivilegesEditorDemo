package net.roy.prototypes.pe.domain;

import net.roy.prototypes.pe.persist.UserPersister;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;

/**
 * Created by Roy on 2015/12/27.
 */
public class UserManager {
    private UserPersister userPersister;


    public void setUserPersister(UserPersister userPersister) {
        this.userPersister = userPersister;
    }


    public List<User> listUsers() {
        return userPersister.listUsers();
    }

    public void createUser(String account, String username, String idCardNo, boolean enabled, List<Department> departmentList ) {
        User user = new User(0, account, username, idCardNo, "", "", enabled);
        userPersister.create(user, departmentList);
    }

    public void removeUser(User user) {
        userPersister.remove(user);
    }

    public List<Department> listUserDepartments(User user) {
        return userPersister.listUserDepartments(user);
    }

    public void addUserDepartment(User user, Department department) {
        userPersister.addUserDepartment(user,department);
    }

    public void removeUserDepartment(User user, Department department) {
        userPersister.removeUserDepartment(user,department);
    }
}
