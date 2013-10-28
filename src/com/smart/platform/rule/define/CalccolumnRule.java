package com.smart.platform.rule.define;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.Category;

import com.smart.platform.demo.mde.Pub_factory_mdemodel;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.ste.CSteModel;

public class CalccolumnRule extends Rulebase {
	Category logger = Category.getInstance(ExprRule.class);
	static String[] treatableruletypes;
	static {
		treatableruletypes = new String[] { "计算列", "细单计算列" };
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
	public void processCalcColumn(CSteModel ste,int row) throws Exception {
		if (expr == null)
			return;
		String ss[] = expr.split(":");
		if (ss.length < 2)
			return;
		String colname = ss[0];
		String expr = ss[1];
		DBColumnDisplayInfo colinfo=ste.getDBColumnDisplayInfo(colname);
		if(colinfo==null){
			//说明需要加列
			colinfo=new DBColumnDisplayInfo(colname,"number",colname);
			ste.getFormcolumndisplayinfos().add(colinfo);
			
		}
		colinfo.setDbcolumn(false);
		colinfo.setUpdateable(false);
		colinfo.setReadonly(true);
		
		

		DBTableModel dbmodel = ste.getDBtableModel();
		if(dbmodel==null)return;//没有初始化

		ExprCalcer c = new ExprCalcer(ste.getDBtableModel());
		// 解释表达式

		String v = null;
		int startrow,endrow;
		if(row>=0){
			startrow=row;
			endrow=row;
		}else{
			startrow=0;
			endrow=dbmodel.getRowCount()-1;
		}
		for (int r = startrow; r <=endrow; r++) {
			try {
				v = c.calc(r, expr);
				String oldv = ste.getItemValue(r, colname);
				if (!v.equals(oldv)) {
					ste.setItemValue(r, colname, v);
					if(ste.getTable()!=null){
						ste.tableChanged(r);
					}

				}
			} catch (Exception e) {
				logger.error("ERROR", e);
				return;
			}
		}
		if(ste.getSumdbmodel()!=null){
			ste.getSumdbmodel().fireDatachanged();
		}

		return;
	}

	@Override
	public boolean setupUI(Object caller) throws Exception {
		// 弹出对话框进行设置
		DBTableModel dbmodel = null;

		if(caller instanceof CMdeModel){
			if(ruletype.startsWith("细单")){
				dbmodel = ((CMdeModel) caller).getDetailModel().getDBtableModel();
			}else{
				dbmodel = ((CMdeModel) caller).getMasterModel().getDBtableModel();
			}
		}else{
			if (!(caller instanceof CSteModel)) {
				throw new Exception(caller + "应是CSteModel");
			}
			dbmodel = ((CSteModel) caller).getDBtableModel();
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
		DBTableModel mdbmodel = null;

		SetupDialog(DBTableModel mdbmodel, String expr) {
			super((Frame) null, "设置计算列");
			this.expr = expr;
			this.mdbmodel = mdbmodel;
			createComponent();
			bindValue();
			localCenter();
			setHelp("增加一个伪列。输入中文伪列名和表达式.");
		}

		protected void bindValue() {
			if (expr == null)
				expr = "";
			String ss[] = expr.split(":");
			if (ss.length < 2)
				return;
			String colname = ss[0];
			textColname.setText(colname);
			String expr = ss[1];
			// 设置值
			textExpr.setText(expr);
		}

		JTextField textColname = new JTextField(40);
		JTextArea textExpr = new JTextArea(5, 40);

		protected void createComponent() {
			Container cp = this.getContentPane();

			JPanel jp = new JPanel();
			cp.add(jp, BorderLayout.CENTER);
			GridBagLayout g = new GridBagLayout();
			jp.setLayout(g);

			JLabel lb = new JLabel("列名(中文)");
			jp.add(lb, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.NONE,
					new Insets(5, 12, 5, 5), 0, 0));
			jp.add(textColname, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.NONE,
					new Insets(5, 12, 5, 5), 0, 0));

			lb = new JLabel("表达式");
			jp.add(lb, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.NONE,
					new Insets(5, 12, 5, 5), 0, 0));
			jp.add(textExpr, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
					GridBagConstraints.WEST, GridBagConstraints.NONE,
					new Insets(5, 12, 5, 5), 0, 0));

		}

		/**
		 * 返回 列名,[id,value]
		 * 
		 * @return
		 */
		public String getExpr() {
			return textColname.getText()+":"+textExpr.getText();
		}

	}

	public static void main(String[] argv) {
		Pub_factory_mdemodel mde = new Pub_factory_mdemodel(null, "");
		// mde.getRootpanel();

		String expr = "计算列1:factid*2";
		CalccolumnRule.SetupDialog dlg = new CalccolumnRule.SetupDialog(mde
				.getMasterModel().getDBtableModel(), expr);
		dlg.pack();
		dlg.setVisible(true);
		if (dlg.getOk()) {
			System.out.println(dlg.getExpr());
		}
	}
}
