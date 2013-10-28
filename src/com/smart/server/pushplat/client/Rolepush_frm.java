package com.smart.server.pushplat.client;

import java.awt.HeadlessException;

import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.mde.MdeFrame;
import com.smart.platform.util.DefaultNPParam;

public class Rolepush_frm extends MdeFrame{

	public Rolepush_frm() throws HeadlessException {
		super("角色推送定义");
	}

	@Override
	protected CMdeModel getMdeModel() {
		return new Rolepush_mde(this,"角色推送");
	}

	public static void main(String[] args) {
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;

		DefaultNPParam.debugdbip = "192.9.200.89";
		DefaultNPParam.debugdbpasswd = "database2";
		DefaultNPParam.debugdbsid = "pb";
		DefaultNPParam.debugdbusrname = "database2";
		DefaultNPParam.prodcontext = "npserver";
		
		Rolepush_frm frm=new Rolepush_frm();
		frm.pack();
		frm.setVisible(true);
		
	}
}
