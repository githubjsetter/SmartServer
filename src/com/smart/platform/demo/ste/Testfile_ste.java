package com.smart.platform.demo.ste;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.ste.CSteModel;

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
