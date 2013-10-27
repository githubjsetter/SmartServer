package com.inca.sysmgr.op;

import java.awt.HeadlessException;

import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.util.DefaultNPParam;
import com.inca.npx.ste.CSteModelAp;

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
