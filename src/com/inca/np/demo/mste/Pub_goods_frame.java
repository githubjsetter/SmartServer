package com.inca.np.demo.mste;

import com.inca.np.gui.ste.*;
import com.inca.np.util.DefaultNPParam;

import java.awt.*;

/*功能"货品管理"Frame窗口*/
public class Pub_goods_frame extends Steframe{
	public Pub_goods_frame() throws HeadlessException {
		super("货品管理");
	}

	protected CSteModel getStemodel() {
		return new Pub_goods_ste(this);
	}

	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.1";
		DefaultNPParam.debugdbpasswd = "xjxty";
		DefaultNPParam.debugdbsid = "data";
		DefaultNPParam.debugdbusrname = "xjxty";
		DefaultNPParam.prodcontext = "npserver";
		
		Pub_goods_frame w=new Pub_goods_frame();
		w.pack();
		w.setVisible(true);
	}
}
