package com.inca.np.rule.define;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;

import javax.swing.JList;
import javax.swing.JTextArea;

import org.apache.log4j.Category;


public class DetailforbiddeleteRule    extends Rulebase {
	Category logger = Category.getInstance(ExprRule.class);
	static String[] treatableruletypes;
	static {
		treatableruletypes = new String[] {"细单根据总单条件禁止删除" };
	}

	public static boolean canProcessruletype(String ruletype) {
		for (int i = 0; treatableruletypes != null
				&& i < treatableruletypes.length; i++) {
			if (treatableruletypes[i].equals(ruletype))
				return true;
		}
		return false;
	}

	public static String[] getRuleypes() {
		return treatableruletypes;
	}

	/**
	 */
	@Override
	public int process(Object caller, int row)
			throws Exception {
		if (expr == null)
			return 0;

		ExprCalcer c = new ExprCalcer(caller);

		String v = null;
		try {
			v = c.calc(row, expr);
			if(v.equals("1")){
				//条件成立,禁止删改
				return -1;
			}
		} catch (Exception e) {
			logger.error("ERROR", e);
			return 0;
		}

		return 0;
	}

	@Override
	public boolean setupUI(Object caller) throws Exception {
		SetupDialog dlg = new SetupDialog(expr);
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
			super((Frame) null, "细单根据总单条件禁止删除 总单表达式");
			this.expr = expr;
			createComponent();
			bindValue();
			localCenter();
		}

		protected void bindValue() {
			if (expr == null)
				expr = "";

			// 设置值
			textExpr.setText(expr);
		}

		JTextArea textExpr = new JTextArea(5, 40);
		JList collist = null;

		protected void createComponent() {
			Container cp = this.getContentPane();
			// 左边是列名

			// 右边是表达式
			cp.add(textExpr, BorderLayout.CENTER);

		}


		/**
		 * 返回 列名,[id,value]
		 * 
		 * @return
		 */
		public String getExpr() {
			StringBuffer sb = new StringBuffer();
			// 列名
			sb.append(textExpr.getText());
			return sb.toString();
		}

	}

}

