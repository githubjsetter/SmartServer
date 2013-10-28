package com.smart.bi.client.ds;

import com.smart.platform.gui.ste.*;
import com.smart.platform.util.DefaultNPParam;

import java.awt.*;

/*功能"数据源连接管理"Frame窗口*/
public class Ds_frame extends Steframe{
	public Ds_frame() throws HeadlessException {
		super("数据源连接管理");
	}

	protected CSteModel getStemodel() {
		return new Ds_ste(this);
	}

	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.47";
		DefaultNPParam.debugdbpasswd = "npserver";
		DefaultNPParam.debugdbsid = "orcl";
		DefaultNPParam.debugdbusrname = "npserver";
		DefaultNPParam.prodcontext = "npserver";

		
		Ds_frame w=new Ds_frame();
		w.pack();
		w.setVisible(true);
	}
}
