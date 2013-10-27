package com.inca.np.demo.ste;

import java.awt.HeadlessException;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.Steframe;
import com.inca.np.util.DefaultNPParam;

/*功能"附件测试"Frame窗口*/
public class Testfile_frame extends Steframe{
	public Testfile_frame() throws HeadlessException {
		super("附件测试");
	}

	protected CSteModel getStemodel() {
		return new Testfile_ste(this);
	}

	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		Testfile_frame w=new Testfile_frame();
		w.pack();
		w.setVisible(true);
	}
}
