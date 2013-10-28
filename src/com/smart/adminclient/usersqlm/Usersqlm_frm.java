package com.smart.adminclient.usersqlm;


import java.awt.HeadlessException;

import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.Steframe;
import com.smart.platform.util.DefaultNPParam;

public class Usersqlm_frm extends Steframe{
	

	public Usersqlm_frm() throws HeadlessException {
		super("用户sql监控");
		
	}

	@Override
	protected CSteModel getStemodel() {
		return new Usersqlm_ste(this,"用户sql");
	}
	
	public static void main(String[] args) {
		DefaultNPParam.develop = 1;
		DefaultNPParam.debug = 1;
		Usersqlm_frm frm = new Usersqlm_frm();
		frm.pack();
		frm.setVisible(true);
		
	}

}
