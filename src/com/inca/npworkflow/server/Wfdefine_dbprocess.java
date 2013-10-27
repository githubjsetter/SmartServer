package com.inca.npworkflow.server;

import java.sql.Connection;
import java.util.Vector;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ResultCommand;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.server.process.MdeProcessor;
import com.inca.npworkflow.client.Wfdefine_mde;

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
