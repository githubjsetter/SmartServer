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
 * 分组规则 expr:
 * (title)(datacolumn[:datacolun])(datacolumn,method[:datacolumn,method])
 * 
 * @author Administrator
 * 
 */
public class GroupRule extends Rulebase {
	static String[] treatableruletypes;
	static {
		treatableruletypes = new String[] { "分组" };
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
		if (getRuletype().equals("分组")) {
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
		SetupDialog dlg = new SetupDialog(dbmodel, expr);
		dlg.pack();
		dlg.setVisible(true);
		if (!dlg.getOk())
			return false;
		expr = dlg.getExpr();

		return true;
	}

	/**
	 * 设置分组. 一个表,两列. 一列是列名, 另一列是分组方法: 分组列|数据列求和|数据列平均
	 * 
	 * @author Administrator
	 * 
	 */
	static class SetupDialog extends RulesetupDialogbase {
		DBTableModel dbmodel = null;
		String expr = null;

		SetupDialog(DBTableModel dbmodel, String expr) {
			super((Frame) null, "设置分组方法");
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
				if (colinfo.getColtype().equals("行号"))
					continue;
				if (colinfo.isHide())
					continue;
				dbtablemodel.appendRow();
				int r = dbtablemodel.getRowCount() - 1;
				dbtablemodel.setItemValue(r, "colname", colinfo.getColname());
				dbtablemodel.setItemValue(r, "title", colinfo.getTitle());
			}
			dbtablemodel.appendRow();

			// 分析表达式
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
					dbtablemodel.setItemValue(r, "groupmethod", "分组列");
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
					if(method.equals("sum"))method="数据列求和";
					if(method.equals("avg"))method="数据列求平均";
					if(method.equals("max"))method="数据列求最大";
					if(method.equals("min"))method="数据列求最小";
					if(method.equals("count"))method="数据列求行数";
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
			jp.add(new JLabel("分组标题"));
			jp.add(textTitle);
			textTitle.setPreferredSize(new Dimension(100, 27));

			table = createTable();
			cp.add(new JScrollPane(table), BorderLayout.CENTER);

			dbtablemodel = (DBTableModel) table.getModel();
		}

		CEditableTable createTable() {
			Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
			DBColumnDisplayInfo col = new DBColumnDisplayInfo("colname",
					"varchar", "列名");
			//col.setReadonly(true);
			cols.add(col);
			
			col = new DBColumnDisplayInfo("title",
					"varchar", "中文名");
			cols.add(col);			
			col = new DBColumnDisplayInfo("groupmethod", "varchar", "分组方法");
			col.setEditcomptype("combobox");
			col.addComboxBoxItem("", "");
			col.addComboxBoxItem("分组列", "分组列");
			col.addComboxBoxItem("数据列求和", "数据列求和");
			col.addComboxBoxItem("数据列求平均", "数据列求平均");
			col.addComboxBoxItem("数据列求最大", "数据列求最大");
			col.addComboxBoxItem("数据列求最小", "数据列求最小");
			col.addComboxBoxItem("数据列求行数", "数据列求行数");
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
		 * 返回 列名,[id,value]
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
				if (method.equals("分组列")) {
					if (colsb.length() > 0)
						colsb.append(":");
					colsb.append(colname);
				} else {
					if (methodsb.length() > 0)
						methodsb.append(":");
					methodsb.append(colname);
					methodsb.append(",");
					if(method.equals("数据列求和"))method="sum";
					if(method.equals("数据列求平均"))method="avg";
					if(method.equals("数据列求最大"))method="max";
					if(method.equals("数据列求最小"))method="min";
					if(method.equals("数据列求行数"))method="count";
					methodsb.append(method);
				}
			}
			String title=textTitle.getText();
			if(title.length()==0)title="分组小计";
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
		String expr = "(货品小计)(opcode:SUPPLYTAXRATE:goodsname:goodstype:prodarea:goodspinyin:FACTORYOPCODE)(goodsid,sum:factid,avg)";
		GroupRule.SetupDialog dlg = new GroupRule.SetupDialog(ste
				.getDBtableModel(), expr);
		dlg.pack();
		dlg.setVisible(true);
		if (dlg.getOk()) {
			System.out.println(dlg.getExpr());
		}
	}

}
