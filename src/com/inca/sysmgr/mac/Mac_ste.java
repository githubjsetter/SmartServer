package com.inca.sysmgr.mac;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.control.CFrame;

import java.awt.*;

/*����"������������"����༭Model*/
public class Mac_ste extends CSteModel{
	public Mac_ste(CFrame frame) throws HeadlessException {
		super(frame, "��������");
	}

	public String getTablename() {
		return "np_mac";
	}

	public String getSaveCommandString() {
		return "com.inca.sysmgr.macreq.Mac_ste.����MAC";
	}
}
