package com.smart.workflow.server;

import java.sql.Connection;
import java.util.Vector;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ResultCommand;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.server.process.MdeProcessor;
import com.smart.workflow.client.Wfdefine_mde;

public class Wfdefine_dbprocess  extends MdeProcessor{
	protected CMdeModel getMdeModel() {
		return new Wfdefine_mde(null,"");
	}
	protected String getMastertablename() {
		return "np_wf_define";
	}
	protected String getDetailtablename() {
		return "np_wf_node";
	}

	@Override
	public void on_aftersavemaster(Connection con, Userruninfo userruninfo,
			DBTableModel masterdbmodel, int masterrow,
			DBTableModel detaildbmodel) throws Exception {
		super.on_aftersavemaster(con, userruninfo, masterdbmodel, masterrow,
				detaildbmodel);
		String wfid=masterdbmodel.getItemValue(masterrow, "Wfid");
		WfEngine.reloadWfdefine(wfid);
	}
	
	
	
}
