package com.smart.platform.demo.mste;

import com.smart.extension.ste.CSteModelAp;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.ste.CSteModel;

import java.awt.*;

/*功能"货品管理"单表编辑Model*/
public class Pub_goods_ste extends CSteModelAp{
	public Pub_goods_ste(CFrame frame) throws HeadlessException {
		super(frame, "货品");
	}

	public String getTablename() {
		return "pub_goods_v";
	}

	public String getSaveCommandString() {
		return "com.inca.np.demo.mste.Pub_goods_ste.保存货品";
	}
}
