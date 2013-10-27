package com.inca.npserver.pushplat.client;

import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.mde.CDetailModel;
import com.inca.np.gui.mde.CMasterModel;
import com.inca.np.gui.mde.CMdeModel;

public class Rolepush_mde extends CMdeModel{

	public Rolepush_mde(CFrame frame, String title) {
		super(frame, title);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected CDetailModel createDetailmodel() {
		return new Rolepush_detail(getParentFrame(),"����",this);
	}

	@Override
	protected CMasterModel createMastermodel() {
		return new Rolepush_master(getParentFrame(),"��ɫ",this);
	}

	@Override
	public String getDetailRelatecolname() {
		return "roleid";
	}

	@Override
	public String getMasterRelatecolname() {
		return "roleid";
	}

	@Override
	public String getSaveCommandString() {
		return "npserver:�����ɫ����";
	}

}
