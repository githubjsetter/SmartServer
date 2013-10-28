package com.smart.platform.gui.design;

import com.smart.platform.demo.communicate.RemotesqlHelper;
import com.smart.platform.gui.control.CEditableTable;
import com.smart.platform.gui.control.CTable;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.control.HovListener;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.Hovdefine;
import com.smart.platform.image.CIcon;
import com.smart.platform.image.IconFactory;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.*;

import org.apache.log4j.Category;

import java.util.Vector;
import java.util.Enumeration;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-4-27 Time: 16:51:39
 * To change this template use File | Settings | File Templates.
 */
public class ProperSetup implements ActionListener {
	Vector<DBColumnDisplayInfo> formdbcolumndisplayinfo = null;
	DesignFrame frame = null;

/*	String coltitles[] = { "字段名", "中文名", "控件", "是否主键", "序列号", "大写", "只读",
			"可聚焦", "数据库可更新", "小数位数", "隐藏", "可查询" };
*/
	
	private CEditableTable table;

	Category logger = Category.getInstance(ProperSetup.class);
	CSteModel stemodel;
	DBTableModel editmodel;

	public ProperSetup(DesignFrame frame,
			Vector<DBColumnDisplayInfo> formdbcolumndisplayinfo,
			CSteModel stemodel) {
		this.frame = frame;
		this.formdbcolumndisplayinfo = formdbcolumndisplayinfo;
		this.stemodel = stemodel;
	}
	
	

	public void createPropPane(JPanel cp) {
		cp.removeAll();
		cp.setLayout(new BorderLayout());

		JPanel bottompanel = new JPanel();
		BoxLayout box = new BoxLayout(bottompanel, BoxLayout.X_AXIS);
		bottompanel.setLayout(box);
		cp.add(bottompanel, BorderLayout.SOUTH);

		JButton btnAdd = new JButton("增加数据库列");
		btnAdd.setActionCommand("add");
		btnAdd.addActionListener(this);
		bottompanel.add(btnAdd);

		JButton btnDel = new JButton("删除选中数据库列");
		btnDel.setActionCommand("delete");
		btnDel.addActionListener(this);
		bottompanel.add(btnDel);
		
		bottompanel.add(new JLabel("提示：序列号可输入回车或可按F12键选择HOV"));

		// 建张表
		editmodel = createEditdbmodel();
		bindData(editmodel);

		table = new CEditableTable(editmodel);
		table.setRowHeight(27);
		
		//允许多选删除
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.autoSize();
		

		TableColumnModel cm = table.getColumnModel();
		/*
		 * JTextField textSeqname=new JTextField();
		 * cm.getColumn(4).setCellEditor(new TextCellEditor(textSeqname));
		 * textSeqname.addKeyListener(new SeqnameeditorListener());
		 * textSeqname.addMouseListener(new SeqnameMouselistener());
		 */

		JScrollPane sp = new JScrollPane(table);
		cp.add(sp, BorderLayout.CENTER);

	}

	void seqHov() {
		Seqnamehov seqhov = new Seqnamehov();
		DBTableModel result = seqhov.showDialog(frame, "选择序列号");
		if (result == null)
			return;
		String seqname = result.getItemValue(0, "sequence_name");

	}

	class SeqnameeditorListener implements KeyListener {
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_F12) {
				seqHov();
			}
		}

		public void keyReleased(KeyEvent e) {
		}

		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub

		}

	}

	class SeqnameMouselistener implements MouseListener {
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() > 1) {
				seqHov();
			}
		}

		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * 将table内容填回formdbcolumndisplayinfo
	 */
	void reversebindData() {
		table.stopEdit();
		
		for (int row = 0; row < editmodel.getRowCount()-1; row++) {
			DBColumnDisplayInfo colinfo = this.formdbcolumndisplayinfo
					.get(row + 1); // 第0个是行号
			colinfo.setTitle(editmodel.getItemValue(row, "title"));
			colinfo.setEditcomptype(editmodel.getItemValue(row, "editcomp"));
			colinfo.setIspk(editmodel.getItemValue(row, "ispk").equals("1"));
			colinfo.setSeqname(editmodel.getItemValue(row, "seqname"));
			colinfo.setUppercase(editmodel.getItemValue(row, "isupper").equals(
					"1"));
			try {
				colinfo.setNumberscale(Integer.parseInt(editmodel.getItemValue(
						row, "numberscale")));
			} catch (Exception ne) {
			}
			colinfo.setReadonly(editmodel.getItemValue(row, "isreadonly")
					.equals("1"));
			colinfo.setUpdateable(editmodel.getItemValue(row, "isupdateable")
					.equals("1"));
			colinfo.setDbcolumn(editmodel.getItemValue(row, "isdbcol").equals(
					"1"));
			colinfo.setHide(editmodel.getItemValue(row, "ishide").equals("1"));
			colinfo.setCalcsum(editmodel.getItemValue(row, "calcsum")
					.equals("1"));
			colinfo.setNumberDisplayformat(editmodel.getItemValue(row, "numberdisplayformat"));
			colinfo.setWithtime(editmodel.getItemValue(row, "withtime").equals("1"));

		}

		// 重新整理一下.将所有的hide放在最后.
		Vector<DBColumnDisplayInfo> tmps = new Vector<DBColumnDisplayInfo>();
		Enumeration<DBColumnDisplayInfo> en = formdbcolumndisplayinfo
				.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en.nextElement();
			if (!colinfo.isHide()) {
				tmps.add(colinfo);
			}
		}

		en = formdbcolumndisplayinfo.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en.nextElement();
			if (colinfo.isHide()) {
				tmps.add(colinfo);
			}
		}

		// 放回去
		formdbcolumndisplayinfo.removeAllElements();
		en = tmps.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en.nextElement();
			formdbcolumndisplayinfo.add(colinfo);
		}
		stemodel.recreateDBModel();

	}

	boolean s2b(String s) {
		return s.equals("true") || s.equals("是");
	}

	CIcon okicon = IconFactory.icok16;
	JLabel lbok = new JLabel(okicon);
	JLabel lbempty = new JLabel("");

	void delColumn() {
		int ret = JOptionPane.showConfirmDialog(frame, "确定要删除？", "警告",
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (ret != JOptionPane.YES_OPTION) {
			return;
		}

		reversebindData();
		// 删除选中列
		int[] rows = table.getSelectedRows();
		for (int i = 0; i < rows.length; i++) {
			String colname = (String) table.getModel().getValueAt(rows[i], 0);
			removeColumn(colname);
		}
		stemodel.recreateTable();
		frame.drawAll();
	}

	void addColumn() {
		this.reversebindData();

		String tablename = frame.getStemodel().getTablename().toUpperCase();
		String sql = "select c.cname,c.coltype,n.cntitle from col c,sys_column_cn n where tname='"
				+ tablename
				+ "' "
				+ " and '"
				+ tablename
				+ "'=n.tablename(+) and c.cname=n.colname(+)";
		RemotesqlHelper sqlh = new RemotesqlHelper();
		DBTableModel dbcols = null;
		try {
			dbcols = sqlh.doSelect(sql, 0, 1000);
		} catch (Exception e) {
			logger.error("e", e);
			JOptionPane.showMessageDialog(frame, e.getMessage());
			return;
		}
		CSteModel ste = frame.getStemodel();
		// 删除已有的列
		for (int i = 0; i < dbcols.getRowCount(); i++) {
			String colname = dbcols.getItemValue(i, "cname");
			DBColumnDisplayInfo colinfo = ste.getDBColumnDisplayInfo(colname);
			if (colinfo != null) {
				// 说明已经有了这列了
				dbcols.removeRow(i);
				i--;
				continue;

			}
		}
		if (dbcols.getRowCount() == 0) {
			JOptionPane.showMessageDialog(frame, "已加入了数据库中所有列");
			return;
		}

		// 弹出HOV供选择
		SelectcolHov hov = new SelectcolHov(dbcols);
		DBTableModel result = hov.showDialog(frame, "选择列", "tname", tablename,
				"");
		if (result == null) {
			return;
		}

		CTable table = hov.getDlgtable();
		DBTableModel tablemodel = (DBTableModel) table.getModel();
		int selectedrows[] = table.getSelectedRows();
		for (int i = 0; i < selectedrows.length; i++) {
			int row = selectedrows[i];
			String cname = tablemodel.getItemValue(row, "cname");
			cname = cname.toLowerCase();
			String coltype = tablemodel.getItemValue(row, "coltype");
			coltype = DBColumnDisplayInfo.dbcoltype2coltype(coltype);
			String cntitle = tablemodel.getItemValue(row, "cntitle");
			DBColumnDisplayInfo newcolinfo = new DBColumnDisplayInfo(cname,
					coltype, cntitle);
			formdbcolumndisplayinfo.add(newcolinfo);
		}
		reversebindData();
		frame.drawAll();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("delete")) {
			delColumn();
		} else if (e.getActionCommand().equals("add")) {
			addColumn();
		}
	}

	private void removeColumn(String colname) {
		// To change body of created methods use File | Settings | File
		// Templates.
		Enumeration<DBColumnDisplayInfo> en = formdbcolumndisplayinfo
				.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo info = en.nextElement();
			if (info.getColname().equals(colname)) {
				formdbcolumndisplayinfo.removeElement(info);
				return;
			}
		}
	}

	class BooleanCellRender implements TableCellRenderer {
		// DefaultTableCellRenderer
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (value == null)
				return null;
			boolean booleanv = false;
			if (value instanceof Boolean) {
				Boolean ob = (Boolean) value;
				booleanv = ob.booleanValue();
			} else if (value instanceof String) {
				booleanv = ((String) value).equals("true")
						|| ((String) value).equals("是");
			}
			if (booleanv) {
				lbok.setOpaque(true);

				if (isSelected) {
					lbok.setBackground(table.getSelectionBackground());
				} else {
					lbok.setBackground(table.getBackground());
				}

				return lbok;
			} else {
				lbempty.setOpaque(true);
				if (isSelected) {
					lbempty.setBackground(table.getSelectionBackground());
				} else {
					lbempty.setBackground(table.getBackground());
				}
				return lbempty;
			}
		}
	}

	DBTableModel createEditdbmodel() {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col = new DBColumnDisplayInfo("colname", "varchar", "字段名");
		cols.add(col);
		col.setReadonly(true);

		col = new DBColumnDisplayInfo("title", "varchar", "中文名");
		cols.add(col);

		col = new DBColumnDisplayInfo("editcomp", "varchar", "控件");
		cols.add(col);
		col.setEditcomptype(DBColumnDisplayInfo.EDITCOMP_COMBOBOX);
		col.addComboxBoxItem(DBColumnDisplayInfo.EDITCOMP_TEXTFIELD,
				DBColumnDisplayInfo.EDITCOMP_TEXTFIELD);
		col.addComboxBoxItem(DBColumnDisplayInfo.EDITCOMP_COMBOBOX,
				DBColumnDisplayInfo.EDITCOMP_COMBOBOX);
		col.addComboxBoxItem(DBColumnDisplayInfo.EDITCOMP_CHECKBOX,
				DBColumnDisplayInfo.EDITCOMP_CHECKBOX);
		col.addComboxBoxItem(DBColumnDisplayInfo.EDITCOMP_TEXTAREA,
				DBColumnDisplayInfo.EDITCOMP_TEXTAREA);

		col = new DBColumnDisplayInfo("ispk", "number", "是否主键");
		cols.add(col);
		col.setEditcomptype(DBColumnDisplayInfo.EDITCOMP_CHECKBOX);

		col = new DBColumnDisplayInfo("seqname", "varchar", "序列号");
		col.setUppercase(true);
		Hovdefine hovdefine=new Hovdefine("com.inca.np.gui.design.Seqnamehov","sequence_name");
		hovdefine.putColpair("sequence_name", "seqname");
		col.setHovdefine(hovdefine);
		cols.add(col);

		col = new DBColumnDisplayInfo("isupper", "number", "大写");
		cols.add(col);
		col.setEditcomptype(DBColumnDisplayInfo.EDITCOMP_CHECKBOX);

		col = new DBColumnDisplayInfo("numberdisplayformat", "varchar", "数字格式");
		cols.add(col);


		col = new DBColumnDisplayInfo("withtime", "number", "日期带时间");
		col.setEditcomptype(DBColumnDisplayInfo.EDITCOMP_CHECKBOX);
		cols.add(col);

		col = new DBColumnDisplayInfo("isreadonly", "number", "只读");
		cols.add(col);
		col.setEditcomptype(DBColumnDisplayInfo.EDITCOMP_CHECKBOX);

		col = new DBColumnDisplayInfo("calcsum", "number", "合计");
		cols.add(col);
		col.setEditcomptype(DBColumnDisplayInfo.EDITCOMP_CHECKBOX);

		col = new DBColumnDisplayInfo("isupdateable", "number", "数据库可更新");
		cols.add(col);
		col.setEditcomptype(DBColumnDisplayInfo.EDITCOMP_CHECKBOX);

		col = new DBColumnDisplayInfo("isdbcol", "number", "数据库列");
		cols.add(col);
		col.setEditcomptype(DBColumnDisplayInfo.EDITCOMP_CHECKBOX);

		col = new DBColumnDisplayInfo("ishide", "number", "隐藏");
		cols.add(col);
		col.setEditcomptype(DBColumnDisplayInfo.EDITCOMP_CHECKBOX);

		//以后不需要设置了,由numberdisplayformat代替
		col = new DBColumnDisplayInfo("numberscale", "number", "小数位数");
		cols.add(col);

		return new DBTableModel(cols);
	}
	
	class Seqnamehovlistener implements HovListener{

		public void gainFocus(DBColumnDisplayInfo dispinfo) {
			// TODO Auto-generated method stub
			
		}

		public void lostFocus(DBColumnDisplayInfo dispinfo) {
			// TODO Auto-generated method stub
			
		}

		public void on_hov(DBColumnDisplayInfo dispinfo, DBTableModel result) {
			String seqname=result.getItemValue(0, "sequence_name");
			int row=table.getRow();
			editmodel.setItemValue(row, "seqname", seqname);
			table.tableChanged(new TableModelEvent(editmodel,row));
		}
		
	}

	void bindData(DefaultTableModel model) {
		Vector data = model.getDataVector();
		data.removeAllElements();

		Enumeration<DBColumnDisplayInfo> en = formdbcolumndisplayinfo
				.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en.nextElement();
			if (colinfo.getColname().equals("行号")) {
				continue;
			}

			int row = editmodel.getRowCount();
			editmodel.appendRow();
			editmodel.setItemValue(row, "colname", colinfo.getColname());
			editmodel.setItemValue(row, "title", colinfo.getTitle());
			editmodel.setItemValue(row, "editcomp", colinfo.getEditcomptype());
			editmodel.setItemValue(row, "ispk", colinfo.isIspk() ? "1" : "0");
			editmodel.setItemValue(row, "seqname", colinfo.getSeqname());
			editmodel.setItemValue(row, "isupper", colinfo.isUppercase() ? "1"
					: "0");
			editmodel.setItemValue(row, "numberscale", String.valueOf(colinfo
					.getNumberscale()));
			editmodel.setItemValue(row, "isreadonly",
					colinfo.isReadonly() ? "1" : "0");
			editmodel.setItemValue(row, "calcsum",
					colinfo.isCalcsum() ? "1" : "0");
			editmodel.setItemValue(row, "isupdateable",
					colinfo.isUpdateable() ? "1" : "0");
			editmodel.setItemValue(row, "isdbcol", colinfo.isDbcolumn() ? "1"
					: "0");
			editmodel.setItemValue(row, "ishide", colinfo.isHide() ? "1" : "0");
			editmodel.setItemValue(row, "numberdisplayformat", colinfo.getNumberDisplayformat());
			editmodel.setItemValue(row, "withtime", colinfo.isWithtime()? "1" : "0");
			
		}
		// 合计
		editmodel.appendRow();
	}

}
