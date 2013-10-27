package com.inca.np.demo.mste;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.MultisteFrame;
import com.inca.np.util.DefaultNPParam;

public class Mgoodssteframe extends MultisteFrame{

	@Override
	protected void createStes() {
		Pubfactory_ste factoryste=new Pubfactory_ste(this);
		factoryste.setShowformonly(true);
		this.addDetailste(factoryste, "factid", "factoryid");
		
		Pubgoodsdetail_ste goodsdtlste=new Pubgoodsdetail_ste(this);
		this.addDetailste(goodsdtlste, "goodsid", "goodsid");
	}

	@Override
	protected CSteModel getStemodel() {
		return new Pub_goods_ste(this);
	}
	
	
	/**
	 * 水平分割位置
	 * @return
	 */
	@Override
	protected int getHorizontalsize(){
		return 200;
	}
	
	/**
	 * 垂直分割位置
	 * @return
	 */
	@Override
	protected int getVerticalsize(){
		return 450;
	}


	public static void main(String[] args) {
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.1"; 
		DefaultNPParam.debugdbpasswd = "xjxty";
		DefaultNPParam.debugdbsid = "data";
		DefaultNPParam.debugdbusrname = "xjxty";
		DefaultNPParam.prodcontext = "npserver";

		Mgoodssteframe frm=new Mgoodssteframe();
		frm.pack();
		frm.setVisible(true);
	}
}
