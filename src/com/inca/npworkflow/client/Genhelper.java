package com.inca.npworkflow.client;

import com.inca.np.util.DefaultNPParam;
import com.inca.np.util.MdeGeneralTool;

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
