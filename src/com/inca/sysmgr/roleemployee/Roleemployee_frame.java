package com.inca.sysmgr.roleemployee;

import java.awt.HeadlessException;

import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.mde.MdeFrame;
import com.inca.np.util.DefaultNPParam;

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
