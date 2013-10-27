package com.inca.sysmgr.roleemployee;

import java.awt.HeadlessException;

import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.mde.MdeFrame;
import com.inca.np.util.DefaultNPParam;

/*功能"角色管理"总单细目Frame窗口*/
public class Roleemployee_frame extends MdeFrame{
	public Roleemployee_frame() throws HeadlessException {
		super("人员角色授权");
	}

	protected CMdeModel getMdeModel() {
		return new Roleemployee_mde(this,"人员角色授权");
	}

	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		Roleemployee_frame w=new Roleemployee_frame();
		w.pack();
		w.setVisible(true);
	}
}
