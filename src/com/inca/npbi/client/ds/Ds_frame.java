package com.inca.npbi.client.ds;

import com.inca.np.gui.ste.*;
import com.inca.np.util.DefaultNPParam;

import java.awt.*;

/*����"����Դ���ӹ���"Frame����*/
public class Ds_frame extends Steframe{
	public Ds_frame() throws HeadlessException {
		super("����Դ���ӹ���");
	}

	protected CSteModel getStemodel() {
		return new Ds_ste(this);
	}

	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.47";
		DefaultNPParam.debugdbpasswd = "npserver";
		DefaultNPParam.debugdbsid = "orcl";
		DefaultNPParam.debugdbusrname = "npserver";
		DefaultNPParam.prodcontext = "npserver";

		
		Ds_frame w=new Ds_frame();
		w.pack();
		w.setVisible(true);
	}
}
