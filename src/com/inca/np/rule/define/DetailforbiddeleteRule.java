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
		treatableruletypes = new String[] {"ϸ�������ܵ�������ֹɾ��" };
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
				//��������,��ֹɾ��
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
			super((Frame) null, "ϸ�������ܵ�������ֹɾ�� �ܵ����ʽ");
			this.expr = expr;
			createComponent();
			bindValue();
			localCenter();
		}

		protected void bindValue() {
			if (expr == null)
				expr = "";

			// ����ֵ
			textExpr.setText(expr);
		}

		JTextArea textExpr = new JTextArea(5, 40);
		JList collist = null;

		protected void createComponent() {
			Container cp = this.getContentPane();
			// ���������

			// �ұ��Ǳ��ʽ
			cp.add(textExpr, BorderLayout.CENTER);

		}


		/**
		 * ���� ����,[id,value]
		 * 
		 * @return
		 */
		public String getExpr() {
			StringBuffer sb = new StringBuffer();
			// ����
			sb.append(textExpr.getText());
			return sb.toString();
		}

	}

}

