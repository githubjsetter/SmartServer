package com.inca.npworkflow.demo;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBColumnDisplayInfo;
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
