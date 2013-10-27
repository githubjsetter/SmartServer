package com.inca.npworkflow.demo;

import com.inca.np.auth.ClientUserManager;
import com.inca.np.gui.ste.*;
import com.inca.np.util.DefaultNPParam;

import java.awt.*;

/*功能"费用申请演示"Frame窗口*/
public class Feedemo_frame extends Steframe{
	public Feedemo_frame() throws HeadlessException {
		super("费用申请演示");
	}

	protected CSteModel getStemodel() {
		return new Feedemo_ste(this);
	}

	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.47";
		DefaultNPParam.debugdbpasswd = "npserver";
		DefaultNPParam.debugdbsid = "orcl";
		DefaultNPParam.debugdbusrname = "npserver";
		DefaultNPParam.prodcontext = "npserver";

		ClientUserManager.getCurrentUser().setUserid("0");
		
		Feedemo_frame w=new Feedemo_frame();
		w.pack();
		w.setVisible(true);
	}
}
