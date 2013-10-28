package com.smart.platform.demo.mmde;

import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.server.process.SteProcessor;

public class Goodsvariety_dbprocess extends SteProcessor{

	@Override
	protected CSteModel getSteModel() {
		return new Goodsvariety_ste();
	}

	@Override
	protected String getTablename() {
		return "pub_goods_variety";
	}

}
