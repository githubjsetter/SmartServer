package com.inca.sysmgr.roleemployee;

import com.inca.np.gui.mde.CMasterModel;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.CStetoolbar;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.util.DefaultNPParam;

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
