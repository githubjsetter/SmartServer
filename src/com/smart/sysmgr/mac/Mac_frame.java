package com.smart.sysmgr.mac;

import com.smart.platform.gui.ste.*;

import java.awt.*;

/*����"������������"Frame����*/
public class Mac_frame extends Steframe{
	public Mac_frame() throws HeadlessException {
		super("��������");
	}

	protected CSteModel getStemodel() {
		return new Mac_ste(this);
	}

	
}
