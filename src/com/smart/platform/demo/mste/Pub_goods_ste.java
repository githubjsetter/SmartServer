package com.smart.platform.demo.mste;

import com.smart.extension.ste.CSteModelAp;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.ste.CSteModel;

import java.awt.*;

/*����"��Ʒ����"����༭Model*/
public class Pub_goods_ste extends CSteModelAp{
	public Pub_goods_ste(CFrame frame) throws HeadlessException {
		super(frame, "��Ʒ");
	}

	public String getTablename() {
		return "pub_goods_v";
	}

	public String getSaveCommandString() {
		return "com.inca.np.demo.mste.Pub_goods_ste.�����Ʒ";
	}
}
