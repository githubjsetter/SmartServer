package com.inca.np.demo.ste;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import java.awt.*;

/*����"��������"����༭Model*/
public class Testfile_ste extends CSteModel{
	public Testfile_ste(CFrame frame) throws HeadlessException {
		super(frame, "��������");
	}

	public String getTablename() {
		return "testfile";
	}

	public String getSaveCommandString() {
		return "�������Ա���";
	}
}
