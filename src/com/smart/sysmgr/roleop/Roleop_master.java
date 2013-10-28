package com.smart.sysmgr.roleop;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CStetoolbar;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.mde.CMasterModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.util.DefaultNPParam;

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
