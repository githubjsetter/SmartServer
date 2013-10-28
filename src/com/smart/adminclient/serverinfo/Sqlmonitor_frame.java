package com.smart.adminclient.serverinfo;

import com.smart.platform.gui.ste.*;
import com.smart.platform.util.DefaultNPParam;

import java.awt.*;

import javax.swing.SwingUtilities;

/*功能"系统信息查询"Frame窗口*/
public class Sqlmonitor_frame extends Steframe{
	public Sqlmonitor_frame() throws HeadlessException {
		super("查询服务器sql执行情况");
	}

	protected CSteModel getStemodel() {
		return new Sqlmonitor_ste(this);
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
		Sqlmonitor_frame w=new Sqlmonitor_frame();
		w.pack();
		w.setVisible(true);
	}
}
