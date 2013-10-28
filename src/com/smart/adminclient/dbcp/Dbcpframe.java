package com.smart.adminclient.dbcp;

import java.awt.HeadlessException;
import java.io.File;

import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.Steframe;
import com.smart.platform.util.DefaultNPParam;

public class Dbcpframe extends Steframe{

	public Dbcpframe() throws HeadlessException {
		super("数据库连接池配置");
	}

	@Override
	protected CSteModel getStemodel() {
		return new Dbcpste(this);
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		if(b){
			stemodel.doQuery();
		}
	}

	public static void main(String[] args) {
		new File("bin").mkdirs();
		new File("logs").mkdirs();
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.89";
		DefaultNPParam.debugdbpasswd = "nbms";
		DefaultNPParam.debugdbsid = "pb";
		DefaultNPParam.debugdbusrname = "nbms";
		DefaultNPParam.prodcontext = "npserver";
		
		Dbcpframe frm=new Dbcpframe();
		frm.pack();
		frm.setVisible(true);
		
	}
}
