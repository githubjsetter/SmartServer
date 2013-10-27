package com.inca.sysmgr.roleop;

import java.awt.HeadlessException;

import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.mde.MdeFrame;
import com.inca.np.util.DefaultNPParam;

/*功能"角色管理"总单细目Frame窗口*/
public class Roleop_frame extends MdeFrame{
	public Roleop_frame() throws HeadlessException {
		super("角色管理");
	}

	protected CMdeModel getMdeModel() {
		return new Roleop_mde(this,"角色管理");
	}

	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.89";
		DefaultNPParam.debugdbpasswd = "database2";
		DefaultNPParam.debugdbsid = "pb";
		DefaultNPParam.debugdbusrname = "database2";
		DefaultNPParam.prodcontext = "npserver";
		
		
		Roleop_frame w=new Roleop_frame();
		w.setOpid("4");
		w.pack();
		w.setVisible(true);
	}
}
