package com.smart.sysmgr.dept;

import com.smart.extension.ste.CSteModelAp;
import com.smart.platform.gui.control.CFrame;

import java.awt.*;

/*����"���Ź���"����༭Model*/
public class Dept_ste extends CSteModelAp{
	public Dept_ste(CFrame frame) throws HeadlessException {
		super(frame, "����");
	}

	public String getTablename() {
		return "pub_company_view";
	}

	public String getSaveCommandString() {
		return "com.inca.sysmgr.dept.Dept_ste.���沿��";
	}
}
