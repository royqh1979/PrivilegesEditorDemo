package net.roy.prototypes.pe.domain;

import java.util.List;

/**
 * Created by Roy on 2015/12/23.
 */
public class User {
    private long id;
    private String name;
    private String account;
    private String password;
    private String salt;
    private boolean enabled;
    private String idCardNo;

    public User(long id,String account, String name,String idCardNo,  String password, String salt,  boolean enabled) {
        this.id = id;
        this.name = name;
        this.account = account;
        this.password = password;
        this.salt = salt;
        this.enabled = enabled;
        this.idCardNo = idCardNo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    @Override
    public String toString() {
        return name;
    }
}
