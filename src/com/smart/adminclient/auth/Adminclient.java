package com.smart.adminclient.auth;

import java.net.URLClassLoader;

import com.smart.platform.util.DefaultNPParam;


/**
 * ����������ͻ���
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
