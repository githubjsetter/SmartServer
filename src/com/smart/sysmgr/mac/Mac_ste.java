package com.smart.sysmgr.mac;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.ste.CSteModel;

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
