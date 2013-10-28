package com.smart.platform.demo.mmde;

import java.awt.HeadlessException;

import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.Steframe;
import com.smart.platform.util.DefaultNPParam;

public class Goodsvariety_frame extends Steframe{

	public Goodsvariety_frame() throws HeadlessException {
		super("品种管理");
	}

	@Override
	protected CSteModel getStemodel() {
		// TODO Auto-generated method stub
		return new Goodsvariety_ste(this,"品种管理");
	}
	
	public static void main(String[] args) {
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.89";
		DefaultNPParam.debugdbpasswd = "nbms";
		DefaultNPParam.debugdbsid = "pb";
		DefaultNPParam.debugdbusrname = "nbms";
		DefaultNPParam.prodcontext = "npserver";
		
		Goodsvariety_frame frm=new Goodsvariety_frame();
		frm.pack();
		frm.setVisible(true);
	}

}
