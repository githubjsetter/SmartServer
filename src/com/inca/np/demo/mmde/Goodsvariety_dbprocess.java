package com.inca.np.demo.mmde;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.server.process.SteProcessor;

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
