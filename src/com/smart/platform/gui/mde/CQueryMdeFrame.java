package com.smart.platform.gui.mde;

import java.awt.HeadlessException;

import javax.swing.JComponent;

public abstract class CQueryMdeFrame extends MdeFrame{

	public CQueryMdeFrame(String title) throws HeadlessException {
		super(title);
	}


	@Override
	protected void setHotkey() {
        MdeControlFactory.setQueryHotkey((JComponent) getContentPane(), mdemodel);
	}
	
	

}
