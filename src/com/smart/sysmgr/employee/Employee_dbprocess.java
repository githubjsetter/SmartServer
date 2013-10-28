package com.smart.sysmgr.employee;
import java.sql.Connection;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.server.process.SteProcessor;
/*功能"部门管理"应用服务器处理*/
public class Employee_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Employee_ste(null);
	}
	protected String getTablename() {
		return "pub_employee";
	}
	@Override
	public void on_beforesave(Connection con, Userruninfo userrininfo,
			DBTableModel dbmodel, int row) throws Exception {
		String opcode=dbmodel.getItemValue(row, "opcode");
		dbmodel.setItemValue(row, "pinyin", opcode);
		super.on_beforesave(con, userrininfo, dbmodel, row);
	}
	
	
}
