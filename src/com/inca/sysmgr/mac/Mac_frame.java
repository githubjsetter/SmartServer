package com.inca.sysmgr.mac;

import com.inca.np.gui.ste.*;
import java.awt.*;

/*功能"入网请求审批"Frame窗口*/
public class Mac_frame extends Steframe{
	public Mac_frame() throws HeadlessException {
		super("入网请求");
	}

	protected CSteModel getStemodel() {
		return new Mac_ste(this);
	}

	
}
