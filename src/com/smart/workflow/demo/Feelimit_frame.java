package com.smart.workflow.demo;

import com.smart.platform.gui.ste.*;
import com.smart.platform.util.DefaultNPParam;

import java.awt.*;

/*功能"费用限额(demo)"Frame窗口*/
public class Feelimit_frame extends Steframe{
	public Feelimit_frame() throws HeadlessException {
		super("费用限额(demo)");
	}

	protected CSteModel getStemodel() {
		return new Feelimit_ste(this);
	}

	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.47";
		DefaultNPParam.debugdbpasswd = "npserver";
		DefaultNPParam.debugdbsid = "orcl";
		DefaultNPParam.debugdbusrname = "npserver";
		DefaultNPParam.prodcontext = "npserver";

		Feelimit_frame w=new Feelimit_frame();
		w.pack();
		w.setVisible(true);
	}
}
