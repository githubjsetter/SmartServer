package com.smart.platform.rule.define;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.smart.platform.gui.control.CStetoolbar;
import com.smart.platform.gui.control.CToolbar;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.ste.CSteModel;

public class ForbidSelectopRule   extends Rulebase {
	static String[] treatableruletypes;
	static {
		treatableruletypes = new String[] { "屏蔽选功能" };
	}
	public static boolean canProcessruletype(String ruletype){
		for(int i=0;treatableruletypes!=null && i<treatableruletypes.length;i++){
			if(treatableruletypes[i].equals(ruletype))return true;
		}
		return false;
	}
	public static String[] getRuleypes(){
		return treatableruletypes;
	}

	/**
	 * 返回-1. 在on_beforedel()中起作用
	 */
	public int process(Object caller, int row) throws Exception {
		return -1;
	}

	/**
	 * 在工具条中,去掉new
	 */
	@Override
	public int process(Object caller) throws Exception {

		if (getRuletype().equals("屏蔽选功能")) {
			if (caller instanceof CSteModel) {
				JPanel rootpane=((CSteModel) caller).getRootpanel();
				CStetoolbar tb=searchToolbar(rootpane);
				if(tb!=null){
					hideButton(tb,CSteModel.ACTION_SELECTOP);
				}
				
			} else if (caller instanceof CMdeModel) {
				JPanel rootpane=((CMdeModel) caller).getMasterModel().getRootpanel();
				CStetoolbar tb=searchToolbar(rootpane);
				if(tb!=null){
					hideButton(tb,CSteModel.ACTION_SELECTOP);
				}
				
			} else {
				throw new Exception("caller " + caller
						+ " 一定是CSteModel或CMdeModel");
			}
		} else {
			if (caller instanceof CMdeModel) {
				JPanel rootpane=((CMdeModel) caller).getMasterModel().getRootpanel();
				CStetoolbar tb=searchToolbar(rootpane);
				if(tb!=null){
					hideButton(tb,CSteModel.ACTION_SELECTOP);
				}
			} else {
				throw new Exception("caller " + caller + " 必须是CMdeModel");
			}
		}
		return 0;
	}

	
	@Override
	public boolean setupUI(Object caller) throws Exception {
		//JOptionPane.showMessageDialog(null,"没有参数设置","提示", JOptionPane.INFORMATION_MESSAGE);
		return true;
	}

}

