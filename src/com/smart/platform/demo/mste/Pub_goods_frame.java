package com.smart.platform.demo.mste;

import com.smart.platform.gui.ste.*;
import com.smart.platform.util.DefaultNPParam;

import java.awt.*;

/*����"��Ʒ����"Frame����*/
public class Pub_goods_frame extends Steframe{
	public Pub_goods_frame() throws HeadlessException {
		super("��Ʒ����");
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
