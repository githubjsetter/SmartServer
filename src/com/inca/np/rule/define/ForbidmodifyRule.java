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
		treatableruletypes = new String[] { "屏蔽修改","细单屏蔽修改" };
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
		if (getRuletype().equals("屏蔽修改") && caller instanceof CDetailModel){
			return 0;
		}
		return -1;
	}

	/**
	 * 在工具条中,去掉new
	 */
	@Override
	public int process(Object caller) throws Exception {

		if (getRuletype().equals("屏蔽修改")) {
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
						+ " 一定是CSteModel或CMdeModel");
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


	public static void main(String[] argv) {
		Pub_goods_ste ste = new Pub_goods_ste(null);
		String expr = "credate:当前时间";
		Initrule.SetupDialog dlg = new Initrule.SetupDialog(ste
				.getDBtableModel(), expr);
		dlg.pack();
		dlg.setVisible(true);
		if (dlg.getOk()) {
			System.out.println(dlg.getExpr());
		}
	}
}
