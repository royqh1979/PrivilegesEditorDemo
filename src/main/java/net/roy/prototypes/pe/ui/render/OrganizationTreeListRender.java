package net.roy.prototypes.pe.ui.render;

import net.roy.prototypes.pe.domain.Department;
import net.roy.prototypes.pe.ui.model.DepartmentJobListModel;
import net.roy.prototypes.pe.ui.model.OrganizationTreeListModel;
import org.springframework.util.StringUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Roy on 2015/12/27.
 */
public class OrganizationTreeListRender extends JLabel implements ListCellRenderer<Department> {
    @Override
    public Component getListCellRendererComponent(JList<? extends Department> list, Department value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value==null) {
            setText("");
        } else {
            OrganizationTreeListModel model = (OrganizationTreeListModel) list.getModel();
            int level = model.getDepartmentLevel(value);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < level; i++) {
                builder.append("  ");
            }
            builder.append("-");
            builder.append(value.getName());
            setText(builder.toString());
        }
        return this;
    }
}
