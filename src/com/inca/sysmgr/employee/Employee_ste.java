package com.inca.sysmgr.employee;

import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.npx.ste.CSteModelAp;

import java.awt.*;

/*功能"部门管理"单表编辑Model*/
public class Employee_ste extends CSteModelAp{
	public Employee_ste(CFrame frame) throws HeadlessException {
		super(frame, "人员");
		DBColumnDisplayInfo col=this.getDBColumnDisplayInfo("sex");
		col.addComboxBoxItem("1","男");
		col.addComboxBoxItem("2","女");
	}

	public String getTablename() {
		return "pub_employee_v";
	}

	public String getSaveCommandString() {
		return "com.inca.sysmgr.employee.Employee_ste.保存人员";
	}
}
