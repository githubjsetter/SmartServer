package com.inca.sysmgr.dept;

import com.inca.np.gui.control.CFrame;
import com.inca.npx.ste.CSteModelAp;

import java.awt.*;

/*功能"部门管理"单表编辑Model*/
public class Dept_ste extends CSteModelAp{
	public Dept_ste(CFrame frame) throws HeadlessException {
		super(frame, "部门");
	}

	public String getTablename() {
		return "pub_company_view";
	}

	public String getSaveCommandString() {
		return "com.inca.sysmgr.dept.Dept_ste.保存部门";
	}
}
