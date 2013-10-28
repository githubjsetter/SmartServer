package com.smart.platform.gui.ste;

import com.smart.platform.demo.ste.Pub_goods_ste;
import com.smart.platform.util.DefaultNPParam;

public class Testmste extends MultisteFrame{

	@Override
	protected void createStes() {
		addDetailste(new Pub_goods_ste(this),"goodsid", "goodsid");
	}

	public static void main(String[] args) {
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.1";
		DefaultNPParam.debugdbpasswd = "xjxty";
		DefaultNPParam.debugdbsid = "data";
		DefaultNPParam.debugdbusrname = "xjxty";
		DefaultNPParam.prodcontext = "npserver";

		Testmste frm=new Testmste();
		frm.pack();
		frm.setVisible(true);
	}

	@Override
	protected CSteModel getStemodel() {
		return new Pub_goods_ste(this);
	}

}
