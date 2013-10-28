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
		treatableruletypes = new String[] { "�����" };
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
		if (getRuletype().equals("�����")) {
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
			super((Frame) null, "���ý������");
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
					dbtablemodel.setItemValue(r, "crossmethod", "����չ����");
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
					dbtablemodel.setItemValue(r, "crossmethod", "������");
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
			// jp.add(new JLabel("�������"));
			// jp.add(textTitle);
			// textTitle.setPreferredSize(new Dimension(100, 27));

			table = createTable();
			cp.add(new JScrollPane(table), BorderLayout.CENTER);

			dbtablemodel = (DBTableModel) table.getModel();
		}

		CEditableTable createTable() {
			Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
			DBColumnDisplayInfo col = new DBColumnDisplayInfo("colname",
					"varchar", "����");
			// col.setReadonly(true);
			cols.add(col);

			col = new DBColumnDisplayInfo("title", "varchar", "������");
			cols.add(col);

			col = new DBColumnDisplayInfo("crossmethod", "varchar", "��������");
			col.setEditcomptype("combobox");
			col.addComboxBoxItem("", "");
			col.addComboxBoxItem("����չ����", "����չ����");
			col.addComboxBoxItem("������", "������");
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
				String method = dbtablemodel.getItemValue(r, "crossmethod");
				if (method.length() == 0)
					continue;
				if (method.equals("����չ����")) {
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

			// �������
			boolean has = false;
			for (int r = 0; r < dbtablemodel.getRowCount(); r++) {
				String colname = dbtablemodel.getItemValue(r, "colname");
				String method = dbtablemodel.getItemValue(r, "crossmethod");
				if (method.length() == 0)
					continue;
				if (method.equals("����չ����")) {
					if (has) {
						JOptionPane.showMessageDialog(this, "һ�������ֻ�ܶ���һ������չ����");
						return;
					}
					has = true;
				} else if (method.equals("������")) {
					DBColumnDisplayInfo colinfo = dbmodel
							.getColumninfo(colname);
					if (!colinfo.getColtype().equals(
							DBColumnDisplayInfo.COLTYPE_NUMBER)) {
						JOptionPane.showMessageDialog(this, colname
								+ "���ܶ���Ϊ�����С������б������������͵ġ�");
						return;
					}
				}
			}

			super.onOk();
		}

	}

	/**
	 * �������
	 * 
	 * @param dbmodel
	 * @return
	 */
	@Override
	public DBTableModel processCrosstable(DBTableModel dbmodel,String displaycols[])
			throws Exception {

		// �������ʽ
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

		// �����µ��ж���
		Vector<DBColumnDisplayInfo> newcolinfos = new Vector<DBColumnDisplayInfo>();
		Enumeration<DBColumnDisplayInfo> en = dbmodel.getDisplaycolumninfos()
				.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en.nextElement();
			String colname = colinfo.getColname().toLowerCase();
			if(dispcolmap.get(colname.toLowerCase())==null){
				//������
				continue;
			}
			if (crosscolname.equals(colname))
				continue;
			if (datacolmap.get(colname) != null)
				continue;
			newcolinfos.add(colinfo.copy());
		}

		// ����չ��
		HashMap<String, String> crossedvaluemap = new HashMap<String, String>();
		for (int r = 0; r < dbmodel.getRowCount(); r++) {
			String v = dbmodel.getItemValue(r, crosscolname);
			if (crossedvaluemap.get(v) != null) {
				// �Ѿ��������
				continue;
			}
			// ��������
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
					throw new Exception("����չ�����г�������256�У���ֹ����");
				}
			}
		}
		//������С��
		Enumeration<String> dcolen = datacoltable.elements();
		while (dcolen.hasMoreElements()) {
			String dcolname = dcolen.nextElement();
			DBColumnDisplayInfo colinfo = dbmodel.getColumninfo(dcolname);
			DBColumnDisplayInfo newcolinfo = colinfo.copy();
			String newcolname = "linesum:" + dcolname;
			newcolinfo.setColname(newcolname);
			String newtitle = "��С��:" + colinfo.getTitle();
			newcolinfo.setTitle(newtitle);
			newcolinfo.setCalcsum(true);
			newcolinfo.setCrossdata(true);
			newcolinfos.add(newcolinfo);
		}
		
		

		// ��ʼ������
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
					// ��������չ��
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
						
						//�ۼ���С��
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

		//�����к�
		for(int r=0;r<crossdbmodel.getRowCount();r++){
			crossdbmodel.setItemValue(r, "�к�", String.valueOf(r+1));
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

		col = new DBColumnDisplayInfo("credate", "date", "����");
		colinfos.add(col);

		col = new DBColumnDisplayInfo("goodsname", "number", "��Ʒ����");
		colinfos.add(col);

		col = new DBColumnDisplayInfo("goodsqty", "number", "��Ʒ����");
		colinfos.add(col);

		col = new DBColumnDisplayInfo("storagename", "number", "������");
		colinfos.add(col);

		DBTableModel dbmodel = new DBTableModel(colinfos);
		dbmodel.appendRow();
		int r = 0;
		dbmodel.setItemValue(r, "credate", "20080311");
		dbmodel.setItemValue(r, "goodsname", "��Ʒ1");
		dbmodel.setItemValue(r, "goodsqty", "1000");
		dbmodel.setItemValue(r, "storagename", "������1");

		r++;
		dbmodel.appendRow();
		dbmodel.setItemValue(r, "credate", "20080311");
		dbmodel.setItemValue(r, "goodsname", "��Ʒ2");
		dbmodel.setItemValue(r, "goodsqty", "100");
		dbmodel.setItemValue(r, "storagename", "������2");

		r++;
		dbmodel.appendRow();
		dbmodel.setItemValue(r, "credate", "20080311");
		dbmodel.setItemValue(r, "goodsname", "��Ʒ2");
		dbmodel.setItemValue(r, "goodsqty", "123");
		dbmodel.setItemValue(r, "storagename", "������2");

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
