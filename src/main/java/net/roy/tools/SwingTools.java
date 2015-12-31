package net.roy.tools;

import javax.swing.*;

/**
 * Created by Roy on 2015/12/30.
 */
public abstract class SwingTools {
    public static void setChildComponentEnabled(JPanel panel, boolean enabled) {
        for (int i=0;i<panel.getComponentCount();i++) {
            panel.getComponent(i).setEnabled(enabled);
        }
    }
}
