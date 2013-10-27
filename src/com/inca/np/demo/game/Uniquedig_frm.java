package com.inca.np.demo.game;

import java.awt.HeadlessException;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.Steframe;
import com.inca.np.util.DefaultNPParam;

public class Uniquedig_frm extends Steframe{

	public Uniquedig_frm() throws HeadlessException {
		super();
		setDefaultCloseOperation(Steframe.DISPOSE_ON_CLOSE);
	}

	@Override
	protected CSteModel getStemodel() {
		return new Uniquedig_ste(this,"Êý¶À");
	}

	public static void main(String[] args) {
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		
		Uniquedig_frm frm=new Uniquedig_frm();
		frm.pack();
		frm.setVisible(true);
	}
}
