package com.inca.np.rule.define;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.inca.np.gui.control.CStetoolbar;
import com.inca.np.gui.control.CToolbar;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.ste.CSteModel;

public class ForbidSelectopRule   extends Rulebase {
	static String[] treatableruletypes;
	static {
		treatableruletypes = new String[] { "����ѡ����" };
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
	 * ����-1. ��on_beforedel()��������
	 */
	public int process(Object caller, int row) throws Exception {
		return -1;
	}

	/**
	 * �ڹ�������,ȥ��new
	 */
	@Override
	public int process(Object caller) throws Exception {

		if (getRuletype().equals("����ѡ����")) {
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
						+ " һ����CSteModel��CMdeModel");
			}
		} else {
			if (caller instanceof CMdeModel) {
				JPanel rootpane=((CMdeModel) caller).getMasterModel().getRootpanel();
				CStetoolbar tb=searchToolbar(rootpane);
				if(tb!=null){
					hideButton(tb,CSteModel.ACTION_SELECTOP);
				}
			} else {
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

