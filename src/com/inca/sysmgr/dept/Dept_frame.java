package com.inca.sysmgr.dept;

import java.awt.HeadlessException;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.Steframe;
import com.inca.np.util.DefaultNPParam;

/*����"���Ź���"Frame����*/
public class Dept_frame extends Steframe{
	public Dept_frame() throws HeadlessException {
		super("���Ź���");
	}

	protected CSteModel getStemodel() {
		return new Dept_ste(this);
	}

	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.89";
		DefaultNPParam.debugdbpasswd = "database2";
		DefaultNPParam.debugdbsid = "pb";
		DefaultNPParam.debugdbusrname = "database2";
		DefaultNPParam.prodcontext = "npserver";

		Dept_frame w=new Dept_frame();
		w.pack();
		w.setVisible(true);
	}
}
