package com.smart.adminclient.serverinfo;

import com.smart.platform.gui.ste.*;
import com.smart.platform.util.DefaultNPParam;

import java.awt.*;

import javax.swing.SwingUtilities;

/*功能"系统信息查询"Frame窗口*/
public class Listlogin_frame extends Steframe{
	public Listlogin_frame() throws HeadlessException {
		super("查询登录用户");
	}

	protected CSteModel getStemodel() {
		return new Listlogin_ste(this);
	}
	
	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		Runnable r=new Runnable(){
			public void run(){
				stemodel.doQuery();
			}
		};
		SwingUtilities.invokeLater(r);
	}
	

	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		Listlogin_frame w=new Listlogin_frame();
		w.pack();
		w.setVisible(true);
	}
}
