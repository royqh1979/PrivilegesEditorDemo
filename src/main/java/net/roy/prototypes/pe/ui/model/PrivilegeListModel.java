package net.roy.prototypes.pe.ui.model;

import net.roy.prototypes.pe.domain.Privilege;
import net.roy.prototypes.pe.domain.PrivilegeManager;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

/**
 * Created by Roy on 2015/12/26.
 */
public class PrivilegeListModel extends AbstractListModel<Privilege> {
    List<Privilege> privilegeList= Collections.emptyList();

    public PrivilegeListModel(List<Privilege> privilegeList) {
        this.privilegeList=privilegeList;

    }

    public PrivilegeListModel() {
    }

    public void setPrivilegeList(List<Privilege> privilegeList) {
        int oldLen=privilegeList.size();
        this.privilegeList = privilegeList;
        fireContentsChanged(this,0,Math.max(privilegeList.size()-1,oldLen));
    }

    @Override
    public int getSize() {
        return privilegeList.size();
    }

    @Override
    public Privilege getElementAt(int index) {
        return privilegeList.get(index);
    }
}
