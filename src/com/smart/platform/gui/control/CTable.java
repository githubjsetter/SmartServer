package com.smart.platform.gui.control;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import oracle.jdbc.dbaccess.DBStatement;

import org.apache.log4j.Category;

import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.gui.ste.CSteModel;


/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-3-28 Time: 10:53:45
 * To change this template use File | Settings | File Templates.
 */
public class CTable extends JTable {

	Category logger = Category.getInstance(CTable.class);

	protected boolean readonly = false;

	protected int currow = -1;
	protected int curcol = -1;

	/**
	 * 自动设置列宽,最大的检查行数
	 */
	static int AUTOSIZE_CHECKROWCOUNT = 100;

	/**
	 * 是否充许排序
	 */
	boolean sortable = true;

	/**
	 * 排序监听
	 */
	private Vector<DbtablemodelSortListener> sortlistener = new Vector<DbtablemodelSortListener>();
	
	int tableheadheight=30;
	

	public int getCurcol() {
		return curcol;
	}

	/**
	 * Default Constructor
	 */
	public CTable() {
		// BasicTableUI
		super(new DefaultTableModel());
		inittable();
	} // CTable

	public CTable(TableModel dm) {
		super(dm);
		inittable();
	}

	public CTable(TableModel dm, TableColumnModel cm) {
		super(dm, cm);
		inittable();
	}

	private void inittable() {
		setColumnSelectionAllowed(false);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setSurrendersFocusOnKeystroke(true);

		// super.getAccessibleContext().addPropertyChangeListener(new
		// TablechangeListener());
		JTableHeader tableHeader = this.getTableHeader();
		// TableCellRenderer defaultRenderer = tableHeader.getDefaultRenderer();

		tableHeader.setDefaultRenderer(new CTableHeaderRender());
		this.setRowHeight(27);

		this.addFocusListener(new CTablefocusListener());

		this.getSelectionModel().addListSelectionListener(
				new Tableselectionlistener());
		

	}
	
	

	public boolean editCellAt(int row, int column) {
		// 最后一行是合计行,不能编辑 by wwh 20070817
		if (row == this.getRowCount() - 1)
			return false;
		getSelectionModel().addSelectionInterval(row, row);
		getColumnModel().getSelectionModel().setSelectionInterval(column,
				column);
		return super.editCellAt(row, column);
	}

	public boolean editCellAt(int row, String colname) {
		DBTableModel dbmodel = (DBTableModel) getModel();
		int modelindex = dbmodel.getColumnindex(colname);
		if (modelindex < 0)
			return false;
		int viewcolindex=convertColumnIndexToView(modelindex);
		return editCellAt(row,viewcolindex);
	}

	public boolean isQuerying() {
		if (getModel() instanceof DBTableModel) {
			return ((DBTableModel) getModel()).isquerying();
		}
		return false;
	}

	@Override
	public Object getValueAt(int row, int column) {
		if (getModel() instanceof DBTableModel) {
			int mi = getColumnModel().getColumn(column).getModelIndex();
			return ((DBTableModel) getModel()).getItemValue(row, mi);
		} else {
			return super.getValueAt(row, column);
		}
	}

	/**
	 * Sizing: making sure it fits in a column
	 */
	private final int SLACK = 15;
	/**
	 * Sizing: max size in pt
	 */
	private final int MAXSIZE = 600;

	/**
	 * Logger
	 */
	private static Category log = Category.getInstance(CTable.class);

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		if (readonly)
			return false;
		int modelindex = convertColumnIndexToModel(column);
		DBTableModel dbmodel = (DBTableModel) getModel();
		DBColumnDisplayInfo colinfo = dbmodel.getDisplaycolumninfos()
				.elementAt(modelindex);
		String colname = colinfo.getColname();
		if (!(isCellEditable(row, colname)))
			return false;
		return super.isCellEditable(row, column);
	}

	/**
	 * Get Selected Value or null
	 * 
	 * @return value
	 */
	public Object getSelectedValue() {
		int row = getSelectedRow();
		int col = getSelectedColumn();
		if (row == -1 || col == -1)
			return null;
		return getValueAt(row, col);
	} // getSelectedValue

	/**
	 * Stop Table Editors and remove focus
	 * 
	 * @param saveValue
	 *            save value
	 */
	/*
	 * public void stopEditor(boolean saveValue) { // MultiRow - remove editors
	 * ChangeEvent ce = new ChangeEvent(this); if (saveValue)
	 * editingStopped(ce); else editingCanceled(ce); // if (getInputContext() !=
	 * null) getInputContext().endComposition(); // change focus to next
	 * transferFocus(); } // stopEditor
	 * 
	 */

	public void stopEdit() {
		TableCellEditor tce = this.getCellEditor();
		if (tce != null) {
			tce.stopCellEditing();
		}
	}

	/**
	 * ***********************************************************************
	 * Size Columns.
	 */
	public void autoSize() {
		// logger.debug("begin table autoSize()");
		DBTableModel model = (DBTableModel) this.getModel();
		// logger.debug("autoSize(): got model=" + model);
		int size = this.getColumnCount();
		// logger.debug("autoSize(): size=" + size);
		// for all columns
		JLabel lbcomp = new JLabel();
		lbcomp.setFont(this.getFont());

		for (int c = 0; c < size; c++) {
			TableColumn column = getColumnModel().getColumn(c);
			int modelindex = column.getModelIndex();
			// logger.debug("autoSize(): c=" + c + ",column=" + column);

			// 求标题宽
			int width = 0;
			// logger.debug("autoSize():
			// renderer.getTableCellRendererComponent="
			// + comp);
			String headv = (String) column.getHeaderValue();
			lbcomp.setText(headv);
			width = (int) lbcomp.getPreferredSize().getWidth();
			// logger.debug("autoSize():for header
			// comp.getPreferredSize().getWidth()="
			// + width);
			DBColumnDisplayInfo colinfo = model.getDisplaycolumninfos()
					.elementAt(modelindex);
			if (colinfo.getColtype().equals("行号")) {
				column.setPreferredWidth(60);
				continue;
			}
			
			if(colinfo.getTablecolumnwidth()>=0){
				column.setPreferredWidth(colinfo.getTablecolumnwidth());
				continue;
			}
			
			headv = colinfo.getTitle();
			lbcomp.setText(headv);
			width = (int) lbcomp.getPreferredSize().getWidth();

			int maxRow = Math.min(AUTOSIZE_CHECKROWCOUNT, getRowCount());
			// logger.debug("autoSize(): maxRow=" + maxRow);

			// 求一行最大值的值
			int maxvlength = 0;
			for (int row = 0; row < maxRow; row++) {
				String v = model.getItemValue(row, modelindex);
				if (colinfo.getEditcomptype().equals(
						DBColumnDisplayInfo.EDITCOMP_COMBOBOX)) {
					String cbv = colinfo.getComboboxValue(v);
					if (cbv != null) {
						v = cbv;
					}
				}
				if (v.length() > maxvlength) {
					maxvlength = v.length();
					lbcomp.setText(v);
					int rowWidth = lbcomp.getPreferredSize().width;
					// logger.debug("autoSize():row=" + row + ",rowWidth=" +
					// rowWidth);
					width = Math.max(rowWidth, width);
					// logger.debug("autoSize():row=" + row + ",final rowWidth="
					// + rowWidth);
				}
			}

			// logger.debug("autoSize():finish row loop");
			width = Math.min(MAXSIZE, width + SLACK);
			// logger.debug("autoSize():end point width=" + width);
			column.setPreferredWidth(width);
			// logger.debug("autoSize():finish
			// column.setPreferredWidth(),width="
			// + width);
		}
		// logger.debug("autoSize():finished");
	}

	/**
	 * Sort Table
	 * 
	 * @param modelColumnIndex
	 *            model column sort index
	 */
	public void sort(int modelColumnIndex, String sortmethod) {
		TableModel model = this.getModel();
		if (model instanceof Sumdbmodel) {
			Sumdbmodel summodel = (Sumdbmodel) model;
			summodel.getDbmodel().sort(modelColumnIndex,
					sortmethod.equalsIgnoreCase("A"));
			summodel.fireDatachanged();
		} else if (model instanceof DBTableModel) {
			((DBTableModel) model).sort(modelColumnIndex, sortmethod
					.equalsIgnoreCase("A"));
		}

		this.tableChanged(new TableModelEvent(model));
		fireSorted();
	} // sort

	/**
	 * String Representation
	 * 
	 * @return info
	 */
	public String toString() {
		// BasicTableUI
		return new StringBuffer("CTable[").append(getModel()).append("]")
				.toString();
	} // toString

	/*
	 * protected void processMouseEvent(MouseEvent e) { System.out.println(e);
	 * 
	 * super.processMouseEvent(e);
	 * 
	 * if(e.getID() == MouseEvent.MOUSE_PRESSED){ int col =
	 * this.getColumnModel().getColumnIndexAtX(e.getX()); col =
	 * convertColumnIndexToModel(col); //int row=this.getSelectedRow();
	 * 
	 * System.out.println("col="+col); } }
	 */

	public void valueChanged(ListSelectionEvent e) {
		super.valueChanged(e); // To change body of overridden methods use File
		// | Settings | File Templates.
	}

	public void redraw() {
		this.invalidate();
		repaint();
	}

	protected int sortcolumnindex = -1;
	protected String sortmethod = "A";

	public Category getLogger() {
		return logger;
	}

	public void setLogger(Category logger) {
		this.logger = logger;
	}

	public int getSortcolumnindex() {
		return sortcolumnindex;
	}

	public void setSortcolumnindex(int sortcolumnindex) {
		this.sortcolumnindex = sortcolumnindex;
	}

	public String getSortmethod() {
		return sortmethod;
	}

	public void setSortmethod(String sortmethod) {
		this.sortmethod = sortmethod;
	}

	protected JTableHeader createDefaultTableHeader() {
		return new SetheightTableheader(columnModel);
	}

	class SetheightTableheader extends JTableHeader {
		public SetheightTableheader(TableColumnModel cm) {
			super(cm);
		}

		protected void processMouseEvent(MouseEvent e) {
			super.processMouseEvent(e); // To change body of overridden methods
			// use File | Settings | File Templates.
			if (MouseEvent.MOUSE_PRESSED == e.getID() && sortable && e.getClickCount()>1) {
				// 不要使用JTable.columnAtPoint().自已算,点中间才行
				double x = 0;
				TableColumnModel colmodel = CTable.this.getColumnModel();
				double mx = e.getPoint().getX();
				int col = -1;
				for (int i = 0; i < colmodel.getColumnCount(); i++) {
					TableColumn column = colmodel.getColumn(i);
					double w = column.getWidth();
					double x1 = x + w;
					double rang = w * 0.2;
					if (mx >= x + rang && mx <= x1 - rang) {
						col = i;
						break;
					}
					x = x1;
				}
				if (col >= 1) {
					// 转换为model index
					int modelcol = this.getColumnModel().getColumn(col)
							.getModelIndex();
					int oldcol = CTable.this.getSortcolumnindex();
					if (oldcol == col) {
						if (getSortmethod().equalsIgnoreCase("A")) {
							setSortmethod("D");
							sort(modelcol, "D");
						} else {
							setSortmethod("A");
							sort(modelcol, "A");
						}
					} else {
						setSortcolumnindex(col);
						setSortmethod("A");
						sort(modelcol, "A");
					}
				}
				fireSorted();
			}
		}

		public Dimension getPreferredSize() {
			Dimension size = super.getPreferredSize();
			return new Dimension((int) size.getWidth(), tableheadheight);
		}

	}

	
	class CTablefocusListener implements FocusListener {

		public void focusGained(FocusEvent e) {
			CTable.this.getTableHeader().repaint();
		}

		public void focusLost(FocusEvent e) {
			CTable.this.getTableHeader().repaint();
		}

	}

	public void freeMemory() {
		TableModel tm = getModel();
		if (tm instanceof DBTableModel) {
			DBTableModel dbmodel = (DBTableModel) tm;
			dbmodel.clearAll();
			this.tableChanged(new TableModelEvent(dbmodel));
			dbmodel.freeMemory();
		}

		TableColumnModel cm = this.getColumnModel();
		while (cm.getColumnCount() > 0) {
			TableColumn tc = cm.getColumn(0);
			TableCellRenderer hr = tc.getHeaderRenderer();
			if (hr instanceof CTableHeaderRender) {
				CTableHeaderRender thr = (CTableHeaderRender) hr;
				thr.freeMemory();
			}
			tc.setHeaderRenderer(null);
			tc.setCellRenderer(null);
			
			tc.setCellRenderer(null);

			
			cm.removeColumn(tc);
		}
	}

	boolean disablemouseevent = false;

	public boolean isDisablemouseevent() {
		return disablemouseevent;
	}

	public void setDisablemouseevent(boolean disablemouseevent) {
		this.disablemouseevent = disablemouseevent;
	}

	@Override
	protected void processMouseEvent(MouseEvent e) {
		if (disablemouseevent) {
			return;
		}
		// TODO Auto-generated method stub
		super.processMouseEvent(e);
	}

	public void scrollToCell(int row, int tablecolno) {
		this.scrollRectToVisible(getCellRect(row, tablecolno, true));
	}

	/**
	 * @deprecated
	 * @param row
	 * @param tablecolno
	 * @param tablescrollpane
	 */
	public void scrollToCell(int row, int tablecolno,
			JScrollPane tablescrollpane) {
		if (tablescrollpane == null) {
			return;
		}
		Dimension viewportsize = tablescrollpane.getViewport().getSize();
		JScrollBar hsb = tablescrollpane.getHorizontalScrollBar();
		double max = hsb.getMaximum();
		double startx = hsb.getValue();

		int rectx = hsb.getValue();

		boolean needscroll = false;
		// 计算当前列是不是可见
		int width = 0;
		for (int i = 0; i < tablecolno; i++) {
			width += getColumnModel().getColumn(i).getWidth();
		}
		int thiswidth = getColumnModel().getColumn(tablecolno).getWidth();

		if (width < startx) {
			rectx = width;
			needscroll = true;
		} else if (width + thiswidth > startx + viewportsize.getWidth()) {
			rectx = width + thiswidth - (int) viewportsize.getWidth();
			needscroll = true;
		}

		// 计算当前行是不是可见
		JScrollBar vsb = tablescrollpane.getVerticalScrollBar();
		int recty = vsb.getValue();
		max = vsb.getMaximum();
		double rowh = max / getRowCount();
		double starty = vsb.getValue();
		int rowsperpage = (int) (viewportsize.getHeight() / rowh);
		int firstvisiblerow = (int) (starty / rowh);
		if (row - firstvisiblerow > rowsperpage || row - firstvisiblerow < 0) {
			recty = (int) (rowh * row);
			needscroll = true;
		}

		if (needscroll) {
			Rectangle r = new Rectangle(rectx, recty, (int) viewportsize
					.getWidth(), (int) viewportsize.getHeight());
			scrollRectToVisible(r);
		}

	}

	/**
	 * 如果是多选,强制toggle为true
	 * 
	 * @Override public void changeSelection(int rowIndex, int columnIndex,
	 *           boolean controldown, boolean shiftdown) {
	 *           System.out.println("row="+rowIndex+",controldown="+controldown+",shiftdown="+shiftdown);
	 *           if(getSelectionModel().getSelectionMode()==ListSelectionModel.MULTIPLE_INTERVAL_SELECTION){
	 *           if(!controldown && !shiftdown )return;
	 *           super.changeSelection(rowIndex, columnIndex, controldown,
	 *           shiftdown); }else{ super.changeSelection(rowIndex, columnIndex,
	 *           controldown, shiftdown); } }
	 */

	public int getRow() {
		return currow;
	}

	/**
	 * 取行号所在的table列
	 * 
	 * @return
	 */
	int getLinenoColumn() {
		TableModel tm = getModel();
		if (!(tm instanceof DBTableModel))
			return -1;
		DBTableModel dbmodel = (DBTableModel) tm;

		TableColumnModel tcm = this.getColumnModel();
		for (int i = 0; i < tcm.getColumnCount(); i++) {
			TableColumn tc = tcm.getColumn(i);
			if (tc == null)
				return -1;
			if (dbmodel.getDisplaycolumninfos() == null)
				return -1;
			DBColumnDisplayInfo col = null;
			try {
				col = dbmodel.getDisplaycolumninfos().elementAt(
						tc.getModelIndex());
			} catch (Exception e) {
				return -1;
			}
			if (col.getColtype().equals("行号")) {
				return i;
			}
		}
		return -1;
	}

	void repaint(int row, int col) {
		this.repaint(getCellRect(row, col, true));
	}

	class Tableselectionlistener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			// 当前选中的行是
			if (e.getValueIsAdjusting())
				return;

			DefaultListSelectionModel dm = (DefaultListSelectionModel) e
					.getSource();
			int oldrow = currow;
			currow = dm.getAnchorSelectionIndex();

			// 重画部分
			// 找行号.
			int linenocolumn = getLinenoColumn();

			if (oldrow >= 0 && linenocolumn >= 0) {
				repaint(oldrow, linenocolumn);
			}
			if (currow >= 0 && linenocolumn >= 0) {
				repaint(currow, 0);
			}
		}
	}

	/**
	 * 确认
	 * 
	 * @return true:编辑结束. false:输辑的内容不合法,不能结束
	 */
	public boolean confirm() {
		TableCellEditor tce = getCellEditor();
		if (tce == null)
			return true;
		return tce.stopCellEditing();
	}

	@Override
	public void setSelectionMode(int selectionMode) {
		// TODO Auto-generated method stub
		super.setSelectionMode(selectionMode);
	}

	public boolean isSortable() {
		return sortable;
	}

	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}

	public void addSortlistener(DbtablemodelSortListener sl) {
		this.sortlistener.add(sl);
	}

	void fireSorted() {
		Enumeration<DbtablemodelSortListener> en = sortlistener.elements();
		while (en.hasMoreElements()) {
			en.nextElement().sorted();
		}
	}

	/**
	 * 是否可编辑。
	 * 
	 * @param row
	 *            行
	 * @param column
	 *            列名
	 * @return
	 */
	public boolean isCellEditable(int row, String column) {
		return true;
	}

	/**
	 * 只返回记录的，不要返回合计行
	 */
	@Override
	public int[] getSelectedRows() {
		ArrayList<Integer> ar = new ArrayList<Integer>();
		int rows[] = super.getSelectedRows();
		DBTableModel dbmodel = (DBTableModel) getModel();
		for (int i = 0; i < rows.length; i++) {
			int row = rows[i];
			RecordTrunk rc = dbmodel.getRecordThunk(row);
			if (RecordTrunk.SUMFLAG_RECORD != rc.getSumflag()) {
				continue;
			}
			ar.add(new Integer(row));
		}
		int newrows[] = new int[ar.size()];
		for (int i = 0; i < ar.size(); i++) {
			Integer ii = ar.get(i);
			newrows[i] = ii.intValue();
		}
		return newrows;
	}

	@Override
	public void changeSelection(int rowIndex, int columnIndex, boolean toggle,
			boolean extend) {
		DBTableModel dbmodel = (DBTableModel) getModel();
		RecordTrunk rc = dbmodel.getRecordThunk(rowIndex);
		if (RecordTrunk.SUMFLAG_RECORD != rc.getSumflag()) {
			return;
		}
		super.changeSelection(rowIndex, columnIndex, toggle, extend);
	}

	boolean tablechanging=false;
	@Override
	public void tableChanged(TableModelEvent e) {
		try{
			tablechanging=true;
			super.tableChanged(e);
		}finally{
			tablechanging=false;
		}
	}
	
	/**
	 * 是否调用了JTable.tableChanged(TableModelEvent e)引起的当前行变化
	 * @return
	 */
	public boolean isTablechanging(){
		return tablechanging;
	}

	public int getTableheadheight() {
		return tableheadheight;
	}

	public void setTableheadheight(int tableheadheight) {
		this.tableheadheight = tableheadheight;
	}
	
	String searchText="";
	public int search(int row){
		Window parent=SwingUtilities.getWindowAncestor(this);
		String s=JOptionPane.showInputDialog(parent, "请输入要搜索的文本", searchText);
		if(s==null || s.length()==0){
			return -1;
		}
		searchText=s;
		return search(row,searchText);
	}

	TableSearcher dmsearcher=null;
	public int search(int startrow,String text){
		if(dmsearcher==null){
			dmsearcher=new TableSearcher(this);
		}
		dmsearcher.setLastrow(startrow);
		int row=dmsearcher.search(text);
		if(row>=0){
			this.setRowSelectionInterval(row, row);
			int c=dmsearcher.getLastcol();
			scrollToCell(row, c);
		}
		return row;
	}

	public int searchNext(){
		if(dmsearcher==null){
			dmsearcher=new TableSearcher(this);
		}

		int row= dmsearcher.searchNext();
		if(row>=0){
			this.setRowSelectionInterval(row, row);
			int c=dmsearcher.getLastcol();
			scrollToCell(row, c);
		}
		return row;
	}

} // CTable

