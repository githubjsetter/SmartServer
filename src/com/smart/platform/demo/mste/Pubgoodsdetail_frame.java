package com.smart.platform.demo.mste;

import com.smart.platform.gui.ste.*;
import com.smart.platform.util.DefaultNPParam;

import java.awt.*;

/*����"��Ʒ��ϸ"Frame����*/
public class Pubgoodsdetail_frame extends Steframe{
	public Pubgoodsdetail_frame() throws HeadlessException {
		super("��Ʒ��ϸ");
	}

	protected CSteModel getStemodel() {
		return new Pubgoodsdetail_ste(this);
	}

	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.1";
		DefaultNPParam.debugdbpasswd = "xjxty";
		DefaultNPParam.debugdbsid = "data";
		DefaultNPParam.debugdbusrname = "xjxty";
		DefaultNPParam.prodcontext = "npserver";
		
		Pubgoodsdetail_frame w=new Pubgoodsdetail_frame();
		w.pack();
		w.setVisible(true);
	}
}
