package com.inca.sysmgr.hov;

import java.sql.Connection;

import com.inca.np.auth.Userruninfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.server.process.SteProcessor;
import com.inca.np.util.DefaultNPParam;

/*功能"部门管理"应用服务器处理*/
public class Hov_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Hov_ste(null);
	}
	protected String getTablename() {
		return "np_hov";
	}
	@Override
	public void on_beforesave(Connection con, Userruninfo userrininfo,
			DBTableModel dbmodel, int row) throws Exception {
		// TODO Auto-generated method stub
		super.on_beforesave(con, userrininfo, dbmodel, row);
		dbmodel.setItemValue(row, "prodname",DefaultNPParam.prodcontext);
	}
	
	
	
}
