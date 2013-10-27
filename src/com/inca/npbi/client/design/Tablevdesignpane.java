package com.inca.npbi.client.design;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.inca.np.communicate.RecordTrunk;
import com.inca.np.gui.control.CMessageDialog;
import com.inca.np.gui.control.CTable;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.control.SplitGroupInfo;
import com.inca.npbi.client.design.BITableV_def.Mergeinfo;
import com.inca.npbi.client.design.link.Linkinfo;
import com.inca.npbi.client.design.link.LinksetupDlg;

/**
 * 设计pane
 * 
 * @author user
 * 
 */

public class Tablevdesignpane extends JPanel implements ActionListener {
	ReportcanvasFrame frm = null;
	BITableV_Render tablevrender = null;
	Tabledesign_table table;
	Tablev_transferhandle tablev_transferhandle;
	JSplitPane jsplitpane;
	CTable columntable;
	Previewpane previewpane;
	Proppane proppane;
	int editingrow = 0;
	int editingcol = 0;
	BICell editingcell = null;
	JTabbedPane proptabbedpane;
	private JButton btnCancelmerge;
	private JButton btnmerge;

	public Tablevdesignpane(ReportcanvasFrame frm) {
		this.frm = frm;
		tablevrender = frm.getTablevrender();
		tablev_transferhandle = new Tablev_transferhandle(this);
		init();
		bind();

	}

	/**
	 * 将tablevdef的数据显示到表格中
	 */
	void bind() {
		BITableV_def tablevdef = tablevrender.getTablevdef();
		int colcount = tablevdef.getColcount();
		int rowcount = tablevdef.getRowcount();
		DBTableModel dm = createNewdbmodel(colcount);
		table.setSettingvalue(true);
		table.setModel(dm);
		table.setColumnSelectionAllowed(true);

		// 数据.
		for (int i = 0; i < rowcount; i++) {
			int newrow = dm.getRowCount();
			dm.appendRow();
			dm.setItemValue(newrow, "edit_area", BITableV_def
					.getRowtypeString(tablevdef.getRowtypes()[i]));
			BICell cells[] = tablevdef.getCells()[i];
			for (int c = 0; c < colcount; c++) {
				dm.setItemValue(newrow, "c_" + c, cells[c].expr);
			}
		}
		// 加入多余
		int newrow = dm.getRowCount();
		dm.appendRow();

		table.tableChanged(new TableModelEvent(dm));

		// 设置行高
		for (int i = 0; i < rowcount; i++) {
			int tmprh = tablevdef.getRowheights()[i];
			if (tmprh <= 0)
				tmprh = 27;
			table.setRowHeight(tmprh);
		}
		table.setRowHeight(newrow, 1);

		// 设置列宽
		for (int c = 1; c < table.getColumnCount(); c++) {
			TableColumn tc = table.getColumnModel().getColumn(c);
			int colwidth = tablevdef.getColwidths()[c - 1];
			tc.setPreferredWidth(colwidth);
		}
		table.setSettingvalue(false);
	}

	/**
	 * 反向回填
	 */
	public void reverseBind() {
		BITableV_def tablevdef = tablevrender.getTablevdef();
		table.stopEdit();
		DBTableModel dm = (DBTableModel) table.getModel();
		int colcount = tablevdef.getColcount();
		int rowcount = tablevdef.getRowcount();
		for (int i = 0; i < rowcount; i++) {
			BICell cells[] = tablevdef.getCells()[i];
			// 考虑到表格的列会调整,需要重新确定列的关系.
			for (int c = 0; c < table.getColumnCount(); c++) {
				TableColumn tc = table.getColumnModel().getColumn(c);
				int modelindex = tc.getModelIndex();
				if (modelindex == 0) {
					continue;
				}
				cells[c - 1].expr = dm.getItemValue(i, modelindex);
				tablevdef.getColwidths()[c - 1] = tc.getWidth();
			}
		}
		tablevdef.fireDefinechanged();
	}

	/**
	 * 新的表头行
	 */
	void newHeadline() {
		reverseBind();
		BITableV_def tablevdef = tablevrender.getTablevdef();
		tablevdef.newHeadline();
		int row = table.getRow();
		bind();
		if (row >= 0) {
			table.setRowSelectionInterval(row, row);
		}
	}

	/**
	 * 新数据行
	 */
	void newDataline() {
		reverseBind();
		BITableV_def tablevdef = tablevrender.getTablevdef();
		tablevdef.newDataline();
		int row = table.getRow();
		bind();
		if (row >= 0) {
			table.setRowSelectionInterval(row, row);
		}
	}

	/**
	 * 新表尾
	 */
	void newFootline() {
		reverseBind();
		BITableV_def tablevdef = tablevrender.getTablevdef();
		tablevdef.newFootline();
		int row = table.getRow();
		bind();
		if (row >= 0) {
			table.setRowSelectionInterval(row, row);
		}
	}

	void newColumn() {
		reverseBind();
		int c = table.getCurcol();
		c--;
		if (c < 0) {
			c = 0;
		}

		BITableV_def tablevdef = tablevrender.getTablevdef();
		tablevdef.insertColumn(c + 1);
		int row = table.getRow();
		bind();
		if (row > 0) {
			table.setRowSelectionInterval(row, row);
		}
	}

	/**
	 * 在当前列后,加入批量的列.
	 * 
	 * @param dm
	 */
	public void newColumn(Vector<RecordTrunk> dm) {
		reverseBind();
		BITableV_def tablevdef = tablevrender.getTablevdef();
		int orgcolct = tablevdef.getColcount();
		int c = table.getCurcol();
		if (c < 0) {
			c = 0;
		}

		Enumeration<RecordTrunk> en = dm.elements();

		for (int ct = 0; en.hasMoreElements(); ct++) {
			RecordTrunk rec = en.nextElement();
			String title = (String) rec.elementAt(0);
			String colname = (String) rec.elementAt(1);
			String coltype = (String) rec.elementAt(2);
			tablevdef.insertColumn(c + ct);

			// 取表头行.设表头
			int ii = tablevdef.getRowByRowtype(BITableV_def.ROWTYPE_HEAD, 0);
			if (ii >= 0) {
				tablevdef.cells[ii][c + ct].setExpr("\"" + title + "\"");
				tablevdef.cells[ii][c + ct].setBold(true);
				tablevdef.cells[ii][c + ct].setAlign(BICell.ALIGN_CENTER);
			}

			// 设置表身
			ii = tablevdef.getRowByRowtype(BITableV_def.ROWTYPE_DATA, 0);
			if (ii >= 0) {
				tablevdef.cells[ii][c + ct].setExpr("{" + colname + "}");
				if (coltype.equals("number")) {
					tablevdef.cells[ii][c + ct].setAlign(BICell.ALIGN_RIGHT);
				} else {
					tablevdef.cells[ii][c + ct].setAlign(BICell.ALIGN_LEFT);
				}
			}

			// 如果是数字,自动生成合计
			if (coltype.equals("number")) {
				ii = tablevdef.getRowByRowtype(BITableV_def.ROWTYPE_FOOT, 0);
				if (ii >= 0) {
					tablevdef.cells[ii][c + ct].setExpr("sum({" + colname
							+ "})");
					tablevdef.cells[ii][c + ct].setBold(true);
					tablevdef.cells[ii][c + ct].setAlign(BICell.ALIGN_RIGHT);
				}
			}

			if (orgcolct == 0 && tablevdef.getColcount() > 0) {
				// 显示合计
				int firstfootrow = tablevdef.getRowByRowtype(
						BITableV_def.ROWTYPE_FOOT, 0);
				if (firstfootrow >= 0) {
					BICell cell = tablevdef.getCells()[firstfootrow][0];
					cell.setExpr("\"合计\"");
					cell.setAlign(BICell.ALIGN_LEFT);
					cell.setBold(true);
				}
			}
		}

		int row = table.getRow();
		bind();
		if (row > 0) {
			table.setRowSelectionInterval(row, row);
		}

	}

	void deleteRow() {
		reverseBind();
		int row = table.getRow();
		if (row < 0)
			return;
		BITableV_def tablevdef = tablevrender.getTablevdef();
		tablevdef.deleteRow(row);
		bind();
		if (row >= 0 && row < table.getRowCount() - 1) {
			table.setRowSelectionInterval(row, row);
		}
	}

	void deleteCol() {
		reverseBind();
		int c = table.getCurcol();
		c--;
		if (c < 0) {
			c = 0;
		}

		BITableV_def tablevdef = tablevrender.getTablevdef();
		tablevdef.deleteColumn(c);
		int row = table.getRow();
		bind();
		if (row > 0) {
			table.setRowSelectionInterval(row, row);
		}
	}

	void init() {
		jsplitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		jsplitpane.setDividerLocation(300);
		add(jsplitpane, BorderLayout.CENTER);

		proptabbedpane = createProppane();
		jsplitpane.setLeftComponent(proptabbedpane);

		// 右边是表格
		BITableV_def tablevdef = tablevrender.getTablevdef();
		DBTableModel dm = createNewdbmodel(1);
		table = new Tabledesign_table(dm, this, tablevdef,
				tablev_transferhandle);
		table.getSelectionModel().setSelectionMode(
				ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setCellSelectionEnabled(true);
		table.getColumnModel().getSelectionModel().setSelectionMode(
				ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setSortable(false);
		// table.setGridColor(table.getBackground());

		JTabbedPane tabbedpane = new JTabbedPane();
		tabbedpane.addChangeListener(new TabbedpaneHandle());
		jsplitpane.setRightComponent(tabbedpane);

		JPanel designpane = new JPanel();
		designpane.setLayout(new BorderLayout());
		// 上部工具条.
		JPanel tb = createToolbar();
		designpane.add(tb, BorderLayout.NORTH);

		designpane.add(new JScrollPane(table), BorderLayout.CENTER);

		JScrollPane jspdesign = new JScrollPane(designpane);
		jspdesign.setPreferredSize(new Dimension(500, 800));
		tabbedpane.add("垂直表列定义", jspdesign);

		previewpane = new Previewpane();
		// JScrollPane jsppreview=new JScrollPane(previewpane);
		// tabbedpane.add("预览", jsppreview);

	}

	class TabbedpaneHandle implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
			JTabbedPane tb = (JTabbedPane) e.getSource();
			int index = tb.getSelectedIndex();
			if (index == 1) {
				previewpane.doDraw();
			}
		}
	}

	JPanel createToolbar() {
		Dimension btnsize = new Dimension(60, 27);
		JPanel tb = new JPanel();
		JButton btn;
		btn = new JButton("增表头");
		btn.setMargin(new Insets(1, 1, 1, 1));
		btn.setPreferredSize(btnsize);
		btn.setActionCommand("增加表头行");
		btn.addActionListener(this);
		tb.add(btn);

		btn = new JButton("增数据");
		btn.setMargin(new Insets(1, 1, 1, 1));
		btn.setPreferredSize(btnsize);
		btn.setActionCommand("增加数据行");
		btn.addActionListener(this);
		tb.add(btn);

		btn = new JButton("增表尾");
		btn.setMargin(new Insets(1, 1, 1, 1));
		btn.setPreferredSize(btnsize);
		btn.setActionCommand("增加表尾行");
		btn.addActionListener(this);
		tb.add(btn);

		btn = new JButton("删行");
		btn.setMargin(new Insets(1, 1, 1, 1));
		btn.setPreferredSize(btnsize);
		btn.setActionCommand("删除当前行");
		btn.addActionListener(this);
		tb.add(btn);

		btn = new JButton("增列");
		btn.setMargin(new Insets(1, 1, 1, 1));
		btn.setPreferredSize(btnsize);
		btn.setActionCommand("增加列");
		btn.addActionListener(this);
		tb.add(btn);

		btn = new JButton("删列");
		btn.setMargin(new Insets(1, 1, 1, 1));
		btn.setPreferredSize(btnsize);
		btn.setActionCommand("删除当前列");
		btn.addActionListener(this);
		tb.add(btn);

		btn = new JButton("分组");
		btn.setMargin(new Insets(1, 1, 1, 1));
		btn.setPreferredSize(btnsize);
		btn.setActionCommand("设置分组");
		btn.addActionListener(this);
		tb.add(btn);

		btnmerge = new JButton("合并");
		btnmerge.setMargin(new Insets(1, 1, 1, 1));
		btnmerge.setPreferredSize(btnsize);
		btnmerge.setActionCommand("合并单元格");
		btnmerge.addActionListener(this);
		tb.add(btnmerge);

		btnCancelmerge = new JButton("取消合并");
		btnCancelmerge.setMargin(new Insets(1, 1, 1, 1));
		btnCancelmerge.setPreferredSize(btnsize);
		btnCancelmerge.setActionCommand("取消合并");
		btnCancelmerge.addActionListener(this);
		tb.add(btnCancelmerge);
		btnCancelmerge.setEnabled(false);

		// btn = new JButton("preview");
		// btn.setActionCommand("preview");
		// btn.addActionListener(this);
		// tb.add(btn);

		return tb;
	}

	public Tabledesign_table getTable() {
		return table;
	}

	JTabbedPane createProppane() {
		JTabbedPane tp = new JTabbedPane();
		columntable = createColumntable();
		// columntable.addRowSelectionInterval(0, columntable.getRowCount() -
		// 1);
		tp.add("数据项", new JScrollPane(columntable));

		proppane = new Proppane(new LinkActionHandler());
		proppane.getLinktable().addMouseListener(new TablemouseHandler());
		DocumentHandle doch = new DocumentHandle();
		Changhandler cl = new Changhandler();
		Itemhandler ih = new Itemhandler();
		proppane.textFontname.addItemListener(ih);
		proppane.textFontsize.addChangeListener(cl);
		proppane.cbBold.addChangeListener(cl);
		proppane.cbItalic.addChangeListener(cl);
		proppane.cbAlign.addItemListener(ih);
		proppane.cbVAlign.addItemListener(ih);
		proppane.cbFormat.addItemListener(ih);
		proppane.cbFixrowheight.addItemListener(ih);
		proppane.textRowheight.addChangeListener(cl);
		proppane.cbFixrowcount.addItemListener(ih);
		proppane.textfixrowcount.addChangeListener(cl);

		tp.add("属性", new JScrollPane(proppane));
		return tp;
	}

	DBTableModel createNewdbmodel(int colct) {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col = new DBColumnDisplayInfo("edit_area", "varchar", "");
		cols.add(col);
		col.setReadonly(true);

		for (int c = 0; c < colct; c++) {
			col = new DBColumnDisplayInfo("c_" + c, "varchar", "");
			cols.add(col);
		}

		DBTableModel dm = new DBTableModel(cols);
		/*
		 * dm.appendRow(); dm.appendRow(); dm.appendRow();
		 * 
		 * dm.setItemValue(0, "edit_area", "表头"); dm.setItemValue(1,
		 * "edit_area", "表身"); dm.setItemValue(2, "edit_area", "表尾");
		 */
		return dm;
	}

	/**
	 * 数据项表格
	 * 
	 * @return
	 */
	public ColumndragTable createColumntable() {
		DBTableModel dm = frm.createColdefdm();
		return new ColumndragTable(dm);
	}

	public void refreshColumndef() {
		DBTableModel dm = frm.createColdefdm();
		columntable.setModel(dm);
		columntable.getSelectionModel().setSelectionInterval(0,
				columntable.getRowCount() - 1);
	}

	void setupGroup() {
		Vector<String> groupcols = new Vector<String>();
		BITableV_def tablevdef = tablevrender.getTablevdef();
		Enumeration<SplitGroupInfo> en = tablevdef.getGroupinfos().elements();
		while (en.hasMoreElements()) {
			SplitGroupInfo ginfo = en.nextElement();
			groupcols.add(ginfo.getGroupcolumn());
		}
		GroupsetupDlg dlg = new GroupsetupDlg(this, groupcols);
		dlg.pack();
		dlg.setVisible(true);
		if (!dlg.isOk()) {
			return;
		}

		// 设置分组.
		Vector<SplitGroupInfo> newgroupinfos = new Vector<SplitGroupInfo>();
		Enumeration<String> encol = dlg.getGroupcols().elements();
		while (encol.hasMoreElements()) {
			String groupcolname = encol.nextElement();

			// 是否定义过了?
			en = tablevdef.getGroupinfos().elements();
			boolean found = false;
			while (en.hasMoreElements()) {
				SplitGroupInfo ginfo = en.nextElement();
				if (ginfo.getGroupcolumn().equalsIgnoreCase(groupcolname)) {
					ginfo.setLevel(newgroupinfos.size());
					newgroupinfos.add(ginfo);
					found = true;
					break;
				}
			}
			if (found) {
				continue;
			}

			// 如果没有定义过,现在产生
			SplitGroupInfo ginfo = new SplitGroupInfo();
			ginfo.setLevel(newgroupinfos.size());
			newgroupinfos.add(ginfo);
			ginfo.addGroupColumn(groupcolname);
			DBTableModel dm = (DBTableModel) columntable.getModel();
			int sr = dm.searchColumnvalue("colname", groupcolname);
			if (sr >= 0) {
				String title = dm.getItemValue(sr, "title");
				ginfo.setTitle(title + "分组小计");
			}

			// 所有数字类型的,都求和.
			for (int i = 0; i < dm.getRowCount(); i++) {
				String colname = dm.getItemValue(i, "colname");
				String coltype = dm.getItemValue(i, "coltype");
				if (colname.equalsIgnoreCase(groupcolname))
					continue;
				if (!coltype.equals("number"))
					continue;
				ginfo.addDataColumn(colname, "sum");
			}

		}
		tablevdef.setGroupinfos(newgroupinfos);
		tablevdef.resetGrouprows();
		tablevdef.fireDefinechanged();
		bind();
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("增加表头行")) {
			newHeadline();
		} else if (cmd.equals("增加数据行")) {
			newDataline();
		} else if (cmd.equals("增加表尾行")) {
			newFootline();
		} else if (cmd.equals("增加列")) {
			newColumn();
		} else if (cmd.equals("设置分组")) {
			setupGroup();
		} else if (cmd.equals("删除当前行")) {
			deleteRow();
		} else if (cmd.equals("删除当前列")) {
			deleteCol();
		} else if (cmd.equals("合并单元格")) {
			domergerCells();
		} else if (cmd.equals("取消合并")) {
			cancelmergerCells();
		} else if (cmd.equals("preview")) {
			previewpane.doDraw();
		}
	}

	/**
	 * 合并单元格.
	 */
	void domergerCells() {
		// 只能是同一区的才能合并.如表头区
		table.stopEdit();
		int row1 = table.getSelectionModel().getMinSelectionIndex();
		int row2 = table.getSelectionModel().getMaxSelectionIndex();

		int col1 = table.getColumnModel().getSelectionModel()
				.getMinSelectionIndex();
		int col2 = table.getColumnModel().getSelectionModel()
				.getMaxSelectionIndex();

		if (row1 < 0 || col1 < 0 || row1 == row2 && col1 == col2) {
			frm.warnMessage("提示", "按Ctrl键用鼠标多选单元格后再选择合并");
			return;
		}
		
		//只支持同一区.
		int memrowtype=tablevrender.tablevdef.getRowtypes()[row1];
		for(int r=row1+1;r<=row2;r++){
			int rowtype=tablevrender.tablevdef.getRowtypes()[r];
			if(rowtype!=memrowtype){
				frm.warnMessage("提示", "只能同一区域才能合并,如都是表头,都是表尾");
				return;
			}
		}

		tablevrender.getTablevdef().addMerge(row1, row2 - row1 + 1, col1-1,
				col2 - col1 + 1);
		table.repaint();
	}

	void cancelmergerCells() {
		table.stopEdit();
		int r=table.getRow();
		int c=table.getCurcol()-1;
		
		for(int i=0;i<tablevrender.tablevdef.getMergeinfos().size();i++){
			Mergeinfo minfo=tablevrender.tablevdef.getMergeinfos().elementAt(i);
			if(minfo.startrow<=r && minfo.startcolumn<=c&&
					r<minfo.startrow+minfo.rowcount&&
					c<minfo.startcolumn+minfo.columncount){
				tablevrender.tablevdef.getMergeinfos().remove(i);
				i--;
			}
		}
		table.repaint();
	}
	
	
	public void doubleclickItem(int row, String colname) {
		table.stopEdit();
		// 编辑
		DBTableModel dm = (DBTableModel) table.getModel();
		String expr = dm.getItemValue(row, colname);
		BICell cell = new BICell();
		cell.setExpr(expr);
		CellExprDlg dlg = new CellExprDlg(frm, this.createColumntable(), cell
				.getExpr(), frm.dsdefine);
		dlg.pack();
		dlg.setVisible(true);
		if (!dlg.isOk())
			return;
		expr = dlg.getExpr();
		dm.setItemValue(row, colname, expr);
		table.tableChanged(new TableModelEvent(dm, row));
		reverseBind();
	}

	public void editCellAt(int row, String colname) {
		bindProp(row, colname);
		proptabbedpane.setSelectedIndex(1);
	}

	void bindProp(int row, String colname) {
		int mindex = 0;
		DBTableModel tablevdm = (DBTableModel) table.getModel();
		Vector<DBColumnDisplayInfo> cols = tablevdm.getDisplaycolumninfos();
		for (; mindex < cols.size(); mindex++) {
			if (colname.equals(cols.elementAt(mindex).getColname())) {
				break;
			}
		}
		int tablecolno = table.convertColumnIndexToView(mindex);
		BITableV_def tablevdef = tablevrender.getTablevdef();
		BICell cell = tablevdef.getCells()[row][tablecolno - 1];
		editingcell = null;

		proppane.textFontname.setSelectedItem(cell.getFontname());
		proppane.textFontsize.setValue(new Integer(cell.getFontsize()));
		proppane.cbBold.setSelected(cell.isBold());
		proppane.cbItalic.setSelected(cell.isItalic());
		int sel = 0;
		if (cell.getAlign() == BICell.ALIGN_LEFT) {
			sel = 0;
		} else if (cell.getAlign() == BICell.ALIGN_CENTER) {
			sel = 1;
		} else if (cell.getAlign() == BICell.ALIGN_RIGHT) {
			sel = 2;
		}
		proppane.cbAlign.setSelectedIndex(sel);

		sel = 0;
		if (cell.getValign() == BICell.ALIGN_NORTH) {
			sel = 0;
		} else if (cell.getValign() == BICell.ALIGN_CENTER) {
			sel = 1;
		} else if (cell.getValign() == BICell.ALIGN_SOUTH) {
			sel = 2;
		}
		proppane.cbVAlign.setSelectedIndex(sel);

		proppane.cbFormat.setSelectedItem((String) cell.getFormat());

		int rowheight = tablevdef.getRowheights()[row];
		proppane.textRowheight.setValue(new Integer(rowheight));

		proppane.cbFixrowheight.setSelected(rowheight > 0);

		int fixrowct = tablevdef.getFixrowcountperpage();
		proppane.cbFixrowcount.setSelected(fixrowct > 0);
		proppane.textfixrowcount.setValue(new Integer(fixrowct));
		
		proppane.setLinkinfo(cell);

		editingcell = cell;
		editingcol = mindex - 1;
		editingrow = row;
	}

	void comppropApply() {
		if (editingcell == null)
			return;
		boolean changed = false;
		String fontname = editingcell.getFontname();
		String newfontname = (String) proppane.textFontname.getSelectedItem();
		if (!fontname.equals(newfontname)) {
			editingcell.setFontname(newfontname);
			changed = true;

		}

		int fontsize = editingcell.getFontsize();
		try {
			int newfontsize = ((Integer) proppane.textFontsize.getValue())
					.intValue();
			if (newfontsize != fontsize) {
				editingcell.setFontsize(newfontsize);
				changed = true;
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}

		boolean bold = editingcell.isBold();
		if (bold != proppane.cbBold.isSelected()) {
			changed = true;
			editingcell.setBold(proppane.cbBold.isSelected());
		}
		boolean italic = editingcell.isItalic();
		if (italic != proppane.cbItalic.isSelected()) {
			changed = true;
			editingcell.setItalic(proppane.cbItalic.isSelected());
		}

		int newalign = 0;
		int sel = proppane.cbAlign.getSelectedIndex();
		if (sel == 0) {
			newalign = BICell.ALIGN_LEFT;
		} else if (sel == 1) {
			newalign = BICell.ALIGN_CENTER;
		} else if (sel == 2) {
			newalign = BICell.ALIGN_RIGHT;
		}
		if (newalign != editingcell.getAlign()) {
			changed = true;
			editingcell.setAlign(newalign);
		}

		int newvalign = 0;
		sel = proppane.cbVAlign.getSelectedIndex();
		if (sel == 0) {
			newvalign = BICell.ALIGN_NORTH;
		} else if (sel == 1) {
			newvalign = BICell.ALIGN_CENTER;
		} else if (sel == 2) {
			newvalign = BICell.ALIGN_SOUTH;
		}
		if (newvalign != editingcell.getValign()) {
			changed = true;
			editingcell.setValign(newvalign);
		}

		String newformat = (String) proppane.cbFormat.getSelectedItem();
		if (!newformat.equals(editingcell.getFormat())) {
			changed = true;
			editingcell.setFormat(newformat);
		}

		BITableV_def tablevdef = tablevrender.getTablevdef();
		int rowheight = tablevdef.getRowheights()[editingrow];
		int newrowheight = rowheight;
		if (proppane.cbFixrowheight.isSelected()) {
			try {
				newrowheight = ((Integer) proppane.textRowheight.getValue())
						.intValue();
			} catch (Exception e) {
			}
		} else {
			newrowheight = 0;
		}

		if (newrowheight != rowheight) {
			changed = true;
			tablevdef.getRowheights()[editingrow] = newrowheight;
			table.stopEdit();
			if (newrowheight > 0)
				table.setRowHeight(editingrow, newrowheight);
		}

		int fixrowcount = tablevdef.getFixrowcountperpage();
		int newfixrowcount = fixrowcount;
		if (proppane.cbFixrowcount.isSelected()) {
			newfixrowcount = ((Integer) proppane.textfixrowcount.getValue())
					.intValue();
		} else {
			newfixrowcount = 0;
		}
		if (fixrowcount != newfixrowcount) {
			changed = true;
			tablevdef.setFixrowsperpage(newfixrowcount);
		}

		if (changed) {
			table.stopEdit();
			tablevdef.fireDefinechanged();
			table.repaint();
		}
	}

	class DocumentHandle implements DocumentListener {

		public void changedUpdate(DocumentEvent e) {
			comppropApply();
		}

		public void insertUpdate(DocumentEvent e) {
			comppropApply();
		}

		public void removeUpdate(DocumentEvent e) {
			comppropApply();
		}

	}

	class Changhandler implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
			comppropApply();
		}

	}

	class Itemhandler implements ItemListener {

		public void itemStateChanged(ItemEvent e) {
			comppropApply();
		}
	}

	class Previewpane extends JPanel {
		PreviewcanvasPane canvaspane;

		public Previewpane() {
			canvaspane = new PreviewcanvasPane(Tablevdesignpane.this);
			setLayout(new BorderLayout());
			add(new Toolbar(), BorderLayout.NORTH);
			add(canvaspane, BorderLayout.CENTER);
		}

		public void doDraw() {
			int pageno = 1;
			try {
				pageno = ((Integer) textPageno.getValue()).intValue();
			} catch (Exception e) {
				return;
			}
			BufferedImage img = preview(pageno - 1);
			canvaspane.setImg(img);
			canvaspane.repaint();
		}

		JSpinner textPageno;
		JLabel lbpageinfo;

		class Toolbar extends JPanel {
			Toolbar() {
				lbpageinfo = new JLabel("页数");
				add(lbpageinfo);
				SpinnerNumberModel spmodel = new SpinnerNumberModel(
						new Integer(1), new Integer(1), new Integer(1),
						new Integer(1));
				textPageno = new JSpinner(spmodel);

				add(textPageno);
				JButton btn = new JButton("跳转");
				btn.setActionCommand("preview");
				btn.addActionListener(Tablevdesignpane.this);
				add(btn);
			}
		}

		public BufferedImage preview(int pageno) {
			reverseBind();

			TestdataFactory tdf = new TestdataFactory();
			int pagecount = tablevrender.getPagecount();
			lbpageinfo.setText("共" + pagecount + "页");
			SpinnerNumberModel spmodel = new SpinnerNumberModel(
					(Integer) textPageno.getValue(), new Integer(1),
					new Integer(pagecount), new Integer(1));
			textPageno.setModel(spmodel);

			BufferedImage img = new BufferedImage(640, 1000,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = (Graphics2D) img.getGraphics();
			g2.setColor(Color.white);
			g2.fillRect(0, 0, 640, 1000);

			tablevrender.draw(g2, pageno);
			return img;

		}

	}

	public void reset() {
		columntable.setModel(frm.createColdefdm());
	}

	public void on_Focuscell() {
		int r=table.getRow();
		int c=table.getCurcol();
		int m=tablevrender.tablevdef.isMergecell(r, c-1);
		if(m==1 || m==2){
			btnCancelmerge.setEnabled(true);
			btnmerge.setEnabled(false);
		}else{
			btnCancelmerge.setEnabled(false);
			btnmerge.setEnabled(true);
		}
	}

	void addLink(){
		if(editingcell==null){
			return;
		}
		LinksetupDlg dlg=new LinksetupDlg(frm,tablevrender.getDbmodel(),tablevrender.getDsdefine().params);
		dlg.pack();
		dlg.setVisible(true);
		if(!dlg.isOk())return;
		
		String linkname=dlg.getLinkname();
		String callopid=dlg.getCallopid();
		String callopname=dlg.getCallopname();
		String cond=dlg.getCallcond();
		
		proppane.addLink(linkname,callopid,callopname,cond);
		editingcell.addLink(linkname,callopid,callopname,cond);
		
	}
	
	void delLink(){
		if(editingcell==null){
			return;
		}
		int row=proppane.getLinktable().getRow();
		if(row<0){
			CMessageDialog.warnMessage(frm, "提示","请选中一个链接再删除");
			return;
		}

		DBTableModel dm=(DBTableModel) proppane.getLinktable().getModel();
		dm.removeRow(row);
		proppane.getLinktable().tableChanged(new TableModelEvent(proppane.getLinktable().getModel()));
		
		editingcell.getLinkinfos().remove(row);
	}
	
	void modifyLink(){
		if(editingcell==null){
			return;
		}
		int row=proppane.getLinktable().getRow();
		if(row<0){
			CMessageDialog.warnMessage(frm, "提示","请选中一个链接再修改");
			return;
		}
		DBTableModel dm=(DBTableModel) proppane.getLinktable().getModel();
		
		LinksetupDlg dlg=new LinksetupDlg(frm,tablevrender.getDbmodel(),tablevrender.getDsdefine().params);
		dlg.pack();
		dlg.setLinkname(dm.getItemValue(row, "linkname"));
		dlg.setCallopid(dm.getItemValue(row, "callopid"));
		dlg.setCallopname(dm.getItemValue(row, "callopname"));
		dlg.setCallcond(dm.getItemValue(row, "callcond"));
		dlg.setVisible(true);
		if(!dlg.isOk())return;
		
		String linkname=dlg.getLinkname();
		String callopid=dlg.getCallopid();
		String callopname=dlg.getCallopname();
		String callcond=dlg.getCallcond();
		dm.setItemValue(row,"linkname",linkname);
		dm.setItemValue(row,"callopid",callopid);
		dm.setItemValue(row,"callopname",callopname);
		dm.setItemValue(row,"callcond",callcond);
		
		Linkinfo linkinfo=editingcell.getLinkinfos().elementAt(row);
		linkinfo.setLinkname(linkname);
		linkinfo.setCallopid(callopid);
		linkinfo.setCallopname(callopname);
		linkinfo.setCallcond(callcond);
		
	}
	
	class LinkActionHandler implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("addlink")){
				addLink();
			}else if(e.getActionCommand().equals("dellink")){
				delLink();
			}else if(e.getActionCommand().equals("modifylink")){
				modifyLink();
			}
		}
	}

	class TablemouseHandler implements MouseListener{

		public void mouseClicked(MouseEvent e) {
			if(e.getClickCount()>1){
				if(table.getRow()>=0){
					modifyLink();
				}
			}
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}
		
	}
}
