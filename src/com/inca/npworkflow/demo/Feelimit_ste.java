package com.inca.npworkflow.demo;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBColumnDisplayInfo;
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
