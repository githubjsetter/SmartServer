package com.smart.sysmgr.emproleop;

import java.awt.HeadlessException;

import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.Steframe;
import com.smart.platform.util.DefaultNPParam;

public class Employeerole_frm  extends Steframe{

	public Employeerole_frm() throws HeadlessException {
		super("��Ա��ɫ");
	}

	@Override
	protected CSteModel getStemodel() {
		return new Employeerole_ste(this,"��Ա��ɫ");
	}
	
	
	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;

		DefaultNPParam.debugdbip = "192.9.200.47";
		DefaultNPParam.debugdbpasswd = "npserver";
		DefaultNPParam.debugdbsid = "orcl";
		DefaultNPParam.debugdbusrname = "npserver";
		DefaultNPParam.prodcontext = "npserver";
		
		Employeerole_frm frm=new Employeerole_frm();
		frm.pack();
		frm.setVisible(true);
		
	}

}
