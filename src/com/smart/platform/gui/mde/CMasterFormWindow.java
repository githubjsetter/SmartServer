package com.smart.platform.gui.mde;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CSteFormWindow;
import com.smart.platform.gui.control.CStetoolbar;
import com.smart.platform.gui.ste.CQueryStemodel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.Steform;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-17
 * Time: 13:27:12
 * To change this template use File | Settings | File Templates.
 * @deprecated
 */
public class CMasterFormWindow extends CSteFormWindow{
    public CMasterFormWindow(CFrame owner, Steform steform, ActionListener actionlistener, String title) throws HeadlessException {
        super(owner, steform, actionlistener, title);
    }

    protected CStetoolbar createToolbar() {
        return MdeControlFactory.createMdetoolbar(actionlistener);
    }


	@Override
	protected void setHotkey(Container cp) {
		JComponent jcp = (JComponent) cp;
		KeyStroke vkctrlw = KeyStroke.getKeyStroke(KeyEvent.VK_W,
				InputEvent.CTRL_MASK, false);
		jcp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(vkctrlw,
				CSteModel.ACTION_HIDEFORM);
		jcp.getActionMap().put(CSteModel.ACTION_HIDEFORM,
				new SteformAction(CSteModel.ACTION_HIDEFORM));

		// esc Òþ²Ø±à¼­´°
		KeyStroke vkesc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		jcp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(vkesc,
				"cancel");
		jcp.getActionMap().put("cancel", new SteformAction("cancel"));

		if (steform.getStemodel() instanceof CQueryStemodel) {
			MdeControlFactory.setQueryHotkey(
					(JComponent) this.getContentPane(), this);
		} else {
			MdeControlFactory.setHotkey((JComponent) this.getContentPane(),
					this);
		}
	}
    
    
}
