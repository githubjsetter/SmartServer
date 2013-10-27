package com.inca.npx.ste;

import java.awt.HeadlessException;
import java.io.File;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.util.DefaultNPParam;

public class ReportframeGeneral extends SteframeGeneral{

	public ReportframeGeneral(File zxfile) throws HeadlessException {
		super(zxfile);
	}

	@Override
	protected CSteModel getStemodel() {
		CReportmodelGeneral ste=new CReportmodelGeneral(this,opname,opid,viewname,zxzipfile);
		return ste;
	}
	
	public static void main(String[] args) {
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.1";
		DefaultNPParam.debugdbpasswd = "xjxty";
		DefaultNPParam.debugdbsid = "data";
		DefaultNPParam.debugdbusrname = "xjxty";
		DefaultNPParam.prodcontext = "npserver";
		
		File zxfile=new File("d:/npserver/build/classes/专项开发/10010.zip");
		
		ReportframeGeneral steframe=new ReportframeGeneral(zxfile);
		steframe.pack();
		steframe.setVisible(true);
	}	

}
