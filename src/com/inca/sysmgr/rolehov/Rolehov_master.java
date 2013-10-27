package com.inca.sysmgr.rolehov;

import java.awt.HeadlessException;

import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.CStetoolbar;
import com.inca.np.gui.mde.CMasterModel;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.sysmgr.roleemployee.Roleemp_toolbar;

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
