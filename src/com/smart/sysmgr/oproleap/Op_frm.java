package com.smart.sysmgr.oproleap;

import java.awt.HeadlessException;

import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.Steframe;
import com.smart.platform.util.DefaultNPParam;

public class Op_frm extends Steframe{

	public Op_frm() throws HeadlessException {
		super("功能");
	}

	@Override
	protected CSteModel getStemodel() {
		return new Op_ste(this,"功能定义");
	}
	
	
	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;

		DefaultNPParam.debugdbip = "192.9.200.47";
		DefaultNPParam.debugdbpasswd = "npserver";
		DefaultNPParam.debugdbsid = "orcl";
		DefaultNPParam.debugdbusrname = "npserver";
		DefaultNPParam.prodcontext = "npserver";
		
		Op_frm frm=new Op_frm();
		frm.pack();
		frm.setVisible(true);
		
	}

}
