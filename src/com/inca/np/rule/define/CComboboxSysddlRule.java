package com.inca.np.rule.define;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import com.inca.np.demo.ste.Pub_goods_ste;
import com.inca.np.gui.control.CTextField;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.mde.CDetailModel;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.ste.CSteModel;

/**
 * 设置系统下拉选择
 * @author Administrator
 *
 */
public class CComboboxSysddlRule extends Rulebase {
	static String[] treatableruletypes;
	static {
		treatableruletypes = new String[] { "设置系统下拉选择", "细单设置系统下拉选择" };
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
		if (getRuletype().equals("设置系统下拉选择")) {
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
		if (ss.length < 1)
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
		
		String ddlname=ss[1];
		thisinfo.setSystemddl(ddlname);
		return 0;
	}



	@Override
	public boolean setupUI(Object caller) throws Exception {
		DBTableModel dbmodel = null;
		if (getRuletype().equals("设置系统下拉选择")) {
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
			if (ss.length < 2)
				return;
			String colname = ss[0];
			ListModel lm = collist.getModel();
			for (int i = 0; i < lm.getSize(); i++) {
				if (((String) lm.getElementAt(i)).equalsIgnoreCase(colname)) {
					collist.setSelectedIndex(i);
					break;
				}
			}
			this.textDdlname.setText(ss[1]);

		}

		JList collist = null;
		CTextField textDdlname;
		
		protected void createComponent() {
			Container cp = this.getContentPane();

			// 左边是列名
			collist = createColumnlist();
			cp.add(new JScrollPane(collist), BorderLayout.WEST);

			textDdlname=new CTextField(60);
			JPanel jp=new JPanel();
			jp.add(textDdlname);
			cp.add(jp, BorderLayout.CENTER);

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
				sb.append(textDdlname.getText());
			}
			
			return sb.toString();
		}
	}

	public static void main(String[] argv) {
		Pub_goods_ste ste = new Pub_goods_ste(null);
		String expr = "usestatus:PUB_STATUS";
		CComboboxSysddlRule.SetupDialog dlg = new CComboboxSysddlRule.SetupDialog(
				ste.getDBtableModel(), expr);
		dlg.pack();
		dlg.setVisible(true);
		if(dlg.getOk()){
			System.out.println(dlg.getExpr());
		}
	}
}
