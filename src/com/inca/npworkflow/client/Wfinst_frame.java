package com.inca.npworkflow.client;

import com.inca.np.gui.mde.MdeFrame;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.util.DefaultNPParam;

import java.awt.*;

/*功能"流程实例"总单细目Frame窗口*/
public class Wfinst_frame extends MdeFrame{
	public Wfinst_frame() throws HeadlessException {
		super("流程实例");
	}

	protected CMdeModel getMdeModel() {
		return new Wfinst_mde(this,"流程实例");
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
