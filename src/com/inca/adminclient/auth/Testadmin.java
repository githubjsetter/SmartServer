package com.inca.adminclient.auth;

import com.inca.np.util.DefaultNPParam;

public class Testadmin {
	public static void main(String[] args) {
		DefaultNPParam.debug = 0;
		DefaultNPParam.develop=0;
		DefaultNPParam.debugdbip = "192.9.200.89";
		DefaultNPParam.debugdbpasswd = "database2";
		DefaultNPParam.debugdbsid = "pb";
		DefaultNPParam.debugdbusrname = "database2";
		DefaultNPParam.defaultappsvrurl = "http://127.0.0.1/npserver/serveradmin.do";
		DefaultNPParam.prodcontext="npserver";
		
		LoginDialog dlg=new LoginDialog();
		dlg.pack();
		dlg.setVisible(true);
	}
}
	