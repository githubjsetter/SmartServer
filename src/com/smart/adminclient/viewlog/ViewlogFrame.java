package com.smart.adminclient.viewlog;

import java.awt.HeadlessException;

import com.smart.platform.auth.ClientUserManager;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.Steframe;
import com.smart.platform.util.DefaultNPParam;

/**
 * �鿴����
 * @author Administrator
 *
 */
public class ViewlogFrame extends Steframe{

	public ViewlogFrame() throws HeadlessException {
		super("��ѯ����������־");
	}

	@Override
	protected CSteModel getStemodel() {
		return new Viewlog_ste(this,"��������־�ļ�");
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
