package com.inca.sysmgr.roleopfast;

import com.inca.np.gui.mde.MdeFrame;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.util.DefaultNPParam;

import java.awt.*;

/*功能"角色功能授权定义"总单细目Frame窗口*/
public class Roleopap_frame extends MdeFrame{
	public Roleopap_frame() throws HeadlessException {
		super("角色功能授权定义");
	}

	protected CMdeModel getMdeModel() {
		return new Roleopap_mde(this,"角色功能授权定义");
	}

	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;

		DefaultNPParam.debugdbip = "192.9.200.47";
		DefaultNPParam.debugdbpasswd = "npserver";
		DefaultNPParam.debugdbsid = "orcl";
		DefaultNPParam.debugdbusrname = "npserver";
		DefaultNPParam.prodcontext = "npserver";

		Roleopap_frame w=new Roleopap_frame();
		w.pack();
		w.setVisible(true);
	}
}
