package com.smart.workflow.client;

import com.smart.platform.gui.ste.*;
import com.smart.platform.util.DefaultNPParam;

import java.awt.*;

/*功能"流程状态管理"Frame窗口*/
public class Approvestatus_frame extends Steframe{
	public Approvestatus_frame() throws HeadlessException {
		super("流程状态管理");
	}

	protected CSteModel getStemodel() {
		return new Approvestatus_ste(this);
	}

	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.47";
		DefaultNPParam.debugdbpasswd = "npserver";
		DefaultNPParam.debugdbsid = "orcl";
		DefaultNPParam.debugdbusrname = "npserver";
		DefaultNPParam.prodcontext = "npserver";

		Approvestatus_frame w=new Approvestatus_frame();
		w.pack();
		w.setVisible(true);
	}
}
