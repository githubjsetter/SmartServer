package com.smart.adminclient.installjar;

import java.awt.event.ActionListener;

import com.smart.platform.gui.control.CStetoolbar;
import com.smart.platform.gui.ste.CSteModel;

public class Edittoolbar extends CStetoolbar{

	public Edittoolbar(ActionListener l) {
		super(l);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean isUsebutton(String actionname) {
		if(actionname.equals(CSteModel.ACTION_UNDO))return false;
		if(actionname.equals(CSteModel.ACTION_MODIFY))return false;
		if(actionname.equals(CSteModel.ACTION_EXIT))return false;
		if(actionname.equals(CSteModel.ACTION_QUERY))return false;
		if(actionname.equals(CSteModel.ACTION_SETUPUI))return false;
		if(actionname.equals(CSteModel.ACTION_SAVEUI))return false;
		if(actionname.equals(CSteModel.ACTION_SETUPRULE))return false;
		if(actionname.equals(CSteModel.ACTION_SAVE))return false;
		if(actionname.equals(CSteModel.ACTION_SELECTOP))return false;
		return true;
	}

	
}
