package com.inca.sysmgr.hov;

import java.awt.HeadlessException;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.Steframe;
import com.inca.np.util.DefaultNPParam;

/*功能"部门管理"Frame窗口*/
public class Hov_frame extends Steframe{
	public Hov_frame() throws HeadlessException {
		super("HOV管理");
	}

	protected CSteModel getStemodel() {
		return new Hov_ste(this);
	}

	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		Hov_frame w=new Hov_frame();
		w.pack();
		w.setVisible(true);
	}
}
