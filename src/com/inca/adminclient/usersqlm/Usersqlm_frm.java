package com.inca.adminclient.usersqlm;


import java.awt.HeadlessException;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.Steframe;
import com.inca.np.util.DefaultNPParam;

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
