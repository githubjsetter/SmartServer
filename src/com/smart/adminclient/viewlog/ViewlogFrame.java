package com.smart.adminclient.viewlog;

import java.awt.HeadlessException;

import com.smart.platform.auth.ClientUserManager;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.Steframe;
import com.smart.platform.util.DefaultNPParam;

/**
 * 查看错误
 * @author Administrator
 *
 */
public class ViewlogFrame extends Steframe{

	public ViewlogFrame() throws HeadlessException {
		super("查询看服务器日志");
	}

	@Override
	protected CSteModel getStemodel() {
		return new Viewlog_ste(this,"服务器日志文件");
	}
	
	@Override
	public void setVisible(boolean b){
		super.setVisible(b);
		if(b){
			stemodel.doQuery();
		}
	}
	
	public static void main(String[] args) {
		DefaultNPParam.debug = 1;
		DefaultNPParam.develop = 1;
		DefaultNPParam.debugdbip = "192.9.200.89";
		DefaultNPParam.debugdbpasswd = "nbms";
		DefaultNPParam.debugdbsid = "pb";
		DefaultNPParam.debugdbusrname = "nbms";
		DefaultNPParam.prodcontext = "npserver";
		ClientUserManager.getCurrentUser().setUserid("0");

		
		ViewlogFrame frm=new ViewlogFrame();
		frm.pack();
		frm.setVisible(true);
	}
	

}
