package com.smart.workflow.demo;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.ste.CSteModel;

import java.awt.*;

/*����"�����޶�(demo)"����༭Model*/
public class Feelimit_ste extends CSteModel{
	public Feelimit_ste(CFrame frame) throws HeadlessException {
		super(frame, "�����޶�");
	}

	public String getTablename() {
		return "np_wf_demo_fee_limit";
	}

	public String getSaveCommandString() {
		return "com.inca.npworkflow.demo.Feelimit_ste.��������޶�";
	}
}
