package com.smart.platform.demo.mste;

import com.smart.platform.util.DefaultNPParam;
import com.smart.platform.util.SteGeneralTool;

public class Genhelp {
	public static void main(String[] args) {
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.47";
		DefaultNPParam.debugdbpasswd = "npserver";
		DefaultNPParam.debugdbsid = "orcl";
		DefaultNPParam.debugdbusrname = "npserver";
		DefaultNPParam.prodcontext = "npserver";
		
		SteGeneralTool tool=new SteGeneralTool();
		tool.pack();
		tool.setVisible(true);
	}
}
