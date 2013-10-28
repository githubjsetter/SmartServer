package com.smart.sysmgr.op;

import java.awt.HeadlessException;

import com.smart.extension.ste.CSteModelAp;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.util.DefaultNPParam;

/*功能"部门管理"单表编辑Model*/
public class Op_ste extends CSteModelAp{
	public Op_ste(CFrame frame) throws HeadlessException {
		super(frame, "功能");
		//this.setTableeditable(true);
	}

	public String getTablename() {
		return "np_op";
	}

	public String getSaveCommandString() {
		return "com.inca.sysmgr.op.Op_ste.保存功能定义";
	}
	
}
