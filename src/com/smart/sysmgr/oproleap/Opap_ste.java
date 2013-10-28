package com.smart.sysmgr.oproleap;

import java.awt.HeadlessException;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMdeModel;

public class Opap_ste extends CDetailModel{

	public Opap_ste(CFrame frame, String title, CMdeModel mdemodel)
			throws HeadlessException {
		super(frame, title, mdemodel);
	}
	
	@Override
	public String getTablename() {
		return "np_op_ap";
	}


}
