package com.smart.bi.client.view;

import com.smart.platform.gui.ste.*;
import com.smart.platform.util.DefaultNPParam;

import java.awt.*;

/*����"��ͼ����"Frame����*/
public class View_frame extends Steframe{
	public View_frame() throws HeadlessException {
		super("��ͼ����");
	}

	protected CSteModel getStemodel() {
		return new View_ste(this);
	}

	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.47";
		DefaultNPParam.debugdbpasswd = "npserver";
		DefaultNPParam.debugdbsid = "orcl";
		DefaultNPParam.debugdbusrname = "npserver";
		DefaultNPParam.prodcontext = "npserver";
		
		View_frame w=new View_frame();
		w.pack();
		w.setVisible(true);
	}
}
