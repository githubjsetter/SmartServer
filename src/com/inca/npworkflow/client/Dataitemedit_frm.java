package com.inca.npworkflow.client;

import java.awt.HeadlessException;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.Steframe;
import com.inca.np.util.DefaultNPParam;

public class Dataitemedit_frm extends Steframe{

	public Dataitemedit_frm() throws HeadlessException {
		super("数据项定义");
		setDefaultCloseOperation(Steframe.DISPOSE_ON_CLOSE);
	}

	@Override
	protected CSteModel getStemodel() {
		return new Dataitemedit_ste(this,"数据项定义");
	}

	public static void main(String[] args) {
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.47";
		DefaultNPParam.debugdbpasswd = "npserver";
		DefaultNPParam.debugdbsid = "orcl";
		DefaultNPParam.debugdbusrname = "npserver";
		DefaultNPParam.prodcontext = "npserver";

		Dataitemedit_frm frm=new Dataitemedit_frm();
		frm.pack();
		frm.setVisible(true);
	}
}
