package com.inca.npserver.pushplat.client;

import java.awt.HeadlessException;

import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.mde.CMasterModel;
import com.inca.np.gui.mde.CMdeModel;

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
