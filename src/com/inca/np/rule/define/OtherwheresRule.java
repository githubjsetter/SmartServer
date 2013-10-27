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
 * �����Ĳ�ѯ���� expr:��ѯ����,select��where�Ӿ�. �����ı���
 * 
 * @author Administrator
 * 
 */
public class OtherwheresRule extends Rulebase {
	static String[] treatableruletypes;
	static {
		treatableruletypes = new String[] { "���Ӳ�ѯ����" };
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
	 * ����-1. ��on_beforedel()��������
	 */
	public int process(Object caller, int row) throws Exception {
		return -1;
	}

	/**
	 * @return ��ѯwhere����
	 */
	@Override
	public String processWheres(Object caller) throws Exception {
		if (expr == null)
			return "";

		if (!getRuletype().equals("���Ӳ�ѯ����"))
			return "";

		String wheres = expr;
		Userruninfo u = ClientUserManager.getCurrentUser();
		if (wheres.indexOf("<��ǰ����ID>") >= 0)
			wheres = wheres.replaceAll("<��ǰ����ID>", u.getDeptid());
		if (wheres.indexOf("<��ǰ��ԱID>") >= 0)
			wheres = wheres.replaceAll("<��ǰ��ԱID>", u.getUserid());
		if (wheres.indexOf("<��ǰ��ɫID>") >= 0)
			wheres = wheres.replaceAll("<��ǰ��ɫID>", u.getRoleid());
		if (wheres.indexOf("<��ǰ�߼���>") >= 0)
			wheres = wheres
					.replaceAll("<��ǰ�߼���>", String.valueOf(u.getUseday()));
		if (wheres.indexOf("<��ǰ�ŵ�ID>") >= 0)
			wheres = wheres.replaceAll("<��ǰ�ŵ�ID>", u.getPlacepointid());
		if (wheres.indexOf("<��ǰ������ID>") >= 0)
			wheres = wheres.replaceAll("<��ǰ������ID>", u.getStorageid());

		return wheres;
	}

	@Override
	public boolean setupUI(Object caller) throws Exception {
		// �����Ի����������
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
			super((Frame) null, "���Ӳ�ѯ����");
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
			btn = new JButton("��ǰ��ԱID");
			btn.setActionCommand("<��ǰ��ԱID>");
			btn.addActionListener(this);
			btnpanel.add(btn);

			btn = new JButton("��ǰ����ID");
			btn.setActionCommand("<��ǰ����ID>");
			btn.addActionListener(this);
			btnpanel.add(btn);

			btn = new JButton("��ǰ��ɫID");
			btn.setActionCommand("<��ǰ��ɫID>");
			btn.addActionListener(this);
			btnpanel.add(btn);

			btn = new JButton("��ǰ�ŵ�ID");
			btn.setActionCommand("<��ǰ�ŵ�ID>");
			btn.addActionListener(this);
			btnpanel.add(btn);

			btn = new JButton("��ǰ������ID");
			btn.setActionCommand("<��ǰ������ID>");
			btn.addActionListener(this);
			btnpanel.add(btn);

			btn = new JButton("��ǰ�߼���");
			btn.setActionCommand("<��ǰ�߼���>");
			btn.addActionListener(this);
			btnpanel.add(btn);
		}

		/**
		 * ���� ����,[id,value]
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
		String expr = "inputmanid=<��ǰ��ԱID>";
		OtherwheresRule.SetupDialog dlg = new OtherwheresRule.SetupDialog(expr);
		dlg.pack();
		dlg.setVisible(true);
		if (dlg.getOk()) {
			System.out.println(dlg.getExpr());
		}
	}
}
