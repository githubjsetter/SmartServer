package com.smart.bi.client.tablecolumn;

import com.smart.platform.gui.ste.*;
import com.smart.platform.util.DefaultNPParam;

import java.awt.*;

/*����"�����ж���"Frame����*/
public class Tablecolumn_frame extends Steframe{
	public Tablecolumn_frame() throws HeadlessException {
		super("�����ж���");
	}

	protected CSteModel getStemodel() {
		return new Tablecolumn_ste(this);
	}

	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.47";
		DefaultNPParam.debugdbpasswd = "npserver";
		DefaultNPParam.debugdbsid = "orcl";
		DefaultNPParam.debugdbusrname = "npserver";
		DefaultNPParam.prodcontext = "npserver";

		Tablecolumn_frame w=new Tablecolumn_frame();
		w.pack();
		w.setVisible(true);
	}
}
