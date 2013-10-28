package com.smart.sysmgr.rolehov;

import java.awt.HeadlessException;

import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.mde.MdeFrame;
import com.smart.platform.util.DefaultNPParam;

/**
 * 角色
 * @author user
 *
 */
public class Rolehov_frame   extends MdeFrame{
	public Rolehov_frame() throws HeadlessException {
		super("角色HOV授权");
	}

	protected CMdeModel getMdeModel() {
		return new Rolehov_mde(this,"角色HOV授权");
	}

	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.47";
		DefaultNPParam.debugdbpasswd = "npserver";
		DefaultNPParam.debugdbsid = "orcl";
		DefaultNPParam.debugdbusrname = "npserver";
		DefaultNPParam.prodcontext = "npserver";

		Rolehov_frame w=new Rolehov_frame();
		w.pack();
		w.setVisible(true);
	}
}
