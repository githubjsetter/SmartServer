package com.inca.sysmgr.rolehov;

import java.awt.HeadlessException;

import com.inca.np.communicate.RecordTrunk;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.CTable;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.design.Selecthovmhov;
import com.inca.np.gui.mde.CDetailModel;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.util.DBHelper;

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
