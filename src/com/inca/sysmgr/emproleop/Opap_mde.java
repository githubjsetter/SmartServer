package com.inca.sysmgr.emproleop;

import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.mde.CDetailModel;
import com.inca.np.gui.mde.CMasterModel;
import com.inca.np.gui.mde.CMdeModel;

public class Opap_mde extends CMdeModel{

	public Opap_mde(CFrame frame, String title) {
		super(frame, title);
	}

	@Override
	protected CDetailModel createDetailmodel() {
		return new Opap_ste(getParentFrame(),"授权",this);
	}

	@Override
	protected CMasterModel createMastermodel() {
		return new Op_ste(getParentFrame(),"功能",this);
	}

	@Override
	public String getDetailRelatecolname() {
		return "roleopid";
	}

	@Override
	public String getMasterRelatecolname() {
		return "roleopid";
	}

	@Override
	public String getSaveCommandString() {
		return "emproleop.保存角色功能授权属性";
	}

	
	
}
