package com.smart.bi.client.instance;

import com.smart.platform.gui.ste.*;
import com.smart.platform.util.DefaultNPParam;

import java.awt.*;

/*����"����ʵ������"Frame����*/
public class Instance_frame extends Steframe{
	public Instance_frame() throws HeadlessException {
		super("����ʵ������");
	}

	protected CSteModel getStemodel() {
		return new Instance_ste(this);
	}

	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.89";
		DefaultNPParam.debugdbpasswd = "database2";
		DefaultNPParam.debugdbsid = "pb";
		DefaultNPParam.debugdbusrname = "database2";
		DefaultNPParam.prodcontext = "npserver";

		
		Instance_frame w=new Instance_frame();
		w.pack();
		w.setVisible(true);
	}
}
