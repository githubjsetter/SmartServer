package com.inca.npserver.server.sysproc;

import java.sql.Connection;

import com.inca.np.auth.Userruninfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.server.UpdateLogger;
import com.inca.np.server.process.SteProcessor;
import com.inca.npclient.updatelog.Updatereg_ste;

public class Updatereg_dbprocessor extends SteProcessor{

	@Override
	protected CSteModel getSteModel() {
		return new Updatereg_ste(null,"");
	}

	@Override
	protected String getTablename() {
		return "np_update_reg";
	}

	@Override
	public void on_aftersave(Connection con, Userruninfo userrininfo,
			DBTableModel saveddbmodel, int row) throws Exception {
		// TODO Auto-generated method stub
		super.on_aftersave(con, userrininfo, saveddbmodel, row);
		UpdateLogger.getInstance().reset();
	}

}
