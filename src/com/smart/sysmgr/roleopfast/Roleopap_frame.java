package com.smart.sysmgr.roleopfast;

import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.mde.MdeFrame;
import com.smart.platform.util.DefaultNPParam;

import java.awt.*;

/*����"��ɫ������Ȩ����"�ܵ�ϸĿFrame����*/
public class Roleopap_frame extends MdeFrame{
	public Roleopap_frame() throws HeadlessException {
		super("��ɫ������Ȩ����");
	}

	protected CMdeModel getMdeModel() {
		return new Roleopap_mde(this,"��ɫ������Ȩ����");
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
