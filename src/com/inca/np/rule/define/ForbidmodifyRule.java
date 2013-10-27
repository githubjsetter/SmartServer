package com.inca.np.rule.define;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.inca.np.demo.ste.Pub_goods_ste;
import com.inca.np.gui.control.CStetoolbar;
import com.inca.np.gui.control.CToolbar;
import com.inca.np.gui.mde.CDetailModel;
import com.inca.np.gui.mde.CMasterModel;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.ste.CSteModel;

public class ForbidmodifyRule extends Rulebase {
	static String[] treatableruletypes;
	static {
		treatableruletypes = new String[] { "�����޸�","ϸ�������޸�" };
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
		if (getRuletype().equals("�����޸�") && caller instanceof CDetailModel){
			return 0;
		}
		return -1;
	}

	/**
	 * �ڹ�������,ȥ��new
	 */
	@Override
	public int process(Object caller) throws Exception {

		if (getRuletype().equals("�����޸�")) {
			if (caller instanceof CSteModel) {
				JPanel rootpane=((CSteModel) caller).getRootpanel();
				CStetoolbar tb=searchToolbar(rootpane);
				if(tb!=null){
					hideButton(tb,CSteModel.ACTION_MODIFY);
				}
				
			} else if (caller instanceof CMdeModel) {
				JPanel rootpane=((CMdeModel) caller).getMasterModel().getRootpanel();
				CStetoolbar tb=searchToolbar(rootpane);
				if(tb!=null){
					hideButton(tb,CMdeModel.ACTION_MODIFY);
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
					hideButton(tb,CMdeModel.ACTION_MODIFYDTL);
				}
			} else if(caller instanceof CMasterModel ){
				JPanel rootpane=((CMasterModel)caller).getRootpanel();
				CStetoolbar tb=searchToolbar(rootpane);
				if(tb!=null){
					hideButton(tb,CMdeModel.ACTION_MODIFYDTL);
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
