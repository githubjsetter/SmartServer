package com.inca.sysmgr.mac;

import com.inca.np.gui.ste.*;
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
