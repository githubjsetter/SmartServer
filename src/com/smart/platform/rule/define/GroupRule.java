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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellEditor;

import com.smart.platform.demo.ste.Pub_goods_ste;
import com.smart.platform.gui.control.CEditableTable;
import com.smart.platform.gui.control.CPlainTextField;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.control.SplitGroupInfo;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.rule.define.CComboboxDropdownRule.SetupDialog;

/**
 * ������� expr:
 * (title)(datacolumn[:datacolun])(datacolumn,method[:datacolumn,method])
 * 
 * @author Administrator
 * 
 */
public class GroupRule extends Rulebase {
	static String[] treatableruletypes;
	static {
		treatableruletypes = new String[] { "����" };
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

	
	@Override
	public SplitGroupInfo processGroup(Object caller) throws Exception {
		if(expr==null || expr.length()==0)return null;
		
		SplitGroupInfo groupinfo=new SplitGroupInfo();;
		int p = expr.indexOf("(");
		if (p < 0)
			return null;

		int p1 = expr.indexOf(")", p);
		if (p1 < 0)
			return null;
		String title = expr.substring(p + 1, p1);
		groupinfo.setTitle(title);
		p = expr.indexOf("(", p1);
		if (p < 0)
			return null;

		p1 = expr.indexOf(")", p);
		if (p1 < 0)
			return null;

		String s = expr.substring(p + 1, p1);
		String ss[] = s.split(":");
		for (int i = 0; i < ss.length; i++) {
			groupinfo.addGroupColumn(ss[i]);
		}


		p = expr.indexOf("(", p1);
		if (p < 0)
			return null;
		p1 = expr.indexOf(")", p);
		if (p1 < 0)
			return null;

		s = expr.substring(p + 1, p1);
		ss = s.split(":");
		for (int i = 0; i < ss.length; i++) {
			String nvs[] = ss[i].split(",");
			if (nvs != null && nvs.length == 2) {
				groupinfo.addDataColumn(nvs[0],nvs[1]);
			}
		}

		return groupinfo;
	}

	@Override
	public boolean setupUI(Object caller) throws Exception {
		DBTableModel dbmodel = null;
		if (getRuletype().equals("����")) {
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

	/**
	 * ���÷���. һ����,����. һ��������, ��һ���Ƿ��鷽��: ������|���������|������ƽ��
	 * 
	 * @author Administrator
	 * 
	 */
	static class SetupDialog extends RulesetupDialogbase {
		DBTableModel dbmodel = null;
		String expr = null;

		SetupDialog(DBTableModel dbmodel, String expr) {
			super((Frame) null, "���÷��鷽��");
			this.dbmodel = dbmodel;
			this.expr = expr;
			createComponent();
			bindValue();
			localCenter();
		}

		protected void bindValue() {
			Enumeration<DBColumnDisplayInfo> en = dbmodel
					.getDisplaycolumninfos().elements();
			while (en.hasMoreElements()) {
				DBColumnDisplayInfo colinfo = en.nextElement();
				if (colinfo.getColtype().equals("�к�"))
					continue;
				if (colinfo.isHide())
					continue;
				dbtablemodel.appendRow();
				int r = dbtablemodel.getRowCount() - 1;
				dbtablemodel.setItemValue(r, "colname", colinfo.getColname());
				dbtablemodel.setItemValue(r, "title", colinfo.getTitle());
			}
			dbtablemodel.appendRow();

			// �������ʽ
			if (expr == null || expr.length() == 0)
				return;

			int p = expr.indexOf("(");
			if (p < 0)
				return;

			int p1 = expr.indexOf(")", p);
			if (p1 < 0)
				return;
			String title = expr.substring(p + 1, p1);
			textTitle.setText(title);
			p = expr.indexOf("(", p1);
			if (p < 0)
				return;

			p1 = expr.indexOf(")", p);
			if (p1 < 0)
				return;

			String s = expr.substring(p + 1, p1);
			String ss[] = s.split(":");
			HashMap<String, String> map = new HashMap<String, String>();
			for (int i = 0; i < ss.length; i++) {
				map.put(ss[i].toLowerCase(), "");
			}

			for (int r = 0; r < dbtablemodel.getRowCount(); r++) {
				String colname = dbtablemodel.getItemValue(r, "colname")
						.toLowerCase();
				if (map.get(colname) != null) {
					dbtablemodel.setItemValue(r, "groupmethod", "������");
				}
			}

			p = expr.indexOf("(", p1);
			if (p < 0)
				return;
			p1 = expr.indexOf(")", p);
			if (p1 < 0)
				return;

			s = expr.substring(p + 1, p1);
			ss = s.split(":");
			map = new HashMap<String, String>();
			for (int i = 0; i < ss.length; i++) {
				String nvs[] = ss[i].split(",");
				if (nvs != null && nvs.length == 2) {
					map.put(nvs[0].toLowerCase(), nvs[1]);
				}
			}

			for (int r = 0; r < dbtablemodel.getRowCount(); r++) {
				String colname = dbtablemodel.getItemValue(r, "colname")
						.toLowerCase();
				String method = map.get(colname);
				if (method != null) {
					if(method.equals("sum"))method="���������";
					if(method.equals("avg"))method="��������ƽ��";
					if(method.equals("max"))method="�����������";
					if(method.equals("min"))method="����������С";
					if(method.equals("count"))method="������������";
					dbtablemodel.setItemValue(r, "groupmethod", method);
				}
			}
			
			table.tableChanged(new TableModelEvent(dbtablemodel));
			table.autoSize();
		}

		CEditableTable table;
		CPlainTextField textTitle = new CPlainTextField();
		DBTableModel dbtablemodel;

		protected void createComponent() {
			Container cp = this.getContentPane();

			JPanel jp = new JPanel();
			cp.add(jp, BorderLayout.NORTH);
			jp.add(new JLabel("�������"));
			jp.add(textTitle);
			textTitle.setPreferredSize(new Dimension(100, 27));

			table = createTable();
			cp.add(new JScrollPane(table), BorderLayout.CENTER);

			dbtablemodel = (DBTableModel) table.getModel();
		}

		CEditableTable createTable() {
			Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
			DBColumnDisplayInfo col = new DBColumnDisplayInfo("colname",
					"varchar", "����");
			//col.setReadonly(true);
			cols.add(col);
			
			col = new DBColumnDisplayInfo("title",
					"varchar", "������");
			cols.add(col);			
			col = new DBColumnDisplayInfo("groupmethod", "varchar", "���鷽��");
			col.setEditcomptype("combobox");
			col.addComboxBoxItem("", "");
			col.addComboxBoxItem("������", "������");
			col.addComboxBoxItem("���������", "���������");
			col.addComboxBoxItem("��������ƽ��", "��������ƽ��");
			col.addComboxBoxItem("�����������", "�����������");
			col.addComboxBoxItem("����������С", "����������С");
			col.addComboxBoxItem("������������", "������������");
			cols.add(col);

			CEditableTable table = new CEditableTable(new DBTableModel(cols));
			InputMap im = table
					.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "");
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "");
			table.setAutoResizeMode(CEditableTable.AUTO_RESIZE_OFF);
			return table;
		}

		/**
		 * ���� ����,[id,value]
		 * 
		 * @return
		 */
		public String getExpr() {
			StringBuffer colsb = new StringBuffer();
			StringBuffer methodsb = new StringBuffer();
			for (int r = 0; r < dbtablemodel.getRowCount(); r++) {
				String colname = dbtablemodel.getItemValue(r, "colname");
				String method = dbtablemodel.getItemValue(r, "groupmethod");
				if (method.length() == 0)
					continue;
				if (method.equals("������")) {
					if (colsb.length() > 0)
						colsb.append(":");
					colsb.append(colname);
				} else {
					if (methodsb.length() > 0)
						methodsb.append(":");
					methodsb.append(colname);
					methodsb.append(",");
					if(method.equals("���������"))method="sum";
					if(method.equals("��������ƽ��"))method="avg";
					if(method.equals("�����������"))method="max";
					if(method.equals("����������С"))method="min";
					if(method.equals("������������"))method="count";
					methodsb.append(method);
				}
			}
			String title=textTitle.getText();
			if(title.length()==0)title="����С��";
			return "(" + title + ")" + "(" + colsb.toString()
					+ ")(" + methodsb.toString() + ")";
		}

		@Override
		protected void onOk() {
			TableCellEditor tce = table.getCellEditor();
			if (tce != null)
				tce.stopCellEditing();
			super.onOk();
		}

	}

	public static void main(String[] argv) {
		Pub_goods_ste ste = new Pub_goods_ste(null);
		String expr = "(��ƷС��)(opcode:SUPPLYTAXRATE:goodsname:goodstype:prodarea:goodspinyin:FACTORYOPCODE)(goodsid,sum:factid,avg)";
		GroupRule.SetupDialog dlg = new GroupRule.SetupDialog(ste
				.getDBtableModel(), expr);
		dlg.pack();
		dlg.setVisible(true);
		if (dlg.getOk()) {
			System.out.println(dlg.getExpr());
		}
	}

}
