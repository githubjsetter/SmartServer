package com.smart.sysmgr.roleemployee;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CStetoolbar;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.mde.CMasterModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.util.DefaultNPParam;

import java.awt.*;

/*����"��ɫ����"�ܵ�Model*/
public class Roleemployee_master extends CMasterModel{
	public Roleemployee_master(CFrame frame, CMdeModel mdemodel) throws HeadlessException {
		super(frame, "��ɫ", mdemodel);
	}

	public String getTablename() {
		return "np_role";
	}

	public String getSaveCommandString() {
		return null;
	}

	@Override
	protected CStetoolbar createToolbar() {
		return new Roleemp_toolbar(mdemodel);
	}
	
	
}
