package com.inca.np.rule.define;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.inca.np.gui.control.DBTableModel;
import com.inca.np.rule.setup.StoreprocHov;

public class PrequeryStoreprocRule   extends Rulebase {
	static protected String[] treatableruletypes = null;
	static {
		treatableruletypes = new String[] { "查询前处理存储过程" };
	}

	public static String[] getRuleypes() {
		return treatableruletypes;
	}

	public static boolean canProcessruletype(String ruletype) {
		for (int i = 0; treatableruletypes != null
				&& i < treatableruletypes.length; i++) {
			if (treatableruletypes[i].equals(ruletype))
				return true;
		}
		return false;
	}

	/**
	 * 返回存储过程名称。
	 */
	@Override
	public String processPrequerystoreproc(Object caller) throws Exception {
		if (expr == null || expr.length() == 0)
			return "";
		return expr;
	}

	@Override
	public boolean setupUI(Object caller) throws Exception {
		DBTableModel dbmodel = null;

		// 弹出对话框进行设置
		SetupDialog dlg = new SetupDialog( expr);
		dlg.pack();
		dlg.setVisible(true);
		if (!dlg.getOk())
			return false;
		expr = dlg.getExpr();

		return true;
	}

	static class SetupDialog extends RulesetupDialogbase {
		String expr = null;

		SetupDialog(String expr) {
			super((Frame) null, "设置查询前存储过程名称");
			this.expr = expr;
			createComponent();
			bindValue();
			localCenter();
		}

		protected void bindValue() {
			if (expr == null)
				expr = "";
			textStoreprocename.setText(expr);
		}

		JTextField textStoreprocename=new JTextField(20);
		protected void createComponent() {
			Container cp = this.getContentPane();
			
			JPanel jp=new JPanel();
			cp.add(jp,BorderLayout.CENTER);
			jp.setLayout(new FlowLayout());
			
			JLabel lb=new JLabel("存储过程名");
			jp.add(lb);
			
			jp.add(textStoreprocename);
			
			JButton btn=new JButton("...");
			jp.add(btn);
			btn.setActionCommand("storeproc");
			btn.addActionListener(this);
			
		}

		/**
		 * 返回 列名,[id,value]
		 * 
		 * @return
		 */
		public String getExpr() {
			return textStoreprocename.getText();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd=e.getActionCommand();
			if(cmd.equals("storeproc")){
				selectStoreproc();
			}else{
				super.actionPerformed(e);
			}
		}

		void selectStoreproc() {
			StoreprocHov hov=new StoreprocHov();
			DBTableModel result=hov.showDialog(this,"选择存储过程","","","");
			if(result==null)return;
			textStoreprocename.setText(result.getItemValue(0, "name"));
		}


	}

	public static void main(String[] argv) {
		String expr = "zx_proc1";
		PrequeryStoreprocRule.SetupDialog dlg = new PrequeryStoreprocRule.SetupDialog(expr);
		dlg.pack();
		dlg.setVisible(true);
		if (dlg.getOk()) {
			System.out.println(dlg.getExpr());
		}
	}
}
