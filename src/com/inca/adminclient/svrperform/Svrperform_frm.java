package com.inca.adminclient.svrperform;

import java.awt.HeadlessException;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.Steframe;
import com.inca.np.util.DefaultNPParam;

public class Svrperform_frm extends Steframe{

	public Svrperform_frm() throws HeadlessException {
		super("服务器性能");
	}

	@Override
	protected CSteModel getStemodel() {
		return new Svrperform_ste(this,"服务器性能");
	}

	public static void main(String[] args) {
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		
		DefaultNPParam.debugdbip = "192.9.200.89";
		DefaultNPParam.debugdbpasswd = "database2";
		DefaultNPParam.debugdbsid = "pb";
		DefaultNPParam.debugdbusrname = "database2";
		DefaultNPParam.prodcontext = "npserver";

		
		Svrperform_frm frm=new Svrperform_frm();
		frm.pack();
		frm.setVisible(true);
	}
}
