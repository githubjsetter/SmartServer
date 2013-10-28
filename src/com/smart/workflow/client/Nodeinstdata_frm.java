package com.smart.workflow.client;


import java.awt.HeadlessException;

import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.Steframe;
import com.smart.platform.util.DefaultNPParam;
import com.smart.workflow.client.Nodeinstdata_ste;

public class Nodeinstdata_frm extends Steframe{

	public Nodeinstdata_frm() throws HeadlessException {
		super("结点决策数据");
	}

	@Override
	protected CSteModel getStemodel() {
		return new Nodeinstdata_ste(this,"结点决策数据");
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
