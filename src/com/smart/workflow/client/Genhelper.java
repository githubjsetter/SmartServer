package com.smart.workflow.client;

import com.smart.platform.util.DefaultNPParam;
import com.smart.platform.util.MdeGeneralTool;

public class Genhelper {
	public static void main(String[] args) {
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.89";
		DefaultNPParam.debugdbpasswd = "nbms";
		DefaultNPParam.debugdbsid = "pb";
		DefaultNPParam.debugdbusrname = "nbms";
		DefaultNPParam.prodcontext = "npserver";

		
		MdeGeneralTool mde=new MdeGeneralTool();
		mde.pack();
		mde.setVisible(true);
		
	}
}
