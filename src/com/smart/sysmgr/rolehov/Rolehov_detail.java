package com.smart.sysmgr.rolehov;

import java.awt.HeadlessException;

import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CTable;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.design.Selecthovmhov;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.util.DBHelper;

public class Rolehov_detail extends CDetailModel {

	public Rolehov_detail(CFrame frame, CMdeModel mdemodel)
			throws HeadlessException {
		super(frame, "HOV", mdemodel);
		
	}

	public String getTablename() {
		return "np_hov_ap_v";
	}

	public String getSaveCommandString() {
		return null;
	}
/*
	@Override
	public void on_doubleclick(int row, int col) {
		String hovid=getItemValue(row, "hovid");
		String classname=getItemValue(row, "classname");
		((Rolehov_mde)mdemodel).setHovap(hovid,classname);
	}
*/
	@Override
	public void doNew() {
		((Rolehov_mde)mdemodel).addHov();
	}
}
