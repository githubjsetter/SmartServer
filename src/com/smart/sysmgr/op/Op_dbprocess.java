package com.smart.sysmgr.op;
import java.sql.Connection;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.server.process.SteProcessor;
import com.smart.platform.util.DefaultNPParam;
/*功能"部门管理"应用服务器处理*/
public class Op_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Op_ste(null);
	}
	protected String getTablename() {
		return "np_op";
	}
	@Override
	public void on_beforesave(Connection con, Userruninfo userrininfo,
			DBTableModel dbmodel, int row) throws Exception {
		// TODO Auto-generated method stub
		super.on_beforesave(con, userrininfo, dbmodel, row);
		dbmodel.setItemValue(row, "prodname",DefaultNPParam.prodcontext);
	}
	
}
