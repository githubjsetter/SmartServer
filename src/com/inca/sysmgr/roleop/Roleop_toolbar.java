package com.inca.sysmgr.roleop;

import java.awt.event.ActionListener;

import com.inca.np.gui.control.CMdetoolbar;
import com.inca.np.gui.mde.CMdeModel;

public class Roleop_toolbar extends CMdetoolbar{

	public Roleop_toolbar(ActionListener l) {
		super(l);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void createOtherButton(ActionListener listener) {
		super.addButton("设置授权", "选择一个细单,设置授权属性", "setupap");
	}

	
}
