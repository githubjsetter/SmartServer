package com.inca.sysmgr.employee;

import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.npx.ste.CSteModelAp;

import java.awt.*;

/*����"���Ź���"����༭Model*/
public class Employee_ste extends CSteModelAp{
	public Employee_ste(CFrame frame) throws HeadlessException {
		super(frame, "��Ա");
		DBColumnDisplayInfo col=this.getDBColumnDisplayInfo("sex");
		col.addComboxBoxItem("1","��");
		col.addComboxBoxItem("2","Ů");
	}

	public String getTablename() {
		return "pub_employee_v";
	}

	public String getSaveCommandString() {
		return "com.inca.sysmgr.employee.Employee_ste.������Ա";
	}
}
