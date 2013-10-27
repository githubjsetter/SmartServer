package com.inca.npworkflow.client;

import java.awt.HeadlessException;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.Steframe;
import com.inca.np.util.DefaultNPParam;

public class Nodedata_frm extends Steframe{

	public Nodedata_frm() throws HeadlessException {
		super("决策数据");
	}

	@Override
	protected CSteModel getStemodel() {
		return new Nodedata_ste(this,"决策依据",null);
	}
	
	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.89";
		DefaultNPParam.debugdbpasswd = "nbms";
		DefaultNPParam.debugdbsid = "pb";
		DefaultNPParam.debugdbusrname = "nbms";
		DefaultNPParam.prodcontext = "npserver";

		
		Nodedata_frm w=new Nodedata_frm();
		w.pack();
		w.setVisible(true);
	}


}
