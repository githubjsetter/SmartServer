package com.inca.np.demo.mste;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.npx.ste.CSteModelAp;

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
