package com.smart.server.pushplat.client;

import java.awt.HeadlessException;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.mde.CMasterModel;
import com.smart.platform.gui.mde.CMdeModel;

public class Rolepush_master extends CMasterModel{

	public Rolepush_master(CFrame frame, String title, CMdeModel mdemodel)
			throws HeadlessException {
		super(frame, title, mdemodel);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getTablename() {
		return "np_role";
	}

	@Override
	public String getSaveCommandString() {
		return "";
	}

}
