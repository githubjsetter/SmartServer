package com.inca.sysmgr.rolehov;

import java.awt.HeadlessException;

import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.mde.MdeFrame;
import com.inca.np.util.DefaultNPParam;

/**
 * ��ɫ
 * @author user
 *
 */
public class Rolehov_frame   extends MdeFrame{
	public Rolehov_frame() throws HeadlessException {
		super("��ɫHOV��Ȩ");
	}

	protected CMdeModel getMdeModel() {
		return new Rolehov_mde(this,"��ɫHOV��Ȩ");
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
