package com.smart.workflow.client;

import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.mde.MdeFrame;
import com.smart.platform.util.DefaultNPParam;

import java.awt.*;

/*����"����ʵ��"�ܵ�ϸĿFrame����*/
public class Wfinst_frame extends MdeFrame{
	public Wfinst_frame() throws HeadlessException {
		super("����ʵ��");
	}

	protected CMdeModel getMdeModel() {
		return new Wfinst_mde(this,"����ʵ��");
	}

	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.89";
		DefaultNPParam.debugdbpasswd = "database2";
		DefaultNPParam.debugdbsid = "pb";
		DefaultNPParam.debugdbusrname = "database2";
		DefaultNPParam.prodcontext = "npserver";

		
		Wfinst_frame w=new Wfinst_frame();
		w.pack();
		w.setVisible(true);
	}
}
