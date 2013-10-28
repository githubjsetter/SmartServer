package com.smart.platform.demo.box8;

import java.awt.HeadlessException;

import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.Steframe;
import com.smart.platform.util.DefaultNPParam;

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
