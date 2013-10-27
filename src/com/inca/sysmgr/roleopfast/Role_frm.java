package com.inca.sysmgr.roleopfast;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.Steframe;
import com.inca.np.util.DefaultNPParam;
import com.inca.np.util.MdeGeneralTool;

public class Role_frm extends Steframe{

	@Override
	protected CSteModel getStemodel() {
		return new Role_ste(this,"½ÇÉ«");
	}
	
	public static void main(String[] args) {
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;

		DefaultNPParam.debugdbip = "192.9.200.47";
		DefaultNPParam.debugdbpasswd = "npserver";
		DefaultNPParam.debugdbsid = "orcl";
		DefaultNPParam.debugdbusrname = "npserver";
		DefaultNPParam.prodcontext = "npserver";

//		MdeGeneralTool t=new MdeGeneralTool();
//		t.pack();
//		t.setVisible(true);
//		
//		if(true)return;
		
		Role_frm frm=new Role_frm();
		frm.pack();
		frm.setVisible(true);

	}

}
