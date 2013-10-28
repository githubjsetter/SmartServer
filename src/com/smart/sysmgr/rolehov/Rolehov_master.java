package com.smart.sysmgr.rolehov;

import java.awt.HeadlessException;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CStetoolbar;
import com.smart.platform.gui.mde.CMasterModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.sysmgr.roleemployee.Roleemp_toolbar;

public class Rolehov_master  extends CMasterModel{
	public Rolehov_master(CFrame frame, CMdeModel mdemodel) throws HeadlessException {
		super(frame, "½ÇÉ«", mdemodel);
	}

	public String getTablename() {
		return "np_role";
	}

	public String getSaveCommandString() {
		return null;
	}
	
	
}
