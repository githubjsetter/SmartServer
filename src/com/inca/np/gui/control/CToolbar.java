package com.inca.np.gui.control;

import javax.swing.*;
import javax.swing.plaf.ToolBarUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicToolBarUI;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleRole;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-3-28
 * Time: 14:23:50
 * ¹¤¾ßÌõ
 */
public class CToolbar  extends JToolBar{
    public void removeDefaultKey(){
        InputMap inputmap = this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputmap.getParent().clear();
        this.setFocusable(false);

        for(int i=0;i<this.getComponentCount();i++){
            JComponent comp = (JComponent) this.getComponentAtIndex(i);
            comp.setFocusable(false);
        }
    }

}