package com.smart.platform.demo.mste;

import com.smart.platform.gui.ste.*;
import com.smart.platform.util.DefaultNPParam;

import java.awt.*;

/*功能"厂家管理"Frame窗口*/
public class Pubfactory_frame extends Steframe{
	public Pubfactory_frame() throws HeadlessException {
		super("厂家管理");
	}

	protected CSteModel getStemodel() {
		return new Pubfactory_ste(this);
	}

	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.1";
		DefaultNPParam.debugdbpasswd = "xjxty";
		DefaultNPParam.debugdbsid = "data";
		DefaultNPParam.debugdbusrname = "xjxty";
		DefaultNPParam.prodcontext = "npserver";
		

		Pubfactory_frame w=new Pubfactory_frame();
		w.pack();
		w.setVisible(true);
	}
}
