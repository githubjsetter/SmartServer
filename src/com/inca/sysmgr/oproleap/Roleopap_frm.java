package com.inca.sysmgr.oproleap;

import java.awt.HeadlessException;

import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.mde.MdeFrame;
import com.inca.np.util.DefaultNPParam;

public class Roleopap_frm extends MdeFrame{

	public Roleopap_frm() throws HeadlessException {
		super("��ɫ������Ȩ");
	}

	@Override
	protected CMdeModel getMdeModel() {
		return new Roleopap_mde(this,"��ɫ������Ȩ");
	}

	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;

		DefaultNPParam.debugdbip = "192.9.200.47";
		DefaultNPParam.debugdbpasswd = "npserver";
		DefaultNPParam.debugdbsid = "orcl";
		DefaultNPParam.debugdbusrname = "npserver";
		DefaultNPParam.prodcontext = "npserver";
		
		
		Roleopap_frm w=new Roleopap_frm();
		w.pack();
		w.setVisible(true);
		
	}
}
