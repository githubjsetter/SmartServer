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
public class CQueryMasterFormWindow extends CSteFormWindow{
    public CQueryMasterFormWindow(CFrame owner, Steform steform, ActionListener actionlistener, String title) throws HeadlessException {
        super(owner, steform, actionlistener, title);
    }


    protected CStetoolbar createToolbar() {
        return new CQueryMdetoolbar(this);
    }

    protected void setHotkey(Container cp) {
        JComponent jcp = (JComponent) cp;
        MdeControlFactory.setQueryHotkey(jcp,actionlistener);
    }
}
