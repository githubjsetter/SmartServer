package com.smart.sysmgr.roleemployee;

import java.awt.HeadlessException;

import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.mde.MdeFrame;
import com.smart.platform.util.DefaultNPParam;

/*����"��ɫ����"�ܵ�ϸĿFrame����*/
public class Roleemployee_frame extends MdeFrame{
	public Roleemployee_frame() throws HeadlessException {
		super("��Ա��ɫ��Ȩ");
	}

	protected CMdeModel getMdeModel() {
		return new Roleemployee_mde(this,"��Ա��ɫ��Ȩ");
	}

	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		Roleemployee_frame w=new Roleemployee_frame();
		w.pack();
		w.setVisible(true);
	}
}
