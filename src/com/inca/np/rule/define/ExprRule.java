package com.inca.np.rule.define;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListModel;

import org.apache.log4j.Category;

import com.inca.np.demo.ste.Pub_goods_ste;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.mde.CDetailModel;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.ste.CSteModel;

/**
 * ������ʽ expr: ��ֵ��=���ʽ ���ʽ����ֱ������dbmodel�е���
 * 
 * @author Administrator
 * 
 */
public class ExprRule extends Rulebase {
	Category logger = Category.getInstance(ExprRule.class);
	static String[] treatableruletypes;
	static {
		treatableruletypes = new String[] { "�Զ�����", "ϸ���Զ�����" };
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
	 * @return ��ѯwhere����
	 */
	@Override
	public int process(Object caller, int row, String editingcolname)
			throws Exception {
		if (expr == null)
			return 0;

		ExprCalcer c = new ExprCalcer(caller);
		// ���ͱ��ʽ
		String ss[] = expr.split(":");
		if (ss.length < 2)
			return 0;
		String colname = ss[0];
		String expr = ss[1];

		String v = null;
		try {
			v = c.calc(row, expr);
			if (caller instanceof CSteModel) {
				String oldv = ((CSteModel) caller).getItemValue(row, colname);
				if (!v.equals(oldv)) {
					((CSteModel) caller).setItemValue(row, colname, v);
				}
			} else if (caller instanceof CMdeModel) {
				CSteModel ste = ((CMdeModel) caller).getMasterModel();
				String oldv = ste.getItemValue(row, colname);
				if (!v.equals(oldv)) {
					ste.setItemValue(row, colname, v);
				}
			} else if (caller instanceof CDetailModel) {
				CSteModel ste = (CSteModel) caller;
				String oldv = ste.getItemValue(row, colname);
				if (!v.equals(oldv)) {
					ste.setItemValue(row, colname, v);
				}
			}
		} catch (Exception e) {
			logger.error("ERROR", e);
			return 0;
		}

		return 0;
	}

	@Override
	public boolean setupUI(Object caller) throws Exception {
		// �����Ի����������
		DBTableModel dbmodel = null;
		
		if("�Զ�����".equals(ruletype)){
			if (caller instanceof CSteModel) {
				dbmodel = ((CSteModel) caller).getDBtableModel();
			} else if (caller instanceof CMdeModel) {
				dbmodel = ((CMdeModel) caller).getMasterModel().getDBtableModel();
			}
		}else{
			if (caller instanceof CMdeModel) {
				dbmodel = ((CMdeModel) caller).getDetailModel().getDBtableModel();
			}else if (caller instanceof CDetailModel){
				dbmodel = ((CDetailModel) caller).getDBtableModel();
			}
		}
		SetupDialog dlg = new SetupDialog(dbmodel, expr);
		dlg.pack();
		dlg.setVisible(true);
		if (!dlg.getOk())
			return false;
		expr = dlg.getExpr();

		return true;
	}

	static class SetupDialog extends RulesetupDialogbase {
		String expr = null;
		DBTableModel dbmodel = null;

		SetupDialog(DBTableModel dbmodel, String expr) {
			super((Frame) null, "������ʽ");
			this.expr = expr;
			this.dbmodel = dbmodel;
			createComponent();
			bindValue();
			localCenter();
		}

		protected void bindValue() {
			if (expr == null)
				expr = "";
			String ss[] = expr.split(":");
			if (ss.length < 2)
				return;
			String colname = ss[0];
			String expr = ss[1];
			ListModel lm = collist.getModel();
			for (int i = 0; i < lm.getSize(); i++) {
				if (((String) lm.getElementAt(i)).equalsIgnoreCase(colname)) {
					collist.setSelectedIndex(i);
					break;
				}
			}

			// ����ֵ
			textExpr.setText(expr);
		}

		JTextArea textExpr = new JTextArea(5, 40);
		JList collist = null;

		protected void createComponent() {
			Container cp = this.getContentPane();
			// ���������
			collist = createColumnlist();
			cp.add(new JScrollPane(collist), BorderLayout.WEST);

			// �ұ��Ǳ��ʽ
			cp.add(textExpr, BorderLayout.CENTER);

		}

		JList createColumnlist() {
			Vector<String> colnames = new Vector<String>();
			Enumeration<DBColumnDisplayInfo> en = dbmodel
					.getDisplaycolumninfos().elements();
			while (en.hasMoreElements()) {
				colnames.add(en.nextElement().getColname());
			}
			JList list = new JList(colnames);
			return list;
		}

		/**
		 * ���� ����,[id,value]
		 * 
		 * @return
		 */
		public String getExpr() {
			StringBuffer sb = new StringBuffer();
			// ����
			if (collist.getSelectedIndex() >= 0) {
				String colname = (String) collist.getSelectedValue();
				sb.append(colname);
				sb.append(":");
				sb.append(textExpr.getText());
			}
			return sb.toString();
		}

	}

	public static void main(String[] argv) {
		Pub_goods_ste ste = new Pub_goods_ste(null);
		ste.getRootpanel();
		String expr = "factid:goodsid*2";
		ExprRule.SetupDialog dlg = new ExprRule.SetupDialog(ste
				.getDBtableModel(), expr);
		dlg.pack();
		dlg.setVisible(true);
		if (dlg.getOk()) {
			System.out.println(dlg.getExpr());
		}
	}
}
