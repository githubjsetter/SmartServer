package com.smart.platform.rule.define;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.smart.platform.demo.ste.Pub_goods_ste;
import com.smart.platform.gui.control.CStetoolbar;
import com.smart.platform.gui.control.CToolbar;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.ste.CSteModel;

public class ForbidqueryRule extends Rulebase {
	static String[] treatableruletypes;
	static {
		treatableruletypes = new String[] { "���β�ѯ" };
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

		if (getRuletype().equals("���β�ѯ")) {
			if (caller instanceof CSteModel) {
				JPanel rootpane=((CSteModel) caller).getRootpanel();
				CStetoolbar tb=searchToolbar(rootpane);
				if(tb!=null){
					hideButton(tb,CSteModel.ACTION_QUERY);
				}
				
			} else if (caller instanceof CMdeModel) {
				JPanel rootpane=((CMdeModel) caller).getMasterModel().getRootpanel();
				CStetoolbar tb=searchToolbar(rootpane);
				if(tb!=null){
					hideButton(tb,CMdeModel.ACTION_QUERY);
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
					hideButton(tb,CMdeModel.ACTION_QUERY);
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


	public static void main(String[] argv) {
		Pub_goods_ste ste = new Pub_goods_ste(null);
		String expr = "credate:��ǰʱ��";
		Initrule.SetupDialog dlg = new Initrule.SetupDialog(ste
				.getDBtableModel(), expr);
		dlg.pack();
		dlg.setVisible(true);
		if (dlg.getOk()) {
			System.out.println(dlg.getExpr());
		}
	}
}
