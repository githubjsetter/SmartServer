package com.smart.workflow.demo;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.ste.CSteModel;

import java.awt.*;

/*功能"费用限额(demo)"单表编辑Model*/
public class Feelimit_ste extends CSteModel{
	public Feelimit_ste(CFrame frame) throws HeadlessException {
		super(frame, "费用限额");
	}

	public String getTablename() {
		return "np_wf_demo_fee_limit";
	}

	public String getSaveCommandString() {
		return "com.inca.npworkflow.demo.Feelimit_ste.保存费用限额";
	}
}
