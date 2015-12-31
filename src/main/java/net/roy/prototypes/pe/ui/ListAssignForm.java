package net.roy.prototypes.pe.ui;

import net.roy.prototypes.pe.ui.model.AssignListModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 用于将一个列表中的项分配给别人
 * Created by Roy on 2015/12/31.
 */
public class ListAssignForm<T> {
    private JList lstAssigned;
    private JList lstUnassigned;
    private JButton btnAssign;
    private JButton btnUnassign;
    private JPanel mainPanel;

    private AssignListModel<T> lstAssignedModel=new AssignListModel<>();
    private AssignListModel<T> lstUnassignedModel=new AssignListModel<>();

    public ListAssignForm() {
        lstAssigned.setModel(lstAssignedModel);
        lstUnassigned.setModel(lstUnassignedModel);

        btnAssign.addActionListener(this::onAssign);
        btnUnassign.addActionListener(this::onUnassign);

        lstAssigned.getSelectionModel().addListSelectionListener(this::onLstAssignedSelect);
        lstUnassigned.getSelectionModel().addListSelectionListener(this::onLstUnassignedSelect);
    }

    private void onLstUnassignedSelect(ListSelectionEvent listSelectionEvent) {
        btnAssign.setEnabled(lstUnassigned.getSelectedIndices().length>0);
    }

    private void onLstAssignedSelect(ListSelectionEvent listSelectionEvent) {
        btnUnassign.setEnabled(lstAssigned.getSelectedIndices().length>0);
    }

    private void onUnassign(ActionEvent actionEvent) {
        int[] indexes=lstAssigned.getSelectedIndices();
        for (int i=indexes.length-1;i>=0;i--){
            int idx=indexes[i];
            T value=lstAssignedModel.getList().get(idx);
            lstUnassignedModel.addElement(value);
            lstAssignedModel.removeElementAt(idx);
        }
    }

    private void onAssign(ActionEvent actionEvent) {
        int[] indexes=lstUnassigned.getSelectedIndices();
        for (int i=indexes.length-1;i>=0;i--){
            int idx=indexes[i];
            T value=lstUnassignedModel.getList().get(idx);
            lstAssignedModel.addElement(value);
            lstUnassignedModel.removeElementAt(idx);
        }
    }

    public void init(List<T> assignedList, List<T> unassignedList) {
        lstAssignedModel.setList(assignedList);
        lstUnassignedModel.setList(unassignedList);
    }

    public void clear() {
        lstAssignedModel.setList(Collections.emptyList());
        lstUnassignedModel.setList(Collections.emptyList());
    }

    public List<T> getAssignList() {
        return lstAssignedModel.getList();
    }
}
