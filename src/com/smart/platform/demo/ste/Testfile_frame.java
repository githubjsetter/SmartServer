package com.smart.platform.demo.ste;

import java.awt.HeadlessException;

import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.Steframe;
import com.smart.platform.util.DefaultNPParam;

/*����"��������"Frame����*/
public class Testfile_frame extends Steframe{
	public Testfile_frame() throws HeadlessException {
		super("��������");
	}

	protected CSteModel getStemodel() {
		return new Testfile_ste(this);
	}

	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		Testfile_frame w=new Testfile_frame();
		w.pack();
		w.setVisible(true);
	}
}
