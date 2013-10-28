package com.smart.platform.rule.define;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellEditor;

import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.demo.ste.Pub_goods_ste;
import com.smart.platform.gui.control.CEditableTable;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.util.DecimalHelper;

public class CrossRule extends Rulebase {
	static String[] treatableruletypes;
	static {
		treatableruletypes = new String[] { "交叉表" };
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
	public boolean setupUI(Object caller) throws Exception {
		DBTableModel dbmodel = null;
		if (getRuletype().equals("交叉表")) {
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
			super((Frame) null, "设置交叉表方法");
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
			p = expr.indexOf("(", 0);
			if (p < 0)
				return;

			int p1 = expr.indexOf(")", p);
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
					dbtablemodel.setItemValue(r, "crossmethod", "交叉展开列");
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
				map.put(ss[i], ss[i]);
			}

			for (int r = 0; r < dbtablemodel.getRowCount(); r++) {
				String colname = dbtablemodel.getItemValue(r, "colname")
						.toLowerCase();
				String method = map.get(colname);
				if (method != null) {
					dbtablemodel.setItemValue(r, "crossmethod", "数据列");
				}
			}

			table.tableChanged(new TableModelEvent(dbtablemodel));
			table.autoSize();
		}

		CEditableTable table;
		// CPlainTextField textTitle = new CPlainTextField();
		DBTableModel dbtablemodel;

		protected void createComponent() {
			Container cp = this.getContentPane();

			// JPanel jp = new JPanel();
			// cp.add(jp, BorderLayout.NORTH);
			// jp.add(new JLabel("分组标题"));
			// jp.add(textTitle);
			// textTitle.setPreferredSize(new Dimension(100, 27));

			table = createTable();
			cp.add(new JScrollPane(table), BorderLayout.CENTER);

			dbtablemodel = (DBTableModel) table.getModel();
		}

		CEditableTable createTable() {
			Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
			DBColumnDisplayInfo col = new DBColumnDisplayInfo("colname",
					"varchar", "列名");
			// col.setReadonly(true);
			cols.add(col);

			col = new DBColumnDisplayInfo("title", "varchar", "中文名");
			cols.add(col);

			col = new DBColumnDisplayInfo("crossmethod", "varchar", "交叉设置");
			col.setEditcomptype("combobox");
			col.addComboxBoxItem("", "");
			col.addComboxBoxItem("交叉展开列", "交叉展开列");
			col.addComboxBoxItem("数据列", "数据列");
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
				String method = dbtablemodel.getItemValue(r, "crossmethod");
				if (method.length() == 0)
					continue;
				if (method.equals("交叉展开列")) {
					if (colsb.length() > 0)
						colsb.append(":");
					colsb.append(colname);
				} else {
					if (methodsb.length() > 0)
						methodsb.append(":");
					methodsb.append(colname);
				}
			}
			return "(" + colsb.toString() + ")(" + methodsb.toString() + ")";
		}

		@Override
		protected void onOk() {
			TableCellEditor tce = table.getCellEditor();
			if (tce != null)
				tce.stopCellEditing();

			// 检查数据
			boolean has = false;
			for (int r = 0; r < dbtablemodel.getRowCount(); r++) {
				String colname = dbtablemodel.getItemValue(r, "colname");
				String method = dbtablemodel.getItemValue(r, "crossmethod");
				if (method.length() == 0)
					continue;
				if (method.equals("交叉展开列")) {
					if (has) {
						JOptionPane.showMessageDialog(this, "一个交叉表只能定义一个交叉展开列");
						return;
					}
					has = true;
				} else if (method.equals("数据列")) {
					DBColumnDisplayInfo colinfo = dbmodel
							.getColumninfo(colname);
					if (!colinfo.getColtype().equals(
							DBColumnDisplayInfo.COLTYPE_NUMBER)) {
						JOptionPane.showMessageDialog(this, colname
								+ "不能定义为数据列。数据列必须是数字类型的。");
						return;
					}
				}
			}

			super.onOk();
		}

	}

	/**
	 * 处理交叉表
	 * 
	 * @param dbmodel
	 * @return
	 */
	@Override
	public DBTableModel processCrosstable(DBTableModel dbmodel,String displaycols[])
			throws Exception {

		// 分析表达式
		if (expr == null || expr.length() == 0)
			return dbmodel;

		int p = expr.indexOf("(");
		p = expr.indexOf("(", 0);
		if (p < 0)
			return dbmodel;

		int p1 = expr.indexOf(")", p);
		if (p1 < 0)
			return dbmodel;

		String crosscolname = expr.substring(p + 1, p1).toLowerCase();

		p = expr.indexOf("(", p1);
		if (p < 0)
			return dbmodel;
		p1 = expr.indexOf(")", p);
		if (p1 < 0)
			return dbmodel;

		HashMap<String, String>dispcolmap=new HashMap<String, String>();
		for(int i=0;i<displaycols.length;i++){
			dispcolmap.put(displaycols[i].toLowerCase(), displaycols[i]);
		}
		String s = expr.substring(p + 1, p1);
		String[] ss = s.split(":");
		Vector<String> datacoltable = new Vector<String>();
		HashMap<String, String> datacolmap = new HashMap<String, String>();
		for (int i = 0; i < ss.length; i++) {
			datacolmap.put(ss[i].toLowerCase(), ss[i]);
			datacoltable.add(ss[i]);
		}

		// 生成新的列定义
		Vector<DBColumnDisplayInfo> newcolinfos = new Vector<DBColumnDisplayInfo>();
		Enumeration<DBColumnDisplayInfo> en = dbmodel.getDisplaycolumninfos()
				.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en.nextElement();
			String colname = colinfo.getColname().toLowerCase();
			if(dispcolmap.get(colname.toLowerCase())==null){
				//隐藏列
				continue;
			}
			if (crosscolname.equals(colname))
				continue;
			if (datacolmap.get(colname) != null)
				continue;
			newcolinfos.add(colinfo.copy());
		}

		// 进行展开
		HashMap<String, String> crossedvaluemap = new HashMap<String, String>();
		for (int r = 0; r < dbmodel.getRowCount(); r++) {
			String v = dbmodel.getItemValue(r, crosscolname);
			if (crossedvaluemap.get(v) != null) {
				// 已经处理过了
				continue;
			}
			// 增加新列
			Enumeration<String> dcolen = datacoltable.elements();
			while (dcolen.hasMoreElements()) {
				String dcolname = dcolen.nextElement();
				DBColumnDisplayInfo colinfo = dbmodel.getColumninfo(dcolname);
				DBColumnDisplayInfo newcolinfo = colinfo.copy();
				newcolinfo.setCrossdata(true);
				String newcolname = v + ":" + dcolname;
				newcolinfo.setColname(newcolname);
				String newtitle = v + ":" + colinfo.getTitle();
				newcolinfo.setTitle(newtitle);
				newcolinfo.setCalcsum(true);
				newcolinfos.add(newcolinfo);
				crossedvaluemap.put(v, v);
				if (crossedvaluemap.size() > 256) {
					throw new Exception("交叉展开的列超过极限256列，中止计算");
				}
			}
		}
		//加入行小计
		Enumeration<String> dcolen = datacoltable.elements();
		while (dcolen.hasMoreElements()) {
			String dcolname = dcolen.nextElement();
			DBColumnDisplayInfo colinfo = dbmodel.getColumninfo(dcolname);
			DBColumnDisplayInfo newcolinfo = colinfo.copy();
			String newcolname = "linesum:" + dcolname;
			newcolinfo.setColname(newcolname);
			String newtitle = "行小计:" + colinfo.getTitle();
			newcolinfo.setTitle(newtitle);
			newcolinfo.setCalcsum(true);
			newcolinfo.setCrossdata(true);
			newcolinfos.add(newcolinfo);
		}
		
		

		// 开始绑定数据
		HashMap<String, Integer> pkmap = new HashMap<String, Integer>();
		DBTableModel crossdbmodel = new DBTableModel(newcolinfos);
		crossdbmodel.setCrosstable(true);
		for (int r = 0; r < dbmodel.getRowCount(); r++) {
			String pkvalue = "";
			en = dbmodel.getDisplaycolumninfos().elements();
			while (en.hasMoreElements()) {
				DBColumnDisplayInfo colinfo = en.nextElement();
				String colname = colinfo.getColname().toLowerCase();
				if (datacolmap.get(colname) != null)
					continue;
				if (crosscolname.equals(colname))
					continue;
				pkvalue += ":" + dbmodel.getItemValue(r, colname);
			}
			Integer irow = pkmap.get(pkvalue);
			int nr;
			if (irow == null) {
				nr = crossdbmodel.getRowCount();
				crossdbmodel.appendRow();
				pkmap.put(pkvalue, new Integer(nr));
			} else {
				nr = irow.intValue();
			}

			en = dbmodel.getDisplaycolumninfos().elements();
			while (en.hasMoreElements()) {
				DBColumnDisplayInfo colinfo = en.nextElement();
				String colname = colinfo.getColname().toLowerCase();
				if (datacolmap.get(colname) != null)
					continue;
				if (crosscolname.equals(colname)) {
					// 按数据列展开
					String v = dbmodel.getItemValue(r, crosscolname);
					dcolen = datacoltable.elements();
					while (dcolen.hasMoreElements()) {
						String dcolname = dcolen.nextElement();
						String newcolname = v + ":" + dcolname;
						String curv = crossdbmodel.getItemValue(nr, newcolname);
						String newv = dbmodel.getItemValue(r, dcolname);
						int scale=colinfo.getNumberscale();
						if(scale==0)scale=6;
						String sumv = DecimalHelper.add(curv, newv, scale);
						if(colinfo.getNumberscale()==0){
							sumv=DecimalHelper.trimZero(sumv);
						}
						crossdbmodel.setItemValue(nr, newcolname, sumv);
						
						//累加行小计
						newcolname = "linesum:" + dcolname;
						curv = crossdbmodel.getItemValue(nr, newcolname);
						sumv = DecimalHelper.add(curv, newv, scale);
						if(colinfo.getNumberscale()==0){
							sumv=DecimalHelper.trimZero(sumv);
						}
						crossdbmodel.setItemValue(nr, newcolname, sumv);
					}
				} else {
					crossdbmodel.setItemValue(nr, colname, dbmodel
							.getItemValue(r, colname));
				}
			}
		}

		//设置行号
		for(int r=0;r<crossdbmodel.getRowCount();r++){
			crossdbmodel.setItemValue(r, "行号", String.valueOf(r+1));
			crossdbmodel.getRecordThunk(r).setDbstatus(RecordTrunk.DBSTATUS_SAVED);
		}
		
		return crossdbmodel;
	}

	public static void main(String[] argv) {
		Pub_goods_ste ste = new Pub_goods_ste(null);
		String expr = "(storagename)(goodsqty)";
		/*
		 * CrossRule.SetupDialog dlg = new CrossRule.SetupDialog(ste
		 * .getDBtableModel(), expr); dlg.pack(); dlg.setVisible(true); if
		 * (dlg.getOk()) { System.out.println(dlg.getExpr()); }
		 */
		CrossRule rule = new CrossRule();
		rule.setExpr(expr);

		Vector<DBColumnDisplayInfo> colinfos = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col = null;

		col = new DBColumnDisplayInfo("credate", "date", "日期");
		colinfos.add(col);

		col = new DBColumnDisplayInfo("goodsname", "number", "货品名称");
		colinfos.add(col);

		col = new DBColumnDisplayInfo("goodsqty", "number", "货品数量");
		colinfos.add(col);

		col = new DBColumnDisplayInfo("storagename", "number", "保管帐");
		colinfos.add(col);

		DBTableModel dbmodel = new DBTableModel(colinfos);
		dbmodel.appendRow();
		int r = 0;
		dbmodel.setItemValue(r, "credate", "20080311");
		dbmodel.setItemValue(r, "goodsname", "货品1");
		dbmodel.setItemValue(r, "goodsqty", "1000");
		dbmodel.setItemValue(r, "storagename", "保管帐1");

		r++;
		dbmodel.appendRow();
		dbmodel.setItemValue(r, "credate", "20080311");
		dbmodel.setItemValue(r, "goodsname", "货品2");
		dbmodel.setItemValue(r, "goodsqty", "100");
		dbmodel.setItemValue(r, "storagename", "保管帐2");

		r++;
		dbmodel.appendRow();
		dbmodel.setItemValue(r, "credate", "20080311");
		dbmodel.setItemValue(r, "goodsname", "货品2");
		dbmodel.setItemValue(r, "goodsqty", "123");
		dbmodel.setItemValue(r, "storagename", "保管帐2");

		try {
			DBTableModel crossdbmodel = rule.processCrosstable(dbmodel,new String[0]);
			Enumeration<DBColumnDisplayInfo> en = crossdbmodel
					.getDisplaycolumninfos().elements();
			while (en.hasMoreElements()) {
				DBColumnDisplayInfo colinfo = en.nextElement();
				System.out.print(colinfo.getTitle() + "\t");
			}
			System.out.println();

			for (r = 0; r < crossdbmodel.getRowCount(); r++) {
				for (int c = 0; c < crossdbmodel.getColumnCount(); c++) {
					System.out.print(crossdbmodel.getItemValue(r, c) + "\t");
				}
				System.out.println();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
