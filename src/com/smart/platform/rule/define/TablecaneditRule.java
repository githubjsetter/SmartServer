package com.smart.platform.rule.define;

import javax.swing.JOptionPane;

import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.ste.CSteModel;

/**
 * ���ñ��ɱ༭
 * expr����
 * @author Administrator
 *
 */
public class TablecaneditRule extends Rulebase {
	static String[] treatableruletypes;
	static {
		treatableruletypes = new String[] { "�����Ա༭", "ϸ�������Ա༭" };
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
		if (getRuletype().equals("�����Ա༭")) {
			if (caller instanceof CSteModel) {
				((CSteModel) caller).setTableeditable(true);
			} else if (caller instanceof CMdeModel) {
				((CMdeModel) caller).getMasterModel().setTableeditable(true);
			} else {
				throw new Exception("caller " + caller
						+ " һ����CSteModel��CMdeModel");
			}
		} else {
			if (caller instanceof CMdeModel) {
				((CMdeModel) caller).getDetailModel().setTableeditable(true);
			} else if(caller instanceof CDetailModel){
				((CDetailModel)caller).setTableeditable(true);
			}else {
				throw new Exception("caller " + caller + " ������CMdeModel");
			}
		}


		return 0;
	}


	@Override
	public boolean setupUI(Object caller) throws Exception {
		//JOptionPane.showMessageDialog(null,"û�в�������","��ʾ", JOptionPane.INFORMATION_MESSAGE);
		return true;
	}


}
