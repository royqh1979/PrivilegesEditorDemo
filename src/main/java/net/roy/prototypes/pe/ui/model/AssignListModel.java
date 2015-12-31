package net.roy.prototypes.pe.ui.model;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

/**
 * Created by Roy on 2015/12/31.
 */
public class AssignListModel<T> extends AbstractListModel<T> {
    private List<T> list= Collections.emptyList();

    public void setList(List<T> list) {
        int oldSize=list.size();
        this.list = list;
        fireContentsChanged(this,0,Math.max(oldSize,list.size()));
    }

    @Override
    public int getSize() {
        return list.size();
    }

    @Override
    public T getElementAt(int index) {
        return list.get(index);
    }

    public void addElement(T value) {
        list.add(value);
        fireIntervalAdded(this,list.size()-1,list.size()-1);
    }

    public void removeElementAt(int i) {
        list.remove(i);
        fireIntervalRemoved(this,i,i);
    }

    public List<T> getList() {
        return list;
    }
}
