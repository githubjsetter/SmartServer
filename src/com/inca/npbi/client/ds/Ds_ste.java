package com.inca.npbi.client.ds;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBColumnDisplayInfo;
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
