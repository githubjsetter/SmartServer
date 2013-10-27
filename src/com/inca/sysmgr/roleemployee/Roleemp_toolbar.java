package com.inca.sysmgr.roleemployee;

import java.awt.event.ActionListener;

import com.inca.np.gui.control.CMdetoolbar;
import com.inca.np.gui.mde.CMdeModel;

public class Roleemp_toolbar extends CMdetoolbar{

	public Roleemp_toolbar(ActionListener l) {
		super(l);
		// TODO Auto-generated constructor stub
	}


	@Override
	protected boolean isUsebutton(String actionname) {
		if(actionname.equals(CMdeModel.ACTION_DEL))return false;
		if(actionname.equals(CMdeModel.ACTION_MODIFY))return false;
		if(actionname.equals(CMdeModel.ACTION_NEW))return false;
		if(actionname.equals(CMdeModel.ACTION_UNDO))return false;
		return true;
	}

	
}
