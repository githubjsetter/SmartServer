package com.inca.npworkflow.client;


import java.awt.HeadlessException;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.Steframe;
import com.inca.np.util.DefaultNPParam;
import com.inca.npworkflow.client.Nodeinstdata_ste;

public class Nodeinstdata_frm extends Steframe{

	public Nodeinstdata_frm() throws HeadlessException {
		super("����������");
	}

	@Override
	protected CSteModel getStemodel() {
		return new Nodeinstdata_ste(this,"����������");
	}

	public static void main(String[] args) {
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.89";
		DefaultNPParam.debugdbpasswd = "nbms";
		DefaultNPParam.debugdbsid = "pb";
		DefaultNPParam.debugdbusrname = "nbms";
		DefaultNPParam.prodcontext = "npserver";

		Nodeinstdata_frm frm=new Nodeinstdata_frm();
		frm.pack();
		frm.setVisible(true);
	}
	
}
