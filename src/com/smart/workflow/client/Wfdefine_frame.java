package com.smart.workflow.client;

import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.mde.MdeFrame;
import com.smart.platform.util.DefaultNPParam;

import java.awt.*;

/*����"���̶���"�ܵ�ϸĿFrame����*/
public class Wfdefine_frame extends MdeFrame{
	public Wfdefine_frame() throws HeadlessException {
		super("���̶���");
	}

	protected CMdeModel getMdeModel() {
		return new Wfdefine_mde(this,"���̶���");
	}

	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.89";
		DefaultNPParam.debugdbpasswd = "database2";
		DefaultNPParam.debugdbsid = "pb";
		DefaultNPParam.debugdbusrname = "database2";
		DefaultNPParam.prodcontext = "npserver";

		
		Wfdefine_frame w=new Wfdefine_frame();
		w.pack();
		w.setVisible(true);
	}
}
