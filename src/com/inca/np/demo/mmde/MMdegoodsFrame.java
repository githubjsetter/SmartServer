package com.inca.np.demo.mmde;

import com.inca.np.demo.mde.Goodsdtl_frame;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.mde.MMdeFrame;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.util.DefaultNPParam;

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
