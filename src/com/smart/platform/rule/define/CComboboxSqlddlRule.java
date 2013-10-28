package com.smart.platform.rule.define;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import com.smart.platform.demo.ste.Pub_goods_ste;
import com.smart.platform.gui.control.CTextField;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.ste.CSteModel;

/**
 * 设置sql ddl
 * expr:=列名,sql,keycol,valuecol
 * @author Administrator
 *
 */
public class CComboboxSqlddlRule extends Rulebase {
	static String[] treatableruletypes;
	static {
		treatableruletypes = new String[] { "设置SQL下拉选择", "细单设置SQL下拉选择" };
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

	@Override
	public int process(Object caller) throws Exception {
		if (expr == null || expr.length()==0)
			return 0;

		DBTableModel dbmodel = null;
		if (getRuletype().equals("设置SQL下拉选择")) {
			if (caller instanceof CSteModel) {
				dbmodel = ((CSteModel) caller).getDBtableModel();
			} else if (caller instanceof CMdeModel) {
				dbmodel = ((CMdeModel) caller).getMasterModel()
						.getDBtableModel();
			} else {
				throw new Exception("caller " + caller
						+ " 一定是CSteModel或CMdeModel");
			}
		} else {
			if (caller instanceof CMdeModel) {
				dbmodel = ((CMdeModel) caller).getDetailModel()
						.getDBtableModel();
			} else if (caller instanceof CDetailModel) {
				dbmodel = ((CDetailModel)caller).getDBtableModel();
			} else {
				throw new Exception("caller " + caller + " 必须是CMdeModel");
			}
		}

		// 解释表达式
		String ss[] = expr.split(":");
		if (ss.length < 4)
			return 0;
		String colname = ss[0];
		DBColumnDisplayInfo thisinfo = null;
		Enumeration<DBColumnDisplayInfo> en = dbmodel.getDisplaycolumninfos()
				.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en.nextElement();
			if (colinfo.getColname().equalsIgnoreCase(colname)) {
				thisinfo = colinfo;
				break;
			}
		}
		if (thisinfo == null) {
			throw new Exception("没有找到列" + colname);
		}
		thisinfo.setEditcomptype(DBColumnDisplayInfo.EDITCOMP_COMBOBOX);
		
		String sql=ss[1];
		String keycol=ss[2];
		String valuecol=ss[3];
		thisinfo.setSqlDdl(sql, keycol, valuecol);
		return 0;
	}


	@Override
	public boolean setupUI(Object caller) throws Exception {
		DBTableModel dbmodel = null;
		if (getRuletype().equals("设置SQL下拉选择")) {
			if (caller instanceof CSteModel) {
				dbmodel = ((CSteModel) caller).getDBtableModel();
			} else if (caller instanceof CMdeModel) {
				dbmodel = ((CMdeModel) caller).getMasterModel()
						.getDBtableModel();
			} else {
				throw new Exception("caller " + caller
						+ " 一定是CSteModel或CMdeModel");
			}
		} else {
			if (caller instanceof CMdeModel) {
				dbmodel = ((CMdeModel) caller).getDetailModel()
						.getDBtableModel();
			} else {
				throw new Exception("caller " + caller + " 必须是CMdeModel");
			}
		}

		// 弹出对话框进行设置
		// 弹出对话框进行设置
		SetupDialog dlg=new SetupDialog(dbmodel,expr);
		dlg.pack();
		dlg.setVisible(true);
		if(!dlg.getOk())return false;
		expr=dlg.getExpr();

		return true;
	}

	static class SetupDialog extends RulesetupDialogbase {
		DBTableModel dbmodel = null;
		String expr = null;

		SetupDialog(DBTableModel dbmodel, String expr) {
			super((Frame) null, "设置列的下拉选择值");
			this.dbmodel = dbmodel;
			this.expr = expr;
			createComponent();
			bindValue();
			localCenter();
		}

		protected void bindValue() {
			if (expr == null)
				expr = "";
			String ss[] = expr.split(":");
			if (ss.length < 4)
				return;
			String colname = ss[0];
			ListModel lm = collist.getModel();
			for (int i = 0; i < lm.getSize(); i++) {
				if (((String) lm.getElementAt(i)).equalsIgnoreCase(colname)) {
					collist.setSelectedIndex(i);
					break;
				}
			}
			textsql.setText(ss[1]);
			textkeycol.setText(ss[2]);
			textvaluecol.setText(ss[3]);

		}

		JList collist = null;
		CTextField textsql;
		CTextField textkeycol;
		CTextField textvaluecol;
		
		protected void createComponent() {
			Container cp = this.getContentPane();

			// 左边是列名
			collist = createColumnlist();
			cp.add(new JScrollPane(collist), BorderLayout.WEST);

			JPanel jp=createDatapanel();
			cp.add(jp, BorderLayout.CENTER);

		}
		
		JPanel createDatapanel(){
			JPanel jp=new JPanel();
			GridBagLayout g=new GridBagLayout();
			jp.setLayout(g);
			GridBagConstraints c=new GridBagConstraints();
			
			JLabel lb=new JLabel("SQL");
			c.gridwidth=GridBagConstraints.RELATIVE;
			g.setConstraints(lb,c);
			jp.add(lb);
			
			textsql=new CTextField(60);
			c.gridwidth=GridBagConstraints.REMAINDER;
			g.setConstraints(textsql,c);
			jp.add(textsql);
			
			lb=new JLabel("ID列名");
			c.gridwidth=GridBagConstraints.RELATIVE;
			g.setConstraints(lb,c);
			jp.add(lb);
			
			textkeycol=new CTextField(10);
			c.gridwidth=GridBagConstraints.REMAINDER;
			c.anchor=GridBagConstraints.WEST;
			g.setConstraints(textkeycol,c);
			jp.add(textkeycol);
			
			lb=new JLabel("值列名");
			c.gridwidth=GridBagConstraints.RELATIVE;
			g.setConstraints(lb,c);
			jp.add(lb);
			
			textvaluecol=new CTextField(10);
			c.gridwidth=GridBagConstraints.REMAINDER;
			g.setConstraints(textvaluecol,c);
			jp.add(textvaluecol);
			
			
			return jp;
			
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
		 * @return
		 */
		public String getExpr(){
			StringBuffer sb=new StringBuffer();
			//列名
			if(collist.getSelectedIndex()>=0){
				String colname=(String) collist.getSelectedValue();
				sb.append(colname);
				sb.append(":");
				sb.append(textsql.getText());
				sb.append(":");
				sb.append(textkeycol.getText());
				sb.append(":");
				sb.append(textvaluecol.getText());
			}
			
			return sb.toString();
		}
	}

	public static void main(String[] argv) {
		Pub_goods_ste ste = new Pub_goods_ste(null);
		String expr = "usestatus:select ddlid,ddlvalue from my_ddl where keyword='PUB_STATUS':ddlid:ddlvalue";
		CComboboxSqlddlRule.SetupDialog dlg = new CComboboxSqlddlRule.SetupDialog(
				ste.getDBtableModel(), expr);
		dlg.pack();
		dlg.setVisible(true);
		if(dlg.getOk()){
			System.out.println(dlg.getExpr());
		}
	}
}
