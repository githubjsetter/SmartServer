package com.smart.sysmgr.op;

import java.awt.HeadlessException;

import com.smart.extension.ste.CSteModelAp;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.util.DefaultNPParam;

/*����"���Ź���"����༭Model*/
public class Op_ste extends CSteModelAp{
	public Op_ste(CFrame frame) throws HeadlessException {
		super(frame, "����");
		//this.setTableeditable(true);
	}

	public String getTablename() {
		return "np_op";
	}

	public String getSaveCommandString() {
		return "com.inca.sysmgr.op.Op_ste.���湦�ܶ���";
	}
	
}
