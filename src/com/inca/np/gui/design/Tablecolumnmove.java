package com.inca.np.gui.design;

import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.inca.np.gui.control.CEditableTable;
import com.inca.np.gui.control.CTable;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.control.Sumdbmodel;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ui.CTableheadUI;

public class Tablecolumnmove {

	Vector<DBColumnDisplayInfo> formcolumndisplayinfos = null;
	DesignFrame frame = null;
	CSteModel stemodel;

	public Tablecolumnmove(DesignFrame frame,
			Vector<DBColumnDisplayInfo> formcolumndisplayinfos,
			CSteModel stemodel) {
		this.frame = frame;
		this.formcolumndisplayinfos = formcolumndisplayinfos;
		this.stemodel = stemodel;
	}

	public void createPanel(JPanel jp) {
		jp.removeAll();
		// 上部为生成的表格。
		// 中部为待选的列

		jp.setLayout(new BorderLayout());
		jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		jsp.setDividerLocation(200);
		jp.add(jsp, BorderLayout.CENTER);

		DBTableModel dbmodel = new DBTableModel(formcolumndisplayinfos);
		String[] tablecolumns = stemodel.getTableColumns();
		table = createTable(dbmodel, tablecolumns);
		jsp.setLeftComponent(new JScrollPane(table));

		if (editmodel == null) {
			editmodel = createEditmodel();
			edittable = new CEditableTable(editmodel);
		}
		bindValue();
		jsp.setRightComponent(new JScrollPane(edittable));

	}

	CTable table = null;
	private DBTableModel editmodel;
	private CEditableTable edittable;
	private JSplitPane jsp;

	protected CTable createTable(DBTableModel dbmodel, String[] tablecolumns) {
		// 建列
		DefaultTableColumnModel cm = new DefaultTableColumnModel();
		String[] tmpcols = tablecolumns;
		if (tmpcols == null) {
			ArrayList<String> ar = new ArrayList<String>();
			Enumeration<DBColumnDisplayInfo> en = formcolumndisplayinfos
					.elements();
			while (en.hasMoreElements()) {
				DBColumnDisplayInfo colinfo = en.nextElement();
				ar.add(colinfo.getColname());
			}
			tmpcols = new String[ar.size()];
			ar.toArray(tmpcols);
		}

		for (int i = 0; i < tmpcols.length; i++) {
			String colname = tmpcols[i];
			// 求列序
			int j;
			Enumeration<DBColumnDisplayInfo> en = formcolumndisplayinfos
					.elements();
			for (j = 0; en.hasMoreElements(); j++) {
				DBColumnDisplayInfo colinfo = en.nextElement();

/*				if (colinfo.isHide()) {
					continue;
				}
*/				
				if (colinfo.getColname().equals(colname)) {
					TableColumn col = new TableColumn(j);
					col.setHeaderValue(colinfo.getTitle());
					if(colinfo.getTablecolumnwidth()>=0){
						col.setPreferredWidth(colinfo.getTablecolumnwidth());
					}else{
						col.setPreferredWidth(65);
					}
					cm.addColumn(col);
					break;
				}
			}
		}

		CTable table = new CTable(dbmodel, cm);
		table.getTableHeader().setUI(new CTableheadUI());
		table.setRowHeight(27);
		return table;
	}

	DBTableModel createEditmodel() {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col = new DBColumnDisplayInfo("colname", "varchar",
				"列名");
		col.setReadonly(true);
		cols.add(col);

		col = new DBColumnDisplayInfo("title", "varchar", "中文名");
		col.setReadonly(true);
		cols.add(col);

		col = new DBColumnDisplayInfo("display", "number", "显示");
		col.setEditcomptype(DBColumnDisplayInfo.EDITCOMP_CHECKBOX);
		cols.add(col);

		DBTableModel dbmodel = new DBTableModel(cols);
		return dbmodel;
		// Sumdbmodel sumdbmodel=new Sumdbmodel(dbmodel,null);
		// return sumdbmodel;
	}

	class Cblistener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			JCheckBox cb = (JCheckBox) e.getSource();
			int row = edittable.getEditingrow();
			if (row >= 0 && row < editmodel.getRowCount()) {
				editmodel.setItemValue(row, "display", cb.isSelected() ? "1"
						: "0");
				String colname = editmodel.getItemValue(row, "colname");
				if(colname.equals("行号")){
					if(!cb.isSelected()){
						JOptionPane.showMessageDialog(frame,"行号必须显示");
						cb.setSelected(true);
						return;
					}
				}
				if (cb.isSelected()) {
					// 该更是否已显示
					if (!isDisplay(colname)) {
						addDisplay(colname);
						DBTableModel dbmodel = new DBTableModel(
								formcolumndisplayinfos);
						String[] tablecolumns = stemodel.getTableColumns();
						table = createTable(dbmodel, tablecolumns);
						jsp.setLeftComponent(new JScrollPane(table));
						jsp.setDividerLocation(200);

					}
				} else {
					if (isDisplay(colname)) {
						removeDisplay(colname);
						DBTableModel dbmodel = new DBTableModel(
								formcolumndisplayinfos);
						String[] tablecolumns = stemodel.getTableColumns();
						table = createTable(dbmodel, tablecolumns);
						jsp.setLeftComponent(new JScrollPane(table));
						jsp.setDividerLocation(200);
					}
				}
			}
		}

		void addDisplay(String colname) {
			String[] tcs = stemodel.getTableColumns();
			String[] newtcs = new String[tcs.length + 1];
			System.arraycopy(tcs, 0, newtcs, 0, tcs.length);
			newtcs[newtcs.length - 1] = colname;
			stemodel.setTableColumns(newtcs);
			stemodel.recreateTable();
		}

		void removeDisplay(String colname) {
			String[] tcs = stemodel.getTableColumns();
			String[] newtcs = new String[tcs.length - 1];
			int j = 0;
			for (int i = 0; i < tcs.length; i++) {
				if (tcs[i].equalsIgnoreCase(colname))
					continue;
				newtcs[j++] = tcs[i];
			}
			stemodel.setTableColumns(newtcs);
			stemodel.recreateTable();
		}

	}

	void bindValue() {
		editmodel.clearAll();
		Enumeration<DBColumnDisplayInfo> en = formcolumndisplayinfos.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en.nextElement();
			int r = editmodel.getRowCount();
			editmodel.appendRow();
			editmodel.setItemValue(r, "colname", colinfo.getColname());
			editmodel.setItemValue(r, "title", colinfo.getTitle());
			editmodel.setItemValue(r, "display",
					isDisplay(colinfo.getColname()) ? "1" : "0");
		}
		editmodel.appendRow(); // 合计
		edittable.tableChanged(new TableModelEvent(editmodel));

		JCheckBox cb = (JCheckBox) editmodel.getDisplaycolumninfos().elementAt(
				2).getEditComponent();
		cb.addItemListener(new Cblistener());
	}

	public void reverseBinddata() {
		// 根据table的列和次序，重建stemodel的table
		ArrayList ar = new ArrayList();
		TableColumnModel cm = table.getColumnModel();
		Enumeration<TableColumn> en = cm.getColumns();
		while (en.hasMoreElements()) {
			TableColumn column = en.nextElement();
			int mindex = column.getModelIndex();
			DBColumnDisplayInfo colinfo = formcolumndisplayinfos
					.elementAt(mindex);
			ar.add(colinfo.getColname());
		}

		String names[] = new String[ar.size()];
		ar.toArray(names);
		stemodel.setTableColumns(names);
		tableColumnwidth2Dbmodel();
		stemodel.recreateTable();

	}

	boolean isDisplay(String colname) {
		String[] tcs = stemodel.getTableColumns();
		for (int i = 0; i < tcs.length; i++) {
			if (tcs[i].equalsIgnoreCase(colname))
				return true;
		}
		return false;

	}
	
	void tableColumnwidth2Dbmodel(){
		DBTableModel tabledm=(DBTableModel)table.getModel();
		Vector<DBColumnDisplayInfo> formdbcolumndisplayinfo=tabledm.getDisplaycolumninfos();

		for(int i=1;i<formdbcolumndisplayinfo.size();i++){
			DBColumnDisplayInfo colinfo=formdbcolumndisplayinfo.elementAt(i);
			//找列
			int tablemodelindex=tabledm.getColumnindex(colinfo.getColname());
			if(tablemodelindex>=0){
				int tcindex=table.convertColumnIndexToView(tablemodelindex);
				if(tcindex>=0){
					TableColumn tablec=table.getColumnModel().getColumn(tcindex);
					colinfo.setTablecolumnwidth(tablec.getWidth());
				}
			}
		}

	}

}
