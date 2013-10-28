package com.smart.platform.rule.define;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.table.TableCellEditor;

import org.apache.log4j.Category;

import com.smart.platform.demo.communicate.RemotesqlHelper;
import com.smart.platform.demo.ste.Pub_goods_ste;
import com.smart.platform.gui.control.CComboBox;
import com.smart.platform.gui.control.CComboBoxModel;
import com.smart.platform.gui.control.CEditableTable;
import com.smart.platform.gui.control.CFormlayout;
import com.smart.platform.gui.control.CFormlineBreak;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.ste.CSteModel;

/**
 * ���ʽ:=������:������:sql
 * 
 * @author Administrator
 * 
 */
public class CComboboxDyndropdownRule extends Rulebase {
	Category logger = Category.getInstance(CComboboxDyndropdownRule.class);
	static protected String[] treatableruletypes = null;
	static {
		treatableruletypes = new String[] { "���ö�̬����ѡ��", "ϸ�����ö�̬����ѡ��" };
	}

	public static String[] getRuleypes() {
		return treatableruletypes;
	}

	public static boolean canProcessruletype(String ruletype) {
		for (int i = 0; treatableruletypes != null
				&& i < treatableruletypes.length; i++) {
			if (treatableruletypes[i].equals(ruletype))
				return true;
		}
		return false;
	}
	/**
	 * ��������������ݿ�
	 * keyΪsql
	 * ֵΪdbmodel
	 */
	static HashMap<String, DBTableModel>DDLmodelcache=new HashMap<String, DBTableModel>();

	@Override
	public void processItemvaluechanged(Object caller, int row,String column,
			String editvalue) throws Exception {
		if (expr == null || expr.length() == 0)
			return;

		DBTableModel dbmodel = null;
		if (getRuletype().equals("���ö�̬����ѡ��")) {
			if (caller instanceof CSteModel) {
				dbmodel = ((CSteModel) caller).getDBtableModel();
			} else if (caller instanceof CMdeModel) {
				dbmodel = ((CMdeModel) caller).getMasterModel()
						.getDBtableModel();
			} else {
				throw new Exception("caller " + caller
						+ " һ����CSteModel��CMdeModel");
			}
		} else {
			if (caller instanceof CMdeModel) {
				dbmodel = ((CMdeModel) caller).getDetailModel()
						.getDBtableModel();
			} else if (caller instanceof CDetailModel) {
				dbmodel = ((CDetailModel) caller).getDBtableModel();
			} else {
				throw new Exception("caller " + caller + " ������CMdeModel");
			}
		}

		// ���ͱ��ʽ
		String ss[] = expr.split(":");
		if (ss.length < 0)
			return;
		String triggercolname = ss[0];
		if (!triggercolname.equalsIgnoreCase(column))
			return;
		String targetcolumn = ss[1];
		String sql = ss[2];

		DBColumnDisplayInfo thisinfo = null;
		Enumeration<DBColumnDisplayInfo> en = dbmodel.getDisplaycolumninfos()
				.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en.nextElement();
			if (colinfo.getColname().equalsIgnoreCase(targetcolumn)) {
				thisinfo = colinfo;
				break;
			}
		}
		if (thisinfo == null) {
			throw new Exception("û���ҵ���" + targetcolumn);
		}
		thisinfo.setEditcomptype(DBColumnDisplayInfo.EDITCOMP_COMBOBOX);

		// ��ѯ
		sql = replaceSqlValue(sql, editvalue);
		RemotesqlHelper sqlhelper = new RemotesqlHelper();
		DBTableModel tmpdbmodel = null;
		try {
			logger.debug("dyn ddl sql:"+sql);
			tmpdbmodel=DDLmodelcache.get(sql);
			if(tmpdbmodel==null){
				tmpdbmodel = sqlhelper.doSelect(sql, 0, 100);
				DDLmodelcache.put(sql, tmpdbmodel);
			}
			CComboBoxModel cbmodel=new CComboBoxModel(tmpdbmodel,tmpdbmodel.getDBColumnName(0),tmpdbmodel.getDBColumnName(1));
			dbmodel.getRecordThunk(row).putDdldbmodel(targetcolumn, cbmodel);
		} catch (Exception e) {
			logger.error("ERROR", e);
			return;
		}
		
/*		CComboBoxModel cbmodel=new CComboBoxModel(tmpdbmodel,tmpdbmodel.getColumnName(0),tmpdbmodel.getColumnName(1));
		CComboBox cb=(CComboBox) thisinfo.getEditComponent();
		cb.setModel(cbmodel);
		
		if(cb.getItemCount()>=2){
			cb.setSelectedIndex(1);
		}
*/

		return;
	}

	String replaceSqlValue(String sql, String editvalue) {
		return sql.replaceAll("\\{�����е�ֵ\\}", "'"+editvalue+"'");
	}

	@Override
	public boolean setupUI(Object caller) throws Exception {
		DBTableModel dbmodel = null;
		if (getRuletype().equals("���ö�̬����ѡ��")) {
			if (caller instanceof CSteModel) {
				dbmodel = ((CSteModel) caller).getDBtableModel();
			} else if (caller instanceof CMdeModel) {
				dbmodel = ((CMdeModel) caller).getMasterModel()
						.getDBtableModel();
			} else {
				throw new Exception("caller " + caller
						+ " һ����CSteModel��CMdeModel");
			}
		} else {
			if (caller instanceof CMdeModel) {
				dbmodel = ((CMdeModel) caller).getDetailModel()
						.getDBtableModel();
			} else {
				throw new Exception("caller " + caller + " ������CMdeModel");
			}
		}

		// �����Ի����������
		SetupDialog dlg = new SetupDialog(dbmodel, expr);
		dlg.pack();
		dlg.setVisible(true);
		if (!dlg.getOk())
			return false;
		expr = dlg.getExpr();

		return true;
	}

	static class SetupDialog extends RulesetupDialogbase {
		DBTableModel dbmodel = null;
		String expr = null;

		SetupDialog(DBTableModel dbmodel, String expr) {
			super((Frame) null, "�����е�����ѡ��ֵ");
			this.dbmodel = dbmodel;
			this.expr = expr;
			createComponent();
			bindValue();
			localCenter();
			setHelp("��\"������\"��ֵ�仯�󣬸���sql��\"�����е�ֵ\"����̬��������ѡ��");
		}

		protected void bindValue() {
			if (expr == null)
				expr = "";
			String ss[] = expr.split(":");
			if (ss.length <= 2) {
				String sql = "select ddlid,ddlvalue from sometable where columnname = {�����е�ֵ}";
				textSql.setText(sql);
				return;

			}

			String triggercolname = ss[0];
			String targetcolname = ss[1];
			ListModel lm = collist1.getModel();
			for (int i = 0; i < lm.getSize(); i++) {
				if (((String) lm.getElementAt(i))
						.equalsIgnoreCase(triggercolname)) {
					collist1.setSelectedIndex(i);
					break;
				}
			}
			lm = collist2.getModel();
			for (int i = 0; i < lm.getSize(); i++) {
				if (((String) lm.getElementAt(i))
						.equalsIgnoreCase(targetcolname)) {
					collist2.setSelectedIndex(i);
					break;
				}
			}

			if (ss.length >= 3) {
				String sql = ss[2];
				if (sql.length() == 0) {
					sql = "select ddlid,ddlvalue from sometable where columnname = {�����е�ֵ}";
				}
				textSql.setText(sql);
			}
		}

		JList collist1 = null;
		JList collist2 = null;
		JTextArea textSql;

		protected void createComponent() {
			Container cp = this.getContentPane();

			JPanel jp = new JPanel();
			cp.add(jp, BorderLayout.CENTER);
			CFormlayout formlayout = new CFormlayout(2, 2);
			jp.setLayout(formlayout);
			// ����Ǵ����� �ұ�������������
			JLabel lb = new JLabel("������");
			jp.add(lb);
			lb = new JLabel("");
			jp.add(lb);
			lb.setPreferredSize(new Dimension(80, 27));

			lb = new JLabel("����������");
			jp.add(lb);
			formlayout.addLayoutComponent(lb, new CFormlineBreak());

			collist1 = createColumnlist();
			jp.add(new JScrollPane(collist1));
			collist2 = createColumnlist();
			JScrollPane jsp2 = new JScrollPane(collist2);
			jp.add(jsp2);
			formlayout.addLayoutComponent(jsp2, new CFormlineBreak());

			lb = new JLabel("��̬����ֵSQL");
			jp.add(lb);
			formlayout.addLayoutComponent(lb, new CFormlineBreak());

			textSql = new JTextArea(4, 40);
			textSql.setLineWrap(true);
			textSql.setWrapStyleWord(true);
			
			jp.add(new JScrollPane(textSql));

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

		CEditableTable createTable() {
			Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
			DBColumnDisplayInfo col = new DBColumnDisplayInfo("id", "varchar",
					"����ID");
			cols.add(col);
			col = new DBColumnDisplayInfo("value", "varchar", "����ֵ");
			cols.add(col);

			CEditableTable table = new CEditableTable(new DBTableModel(cols));
			InputMap im = table
					.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "");
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "");
			return table;
		}

		/**
		 * ���� ����,[id,value]
		 * 
		 * @return
		 */
		public String getExpr() {
			StringBuffer sb = new StringBuffer();
			sb.append(collist1.getSelectedValue());
			sb.append(":");
			sb.append(collist2.getSelectedValue());
			sb.append(":");
			sb.append(textSql.getText());

			return sb.toString();
		}
		
		@Override
		protected void onOk() {
			if(!checkSql(textSql.getText()))return;
			super.onOk();
		}

		boolean checkSql(String sql){
			sql=sql.replaceAll("\\{�����е�ֵ\\}","''");
			RemotesqlHelper sqlhelper = new RemotesqlHelper();
			DBTableModel tmpdbmodel = null;
			try {
				tmpdbmodel = sqlhelper.doSelect(sql, 0, 100);
				return true;
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "SQL����"+e.getMessage());
				return false;
			}

		}
	}

	public static void main(String[] argv) {
		Pub_goods_ste ste = new Pub_goods_ste(null);
		String expr = "";
		CComboboxDyndropdownRule.SetupDialog dlg = new CComboboxDyndropdownRule.SetupDialog(
				ste.getDBtableModel(), expr);
		dlg.pack();
		dlg.setVisible(true);
		if (dlg.getOk()) {
			System.out.println(dlg.getExpr());
		}
	}
}
