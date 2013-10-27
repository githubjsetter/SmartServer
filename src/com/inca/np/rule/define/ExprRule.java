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
 * 计算表达式 expr: 赋值列=表达式 表达式可以直接引用dbmodel中的列
 * 
 * @author Administrator
 * 
 */
public class ExprRule extends Rulebase {
	Category logger = Category.getInstance(ExprRule.class);
	static String[] treatableruletypes;
	static {
		treatableruletypes = new String[] { "自动计算", "细单自动计算" };
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
	 * @return 查询where条件
	 */
	@Override
	public int process(Object caller, int row, String editingcolname)
			throws Exception {
		if (expr == null)
			return 0;

		ExprCalcer c = new ExprCalcer(caller);
		// 解释表达式
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
		// 弹出对话框进行设置
		DBTableModel dbmodel = null;
		
		if("自动计算".equals(ruletype)){
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
			super((Frame) null, "计算表达式");
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
			Enumeration<DBColumnDisplayInfo> en = dbmodel
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
