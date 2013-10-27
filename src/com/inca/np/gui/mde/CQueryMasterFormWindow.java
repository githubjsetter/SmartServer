package com.inca.np.gui.mde;

import java.awt.Container;
import java.awt.HeadlessException;
import java.awt.event.ActionListener;

import javax.swing.JComponent;

import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.CSteFormWindow;
import com.inca.np.gui.control.CStetoolbar;
import com.inca.np.gui.ste.Steform;

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
