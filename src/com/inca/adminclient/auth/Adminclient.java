package com.inca.adminclient.auth;

import java.net.URLClassLoader;

import com.inca.np.util.DefaultNPParam;


/**
 * 服务器管理客户端
 * @author Administrator
 *
 */
public class Adminclient {
	public static void main(String[] args) {
		DefaultNPParam.debug=0;
		DefaultNPParam.develop=0;
		LoginDialog dlg=new LoginDialog();
		dlg.pack();
		dlg.setVisible(true);
	}
	
}
