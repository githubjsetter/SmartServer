package com.inca.npbi.client.view;

import com.inca.np.gui.ste.*;
import com.inca.np.util.DefaultNPParam;

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
