package com.smart.platform.demo.mmde;

import com.smart.platform.demo.mde.Goodsdtl_frame;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.mde.MMdeFrame;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.util.DefaultNPParam;

public class MMdegoodsFrame extends MMdeFrame{

	public MMdegoodsFrame() {
		super();
	}

	@Override
	protected CMdeModel createMde() {
		Goodsdtl_frame goodsdtlframe=new Goodsdtl_frame();
		//goodsdtlframe.setOpid("1234");
		goodsdtlframe.pack();
		return goodsdtlframe.getCreatedMdemodel();
	}

	@Override
	protected String getMderelatecolname() {
		return "varietyid";
	}

	@Override
	protected String getStepkcolname() {
		return "varietyid";
	}

	@Override
	protected CSteModel getStemodel() {
		Goodsvariety_frame frm=new Goodsvariety_frame();
		//frm.setopid(...)
		frm.pack();
		return frm.getCreatedStemodel();
	}

	public static void main(String[] args) {
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.89";
		DefaultNPParam.debugdbpasswd = "nbms";
		DefaultNPParam.debugdbsid = "pb";
		DefaultNPParam.debugdbusrname = "nbms";
		DefaultNPParam.prodcontext = "npserver";
		
		MMdegoodsFrame frm=new MMdegoodsFrame();
		frm.pack();
		frm.setVisible(true);
	}

}
