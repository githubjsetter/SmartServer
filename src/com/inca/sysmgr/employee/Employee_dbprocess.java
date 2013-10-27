package com.inca.sysmgr.employee;
import java.sql.Connection;

import com.inca.np.server.process.SteProcessor;
import com.inca.np.auth.Userruninfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.CSteModel;
/*����"���Ź���"Ӧ�÷���������*/
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
