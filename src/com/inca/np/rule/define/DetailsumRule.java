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

import com.inca.np.demo.mde.Pub_factory_mdemodel;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.ste.CSteModel;

public class DetailsumRule extends Rulebase {
	Category logger = Category.getInstance(ExprRule.class);
	static String[] treatableruletypes;
	static {
		treatableruletypes = new String[] { "细单列计算结果赋值给总单列" };
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
	 * @return 细单计算值赋给总单
	 */
	@Override
	public int process(Object caller, int dtlrow, String editingcolname)
			throws Exception {
		if (expr == null)
			return 0;

		CMdeModel mde = null;
		if (!(caller instanceof CMdeModel)) {
			throw new Exception(caller + "不是CMdeModel");
		}
		mde = (CMdeModel) caller;

		ExprCalcer c = new ExprCalcer(mde.getDetailModel());
		// 解释表达式
		String ss[] = expr.split(":");
		if (ss.length < 2)
			return 0;
		String colname = ss[0];
		String expr = ss[1];

		String v = null;
		try {
			v = c.calc(dtlrow, expr);
			CSteModel masterste = mde.getMasterModel();
			if(masterste.getRow()<0)return 0;
			String oldv = masterste.getItemValue(masterste.getRow(), colname);
			if (!v.equals(oldv)) {
				masterste.setItemValue(masterste.getRow(), colname, v);
			}
		} catch (Exception e) {
			logger.error("ERROR", e);
			return 0;
		}

		return 0;
	}

	@Override
	public boolean setupUI(Object caller) throws Exception {
		// 弹出对话框进行设置
		DBTableModel dbmodel = null;

		if (!(caller instanceof CMdeModel)) {
			throw new Exception(caller + "应是CMdeModel");
		}
		dbmodel = ((CMdeModel) caller).getMasterModel().getDBtableModel();
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
		DBTableModel mdbmodel = null;

		SetupDialog(DBTableModel mdbmodel, String expr) {
			super((Frame) null, "细单列之和赋值给总单列");
			this.expr = expr;
			this.mdbmodel = mdbmodel;
			createComponent();
			bindValue();
			localCenter();
			setHelp("将右部表达式值赋值给左部总单列。选择左边总单列。右部输入细单表达式。" +
					"支持sum(细单列名)求和;count()求细单笔数");
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

			// 设置值
			textExpr.setText(expr);
		}

		JTextArea textExpr = new JTextArea(5, 40);
		JList collist = null;

		protected void createComponent() {
			Container cp = this.getContentPane();
			// 左边是列名
			collist = createColumnlist();
			cp.add(new JScrollPane(collist), BorderLayout.WEST);

			// 右边是表达式
			cp.add(textExpr, BorderLayout.CENTER);

		}

		JList createColumnlist() {
			Vector<String> colnames = new Vector<String>();
			Enumeration<DBColumnDisplayInfo> en = mdbmodel
					.getDisplaycolumninfos().elements();
			while (en.hasMoreElements()) {
				colnames.add(en.nextElement().getColname());
			}
			JList list = new JList(colnames);
			return list;
		}

		/**
		 * 返回 列名,[id,value]
		 * 
		 * @return
		 */
		public String getExpr() {
			StringBuffer sb = new StringBuffer();
			// 列名
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
		Pub_factory_mdemodel mde = new Pub_factory_mdemodel(null, "");
		// mde.getRootpanel();

		String expr = "companyid:factid*2";
		ExprRule.SetupDialog dlg = new ExprRule.SetupDialog(mde
				.getMasterModel().getDBtableModel(), expr);
		dlg.pack();
		dlg.setVisible(true);
		if (dlg.getOk()) {
			System.out.println(dlg.getExpr());
		}
	}
}
