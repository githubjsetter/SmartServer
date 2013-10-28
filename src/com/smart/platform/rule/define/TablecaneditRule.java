package com.smart.platform.rule.define;

import javax.swing.JOptionPane;

import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.ste.CSteModel;

/**
 * 设置表格可编辑
 * expr无用
 * @author Administrator
 *
 */
public class TablecaneditRule extends Rulebase {
	static String[] treatableruletypes;
	static {
		treatableruletypes = new String[] { "表格可以编辑", "细单表格可以编辑" };
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

	@Override
	public int process(Object caller) throws Exception {
		if (getRuletype().equals("表格可以编辑")) {
			if (caller instanceof CSteModel) {
				((CSteModel) caller).setTableeditable(true);
			} else if (caller instanceof CMdeModel) {
				((CMdeModel) caller).getMasterModel().setTableeditable(true);
			} else {
				throw new Exception("caller " + caller
						+ " 一定是CSteModel或CMdeModel");
			}
		} else {
			if (caller instanceof CMdeModel) {
				((CMdeModel) caller).getDetailModel().setTableeditable(true);
			} else if(caller instanceof CDetailModel){
				((CDetailModel)caller).setTableeditable(true);
			}else {
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
