package com.inca.sysmgr.oproleap;

import java.awt.HeadlessException;

import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.mde.CDetailModel;
import com.inca.np.gui.mde.CMdeModel;

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
