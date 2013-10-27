package com.inca.sysmgr.roleop;

import com.inca.np.gui.mde.CMasterModel;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.CStetoolbar;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.util.DefaultNPParam;

import java.awt.*;

/*功能"角色管理"总单Model*/
public class Roleop_master extends CMasterModel{
	public Roleop_master(CFrame frame, CMdeModel mdemodel) throws HeadlessException {
		super(frame, "角色", mdemodel);
		this.setTableeditable(true);
	}

	public String getTablename() {
		return "np_role";
	}

	public String getSaveCommandString() {
		return null;
	}
	

	@Override
	protected CStetoolbar createToolbar() {
		// TODO Auto-generated method stub
		return new Roleop_toolbar(mdemodel);
	}
	
	
	
}
