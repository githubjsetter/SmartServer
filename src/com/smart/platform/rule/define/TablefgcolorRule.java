package com.smart.platform.rule.define;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.ste.CSteModel;

/**
 * �������ǰ������
 * @author Administrator
 *
 * expr:=��ֵ���ʽ,����Ϊif(����,ֵ1,ֵ2)��ʽ
 *
 */
public class TablefgcolorRule extends Rulebase {
	static String[] treatableruletypes;
	static {
		treatableruletypes = new String[] { "���������ɫ","ϸ�����������ɫ" };
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
	public Color processColor(Object caller, int row) throws Exception {
		if (expr == null)
			return null;
		
		DBTableModel dbmodel = null;
		if (getRuletype().equals("���������ɫ")) {
			if (caller instanceof CSteModel) {
				dbmodel = ((CSteModel) caller).getDBtableModel();
			} else if (caller instanceof CMdeModel) {
				dbmodel = ((CMdeModel) caller).getMasterModel()
						.getDBtableModel();
			} else {
				throw new Exception("caller " + caller
						+ " һ����CSteModel��CMdeModel");
			}
		} else {
			if (caller instanceof CMdeModel) {
				dbmodel = ((CMdeModel) caller).getDetailModel()
						.getDBtableModel();
			} else if (caller instanceof CDetailModel) {
				dbmodel = ((CDetailModel)caller).getDBtableModel();
			} else {
				throw new Exception("caller " + caller + " ������CMdeModel");
			}
		}

		ExprCalcer ec=new ExprCalcer(caller);
		String strcolor=ec.calc(row, expr);
		Integer color=0;
		try{
			color=Integer.parseInt(strcolor);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		int r=color & 0xff;
		int g=(color>>8) & 0xff;
		int b=(color>>16) & 0xff;
		return new Color(r,g,b);
	}


	@Override
	public boolean setupUI(Object caller) throws Exception {
		SetupDialog dlg=new SetupDialog(expr);
		dlg.pack();
		dlg.setVisible(true);
		if(dlg.getOk()){
			expr=dlg.getExpr();
			return true;
		}else{
			return false;
		}
	}

	static class SetupDialog extends RulesetupDialogbase {
		String expr = null;

		SetupDialog(String expr) {
			super((Frame) null, "���ñ��������ʾ��ɫ");
			this.expr = expr;
			createComponent();
			bindValue();
			localCenter();
		}

		protected void bindValue() {
			if (expr == null)
				expr = "";
			textExpr.setText(expr);
		}

		JTextArea textExpr=new JTextArea(5,20);

		protected void createComponent() {
			Container cp = this.getContentPane();
			
			cp.add(new JLabel("������if���ʽ,��if(money<0,255,0),��ʾ���С��0Ϊ��ɫ."),BorderLayout.NORTH);
			cp.add(new JScrollPane(textExpr), BorderLayout.CENTER);
			
			//�ұ߷Ÿ�ѡ��ɫ��
			JButton btn=new JButton("ѡ��ɫ");
			btn.setActionCommand("colorchoose");
			btn.addActionListener(this);
			cp.add(btn, BorderLayout.EAST);
			

		}


		/**
		 * ���� ���ʽ
		 * 
		 * @return
		 */
		public String getExpr() {
			return textExpr.getText();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("colorchoose")){
				chooseColor();
			}else{
				super.actionPerformed(e);
			}
		}
		
		void chooseColor(){
			Color cc=JColorChooser.showDialog(this,"ѡ����ɫ",null);
			if(cc==null)return;
			int r=cc.getRed();
			int g=cc.getGreen();
			int b=cc.getBlue();
			
			int ic=(b<<16)|(g<<8)|r;
			textExpr.replaceSelection(String.valueOf(ic));
		}
		
	}
	public static void main(String[] argv) {
		String expr = "if(goodsid<10000,255,0)";
		TablefgcolorRule.SetupDialog dlg = new TablefgcolorRule.SetupDialog(
				expr);
		dlg.pack();
		dlg.setVisible(true);
		if (dlg.getOk()) {
			System.out.println(dlg.getExpr());
		}
	}
	

}
