package com.inca.sysmgr.emproleop;

import java.awt.HeadlessException;

import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.mde.MdeFrame;
import com.inca.np.util.DefaultNPParam;

public class Opap_frm extends MdeFrame{

	public Opap_frm() throws HeadlessException {
		super("功能授权");
	}

	@Override
	protected CMdeModel getMdeModel() {
		return new Opap_mde(this,"功能角色");
	}
		

	
	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;

		DefaultNPParam.debugdbip = "192.9.200.47";
		DefaultNPParam.debugdbpasswd = "npserver";
		DefaultNPParam.debugdbsid = "orcl";
		DefaultNPParam.debugdbusrname = "npserver";
		DefaultNPParam.prodcontext = "npserver";
		
		Opap_frm frm=new Opap_frm();
		frm.pack();
		frm.setVisible(true);
	}
	
}
