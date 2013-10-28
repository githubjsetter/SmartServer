package com.smart.server.pushplat.client;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMasterModel;
import com.smart.platform.gui.mde.CMdeModel;

public class Rolepush_mde extends CMdeModel{

	public Rolepush_mde(CFrame frame, String title) {
		super(frame, title);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected CDetailModel createDetailmodel() {
		return new Rolepush_detail(getParentFrame(),"推送",this);
	}

	@Override
	protected CMasterModel createMastermodel() {
		return new Rolepush_master(getParentFrame(),"角色",this);
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
		return "npserver:保存角色推送";
	}

}
