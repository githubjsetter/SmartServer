package com.smart.server.server.sysproc;

import java.sql.Connection;

import com.smart.client.updatelog.Updatereg_ste;
import com.smart.platform.auth.Userruninfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.server.UpdateLogger;
import com.smart.platform.server.process.SteProcessor;

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
