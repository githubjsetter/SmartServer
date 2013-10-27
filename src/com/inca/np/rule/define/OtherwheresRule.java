package com.inca.np.rule.define;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.inca.np.auth.ClientUserManager;
import com.inca.np.auth.Userruninfo;
import com.inca.np.demo.ste.Pub_goods_ste;

/**
 * 其它的查询条件 expr:查询条件,select的where子句. 带中文变量
 * 
 * @author Administrator
 * 
 */
public class OtherwheresRule extends Rulebase {
	static String[] treatableruletypes;
	static {
		treatableruletypes = new String[] { "附加查询条件" };
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
	 * 返回-1. 在on_beforedel()中起作用
	 */
	public int process(Object caller, int row) throws Exception {
		return -1;
	}

	/**
	 * @return 查询where条件
	 */
	@Override
	public String processWheres(Object caller) throws Exception {
		if (expr == null)
			return "";

		if (!getRuletype().equals("附加查询条件"))
			return "";

		String wheres = expr;
		Userruninfo u = ClientUserManager.getCurrentUser();
		if (wheres.indexOf("<当前部门ID>") >= 0)
			wheres = wheres.replaceAll("<当前部门ID>", u.getDeptid());
		if (wheres.indexOf("<当前人员ID>") >= 0)
			wheres = wheres.replaceAll("<当前人员ID>", u.getUserid());
		if (wheres.indexOf("<当前角色ID>") >= 0)
			wheres = wheres.replaceAll("<当前角色ID>", u.getRoleid());
		if (wheres.indexOf("<当前逻辑日>") >= 0)
			wheres = wheres
					.replaceAll("<当前逻辑日>", String.valueOf(u.getUseday()));
		if (wheres.indexOf("<当前门店ID>") >= 0)
			wheres = wheres.replaceAll("<当前门店ID>", u.getPlacepointid());
		if (wheres.indexOf("<当前保管帐ID>") >= 0)
			wheres = wheres.replaceAll("<当前保管帐ID>", u.getStorageid());

		return wheres;
	}

	@Override
	public boolean setupUI(Object caller) throws Exception {
		// 弹出对话框进行设置
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
			super((Frame) null, "附加查询条件");
			this.expr = expr;
			createComponent();
			bindValue();
			localCenter();
		}

		protected void bindValue() {
			if (expr == null)
				expr = "";
			textWhere.setText(expr);
		}

		JTextArea textWhere = new JTextArea(5, 40);

		protected void createComponent() {
			Container cp = this.getContentPane();
			JPanel jp = new JPanel();
			cp.add(jp, BorderLayout.CENTER);

			jp.setLayout(new BorderLayout());
			jp.add(new JScrollPane(textWhere), BorderLayout.CENTER);
			JPanel btnpanel = new JPanel();
			jp.add(btnpanel, BorderLayout.EAST);
			BoxLayout boxl = new BoxLayout(btnpanel, BoxLayout.Y_AXIS);
			btnpanel.setLayout(boxl);

			JButton btn;
			btn = new JButton("当前人员ID");
			btn.setActionCommand("<当前人员ID>");
			btn.addActionListener(this);
			btnpanel.add(btn);

			btn = new JButton("当前部门ID");
			btn.setActionCommand("<当前部门ID>");
			btn.addActionListener(this);
			btnpanel.add(btn);

			btn = new JButton("当前角色ID");
			btn.setActionCommand("<当前角色ID>");
			btn.addActionListener(this);
			btnpanel.add(btn);

			btn = new JButton("当前门店ID");
			btn.setActionCommand("<当前门店ID>");
			btn.addActionListener(this);
			btnpanel.add(btn);

			btn = new JButton("当前保管帐ID");
			btn.setActionCommand("<当前保管帐ID>");
			btn.addActionListener(this);
			btnpanel.add(btn);

			btn = new JButton("当前逻辑日");
			btn.setActionCommand("<当前逻辑日>");
			btn.addActionListener(this);
			btnpanel.add(btn);
		}

		/**
		 * 返回 列名,[id,value]
		 * 
		 * @return
		 */
		public String getExpr() {
			return textWhere.getText();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if (cmd.startsWith("<")) {
				textWhere.replaceSelection(e.getActionCommand());
			} else {
				super.actionPerformed(e);
			}
		}

	}

	public static void main(String[] argv) {
		Pub_goods_ste ste = new Pub_goods_ste(null);
		String expr = "inputmanid=<当前人员ID>";
		OtherwheresRule.SetupDialog dlg = new OtherwheresRule.SetupDialog(expr);
		dlg.pack();
		dlg.setVisible(true);
		if (dlg.getOk()) {
			System.out.println(dlg.getExpr());
		}
	}
}
