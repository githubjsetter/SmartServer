package com.inca.sysmgr.roleop;

import java.awt.HeadlessException;

import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.mde.MdeFrame;
import com.inca.np.util.DefaultNPParam;

/*����"��ɫ����"�ܵ�ϸĿFrame����*/
public class Roleop_frame extends MdeFrame{
	public Roleop_frame() throws HeadlessException {
		super("��ɫ����");
	}

	protected CMdeModel getMdeModel() {
		return new Roleop_mde(this,"��ɫ����");
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
