package com.inca.np.demo.box8;

import java.awt.HeadlessException;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.Steframe;
import com.inca.np.util.DefaultNPParam;

public class Box8_frm extends Steframe{

	public Box8_frm() throws HeadlessException {
		super("∞Àÿ‘œ‰ÃÂ");
	}

	@Override
	protected CSteModel getStemodel() {
		return new Box8_ste(this,"∞Àÿ‘œ‰ÃÂ");
	}

	public static void main(String[] args) {
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		
		Box8_frm frm=new Box8_frm();
		frm.pack();
		frm.setVisible(true);
	}
}
