package com.smart.platform.demo.mde;

import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.mde.MdeFrame;
import com.smart.platform.util.DefaultNPParam;

import java.awt.*;

/*����"��Ʒ�ͻ�Ʒ��ϸ"�ܵ�ϸĿFrame����*/
public class Goodsdtl_frame extends MdeFrame{
	public Goodsdtl_frame() throws HeadlessException {
		super("��Ʒ�ͻ�Ʒ��ϸ");
	}

	protected CMdeModel getMdeModel() {
		return new Goodsdtl_mde(this,"��Ʒ�ͻ�Ʒ��ϸ");
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
