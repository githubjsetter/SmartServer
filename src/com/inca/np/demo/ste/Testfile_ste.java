package com.inca.np.demo.ste;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import java.awt.*;

/*功能"附件测试"单表编辑Model*/
public class Testfile_ste extends CSteModel{
	public Testfile_ste(CFrame frame) throws HeadlessException {
		super(frame, "附件测试");
	}

	public String getTablename() {
		return "testfile";
	}

	public String getSaveCommandString() {
		return "附件测试保存";
	}
}
