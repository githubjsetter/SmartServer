package com.smart.bi.client.report;

import com.smart.platform.gui.ste.*;
import com.smart.platform.util.DefaultNPParam;

import java.awt.*;

/*����"������"Frame����*/
public class Report_frame extends Steframe{
	public Report_frame() throws HeadlessException {
		super("������");
	}

	protected CSteModel getStemodel() {
		return new Report_ste(this);
	}

	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.47";
		DefaultNPParam.debugdbpasswd = "npserver";
		DefaultNPParam.debugdbsid = "orcl";
		DefaultNPParam.debugdbusrname = "npserver";
		DefaultNPParam.prodcontext = "npserver";

		
		Report_frame w=new Report_frame();
		w.pack();
		w.setVisible(true);
	}
}
