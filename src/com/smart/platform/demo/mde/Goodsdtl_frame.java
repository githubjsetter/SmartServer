package com.smart.platform.demo.mde;

import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.mde.MdeFrame;
import com.smart.platform.util.DefaultNPParam;

import java.awt.*;

/*功能"货品和货品明细"总单细目Frame窗口*/
public class Goodsdtl_frame extends MdeFrame{
	public Goodsdtl_frame() throws HeadlessException {
		super("货品和货品明细");
	}

	protected CMdeModel getMdeModel() {
		return new Goodsdtl_mde(this,"货品和货品明细");
	}
	
	

	@Override
	protected int getDividerLocation() {
		return 300;
	}

	public static void main(String[] argv){
        new DefaultNPParam();
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
/*		DefaultNPParam.debugdbip = "192.9.200.47";
		DefaultNPParam.debugdbpasswd = "npserver";
		DefaultNPParam.debugdbsid = "orcl";
		DefaultNPParam.debugdbusrname = "npserver";
		DefaultNPParam.prodcontext = "npserver";
*/
		
		DefaultNPParam.debugdbip = "192.9.200.89";
		DefaultNPParam.debugdbpasswd = "database2";
		DefaultNPParam.debugdbsid = "pb";
		DefaultNPParam.debugdbusrname = "database2";
		DefaultNPParam.prodcontext = "npserver";


        Goodsdtl_frame w=new Goodsdtl_frame();
        w.setOpid("2");
		w.pack();
		w.setVisible(true);
		//w.setupAp("0");
	}
}
