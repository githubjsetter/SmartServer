package com.smart.bi.client.ds;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.ste.CSteModel;

import java.awt.*;

/*����"����Դ���ӹ���"����༭Model*/
public class Ds_ste extends CSteModel{
	public Ds_ste(CFrame frame) throws HeadlessException {
		super(frame, "����Դ����");
	}

	public String getTablename() {
		return "npbi_ds";
	}

	public String getSaveCommandString() {
		return "Ds_ste.��������Դ����";
	}
}
