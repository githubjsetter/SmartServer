package com.smart.platform.gui.mde;

import java.awt.Container;
import java.awt.HeadlessException;
import java.awt.event.ActionListener;

import javax.swing.JComponent;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CSteFormWindow;
import com.smart.platform.gui.control.CStetoolbar;
import com.smart.platform.gui.ste.Steform;

/**
 * @deprecated
 * @author Administrator
 *
 */
public class CQueryDetailFormWindow extends CSteFormWindow{
    public CQueryDetailFormWindow(CFrame owner, Steform steform, ActionListener actionlistener, String title) throws HeadlessException {
        super(owner, steform, actionlistener, title);
    }

    protected CStetoolbar createToolbar() {
        return null;
    }

    protected void setHotkey(Container cp) {
        super.setHotkey(cp);
        MdeControlFactory.setQueryHotkey((JComponent)this.getContentPane(),actionlistener);
    }

}
