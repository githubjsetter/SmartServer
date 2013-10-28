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
		treatableruletypes = new String[] { "屏蔽查询" };
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

		if (getRuletype().equals("屏蔽查询")) {
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
						+ " 一定是CSteModel或CMdeModel");
			}
		} else {
			if (caller instanceof CMdeModel) {
				JPanel rootpane=((CMdeModel) caller).getMasterModel().getRootpanel();
				CStetoolbar tb=searchToolbar(rootpane);
				if(tb!=null){
					hideButton(tb,CMdeModel.ACTION_QUERY);
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
