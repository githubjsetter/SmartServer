package com.smart.platform.rule.define;

import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;

import com.smart.platform.gui.control.CTable;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.ste.CSteModel;

/**
 * 表格多选
 * @author Administrator
 *
 */
public class TablemultiselectRule extends Rulebase {
	static String[] treatableruletypes;
	static {
		treatableruletypes = new String[] { "表格多选", "细单表格多选" };
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
		CTable table=null;
		if (getRuletype().equals("表格多选")) {
			if (caller instanceof CSteModel) {
				table=((CSteModel) caller).getTable();
			} else if (caller instanceof CMdeModel) {
				table=((CMdeModel) caller).getMasterModel().getTable();
			} else {
				throw new Exception("caller " + caller
						+ " 一定是CSteModel或CMdeModel");
			}
		} else {
			if (caller instanceof CMdeModel) {
				table=((CMdeModel) caller).getDetailModel().getTable();
			} else if(caller instanceof CDetailModel){
				table=((CDetailModel)caller).getTable();
			}else {
				throw new Exception("caller " + caller + " 必须是CMdeModel");
			}
		}

		table.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		return 0;
	}


	@Override
	public boolean setupUI(Object caller) throws Exception {
		//JOptionPane.showMessageDialog(null,"没有参数设置","提示", JOptionPane.INFORMATION_MESSAGE);
		return true;
	}


}
