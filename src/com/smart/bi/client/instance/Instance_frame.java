package com.smart.bi.client.instance;

import com.smart.platform.gui.ste.*;
import com.smart.platform.util.DefaultNPParam;

import java.awt.*;

/*功能"报表实例管理"Frame窗口*/
public class Instance_frame extends Steframe{
	public Instance_frame() throws HeadlessException {
		super("报表实例管理");
	}

	protected CSteModel getStemodel() {
		return new Instance_ste(this);
	}

	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.89";
		DefaultNPParam.debugdbpasswd = "database2";
		DefaultNPParam.debugdbsid = "pb";
		DefaultNPParam.debugdbusrname = "database2";
		DefaultNPParam.prodcontext = "npserver";

		
		Instance_frame w=new Instance_frame();
		w.pack();
		w.setVisible(true);
	}
}
