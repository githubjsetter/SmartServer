package com.inca.np.gui.control;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.inca.np.communicate.RecordTrunk;
import com.inca.np.util.DefaultNPParam;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-5-31 Time: 15:37:54
 * 用于实现合计的dbmodel 不能进行编辑的 ???
 */
public class Sumdbmodel extends DBTableModel {
	protected DBTableModel dbmodel = null;

	Vector<String> sumcolnames = null;
	RecordTrunk sumrec = null;

	// protected static Vector<String> neednotsumcolumns=new Vector<String>();
	@Deprecated
	public static void addNeednotsumcolumn(String colname) {
		// neednotsumcolumns.add(colname);
	}

	public Sumdbmodel(DBTableModel dbmodel) {
		this.dbmodel = dbmodel;
		Vector<String> sumcols = new Vector<String>();
		Enumeration<DBColumnDisplayInfo> en = dbmodel.getDisplaycolumninfos()
				.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo col = en.nextElement();
			if (col.isCalcsum()) {
				sumcols.add(col.getColname());
			}
		}
		this.sumcolnames = sumcols;
		sumrec = new RecordTrunk(dbmodel.getColumnCount());
		sumrec.setDbstatus(RecordTrunk.DBSTATUS_SAVED);
		this.fireDatachanged();
	}

	public Sumdbmodel(DBTableModel dbmodel, Vector<String> sumcolnames) {
		this.dbmodel = dbmodel;
		this.sumcolnames = sumcolnames;
		sumrec = new RecordTrunk(dbmodel.getColumnCount());
		sumrec.setDbstatus(RecordTrunk.DBSTATUS_SAVED);
		this.fireDatachanged();
	}

	public Sumdbmodel(Vector<DBColumnDisplayInfo> displaycolumninfos,
			DBTableModel dbmodel, Vector<String> sumcolnames) {
		this.dbmodel = dbmodel;
		this.sumcolnames = sumcolnames;
		sumrec = new RecordTrunk(dbmodel.getColumnCount());
		sumrec.setDbstatus(RecordTrunk.DBSTATUS_SAVED);
		this.fireDatachanged();
	}

	public DBTableModel getDbmodel() {
		return dbmodel;
	}

	public DBColumnDisplayInfo getColumninfo(String colname) {
		if (dbmodel == null) {
			return null;
		}
		return dbmodel.getColumninfo(colname);
	}

	public void addDisplayColuminfo(DBColumnDisplayInfo info) {
		dbmodel.addDisplayColuminfo(info);
	}

	public Vector<DBColumnDisplayInfo> getDisplaycolumninfos() {
		if (dbmodel == null) {
			return null;
		}
		return dbmodel.getDisplaycolumninfos();
	}

	public void setDisplaycolumninfos(
			Vector<DBColumnDisplayInfo> displaycolumninfos) {
		dbmodel.setDisplaycolumninfos(displaycolumninfos);
	}

	public void finishBuild() {
		dbmodel.finishBuild();
	}

	public int getColumnindex(String colname) {
		return dbmodel.getColumnindex(colname);
	}

	public void setItemValue(int row, int col, String value) {
		dbmodel.setItemValue(row, col, value);
	}

	public void setItemValue(int row, String colname, String value) {
		dbmodel.setItemValue(row, colname, value);
	}

	public JComponent getEditComp(String colname) {
		return dbmodel.getEditComp(colname);
	}

	public String getColumnDBName(int colindex) {
		return dbmodel.getColumnDBName(colindex);
	}

	public String getColumnDBType(int colindex) {
		return dbmodel.getColumnDBType(colindex);
	}

	public String getColumnDBType(String colname) {
		return dbmodel.getColumnDBType(colname);
	}

	public int getdbStatus(int row) {
		if (dbmodel == null) {
			return 0;
		}
		if (row < dbmodel.getRowCount()) {
			return dbmodel.getdbStatus(row);
		} else {
			return sumrec.getDbstatus();
		}
	}

	public void setdbStatus(int row, int status) {
		if (row < dbmodel.getRowCount()) {
			dbmodel.setdbStatus(row, status);
		} else {
			sumrec.setDbstatus(status);
		}
	}

	public void bindMemds(DBTableModel dbmodel) {
		this.dbmodel.bindMemds(dbmodel);
	}

	public RecordTrunk getRecordThunk(int row) {
		if (row < dbmodel.getRowCount()) {
			return dbmodel.getRecordThunk(row);
		} else {
			sumrec.setSumflag(RecordTrunk.SUMFLAG_SUMMARY);
			return sumrec;
		}
	}

	public DBTableModel getModifiedData() {
		return dbmodel.getModifiedData();
	}

	public RecordTrunk appendRow() {
		return dbmodel.appendRow();
	}

	public Vector getDataVector() {
		logger.error("!!!!!!!!!!SUMDBMODEL!!!!调用了getDataVector");
		Vector<RecordTrunk> tmprec = new Vector<RecordTrunk>(dbmodel
				.getRowCount());
		Enumeration<RecordTrunk> en = dbmodel.getDataVector().elements();
		while (en.hasMoreElements()) {
			tmprec.add(en.nextElement());
		}
		tmprec.add(sumrec);
		return tmprec;

	}

	public void clearAll() {
		if (dbmodel != null) {
			dbmodel.clearAll();
		}
	}

	public void setRowCount(int rowCount) {
		dbmodel.setRowCount(rowCount);
	}

	public void setLineresult(RecordTrunk lineresult) {
		dbmodel.setLineresult(lineresult);
	}

	public void setLineresults(Vector<RecordTrunk> results) {
		dbmodel.setLineresults(results);
	}

	public void clearDeleted() {
		dbmodel.clearDeleted();
	}

	public int getResult(int row) {
		if (row > dbmodel.getRowCount() - 1) {
			return 0;
		}
		return dbmodel.getResult(row);
	}

	public String getResultMessage(int row) {
		// System.out.println("getResultMessage
		// ,row="+row+",dbmodel.rowcount="+dbmodel.getRowCount());
		if (row < dbmodel.getRowCount()) {
			return dbmodel.getResultMessage(row);
		}
		return sumrec.getSavemessage();
	}

	public String getItemValue(int row, String colname) {
		if (row < dbmodel.getRowCount()) {
			return dbmodel.getItemValue(row, colname);
		} else {
			int colindex = dbmodel.getColumnindex(colname);
			// 进行format?
			DBColumnDisplayInfo colinfo = this.getColumninfo(colname);
			if (colinfo == null) {
				return "";
			}
			return (String) sumrec.elementAt(colindex);
		}
	}

	public String getItemValue(int row, int col) {
		if (row < dbmodel.getRowCount()) {
			return dbmodel.getItemValue(row, col);
		} else {
			return (String) sumrec.elementAt(col);
		}
	}

	public void writeData(OutputStream out) throws Exception {
		dbmodel.writeData(out);
	}

	public void readData(InputStream in) throws Exception {
		dbmodel.readData(in);
	}

	public boolean hasmore() {
		return dbmodel.hasmore();
	}

	public void setHasmore(boolean hasmore) {
		dbmodel.setHasmore(hasmore);
	}

	public void addRecord(RecordTrunk rec) {
		dbmodel.addRecord(rec);
	}

	public boolean isColumnmodified(int row, String colname) {
		return dbmodel.isColumnmodified(row, colname);
	}

	public boolean isquerying() {
		return dbmodel.isquerying();
	}

	public boolean doRetrieve(String sql, int maxrowcount) {
		return dbmodel.doRetrieve(sql, maxrowcount);
	}

	public DBTableModelEvent getRetrievelistener() {
		return dbmodel.getRetrievelistener();
	}

	public void setRetrievelistener(DBTableModelEvent retrievelistener) {
		dbmodel.setRetrievelistener(retrievelistener);
	}

	public void stopQuery() {
		dbmodel.stopQuery();
	}

	public void undo(int row) {
		dbmodel.undo(row);
	}

	public void sort(int colindex, boolean asc) {
		dbmodel.sort(colindex, asc);
	}

	public void resort() {
		dbmodel.resort();
	}

	public void setIsquerying(boolean isquerying) {
		dbmodel.setIsquerying(isquerying);
	}

	public BigDecimal sum(String colname) {
		return dbmodel.sum(colname);
	}

	public void setDataVector(Vector dataVector, Vector columnIdentifiers) {
		// dbmodel.setDataVector(dataVector, columnIdentifiers);
	}

	public void setDataVector(Object[][] dataVector, Object[] columnIdentifiers) {
		dbmodel.setDataVector(dataVector, columnIdentifiers);
	}

	public void newDataAvailable(TableModelEvent event) {
		dbmodel.newDataAvailable(event);
	}

	public void newRowsAdded(TableModelEvent e) {
		dbmodel.newRowsAdded(e);
	}

	public void rowsRemoved(TableModelEvent event) {
		dbmodel.rowsRemoved(event);
	}

	/*
	 * Sets the number of rows in the model. If the new size is greater than the
	 * current size, new rows are added to the end of the model If the new size
	 * is less than the current size, all rows at index <code>rowCount</code>
	 * and greater are discarded. <p>
	 * 
	 * @param rowCount the new number of rows
	 * 
	 * @see #setRowCount
	 */
	public void setNumRows(int rowCount) {
		dbmodel.setNumRows(rowCount);
	}

	public void addRow(Vector rowData) {
		dbmodel.addRow(rowData);
	}

	public void addRow(Object[] rowData) {
		dbmodel.addRow(rowData);
	}

	public void insertRow(int row, Vector rowData) {
		dbmodel.insertRow(row, rowData);
	}

	public void insertRow(int row, Object[] rowData) {
		dbmodel.insertRow(row, rowData);
	}

	public void moveRow(int start, int end, int to) {
		dbmodel.moveRow(start, end, to);
	}

	public void removeRow(int row) {
		dbmodel.removeRow(row);
	}

	public void setColumnIdentifiers(Vector columnIdentifiers) {
		dbmodel.setColumnIdentifiers(columnIdentifiers);
	}

	public void setColumnIdentifiers(Object[] newIdentifiers) {
		dbmodel.setColumnIdentifiers(newIdentifiers);
	}

	public void setColumnCount(int columnCount) {
		dbmodel.setColumnCount(columnCount);
	}

	public void addColumn(Object columnName) {
		dbmodel.addColumn(columnName);
	}

	public void addColumn(Object columnName, Vector columnData) {
		dbmodel.addColumn(columnName, columnData);
	}

	public void addColumn(Object columnName, Object[] columnData) {
		dbmodel.addColumn(columnName, columnData);
	}

	public int getRowCount() {
		if (dbmodel == null) {
			return 0;
		}
		return dbmodel.getRowCount() + 1;
	}

	public int getColumnCount() {
		return dbmodel.getColumnCount();
	}

	public String getColumnName(int column) {
		return dbmodel.getColumnName(column);
	}

	public boolean isCellEditable(int row, int column) {
		return dbmodel.isCellEditable(row, column);
	}

	public Object getValueAt(int row, int column) {
		if (dbmodel == null) {
			return "";
		}
		RecordTrunk rec = null;
		if (row < dbmodel.getRowCount()) {
			rec = dbmodel.getRecordThunk(row);
		} else {
			rec = sumrec;
		}
		return rec.elementAt(column);
	}

	public void setValueAt(Object aValue, int row, int column) {
		if (row < 0 || column < 0)
			return;
		// 如果CComboxbox，需要区别对待。将aValue转为key
		if (row >= 0 && row < dbmodel.getRowCount()) {
			RecordTrunk rec = dbmodel.getRecordThunk(row);
			rec.setValueAt(column, (String) aValue);
			// 不要调用dbmodel.setItemvalue 20070928
			// dbmodel.setItemValue(row, column, (String)aValue);
		} else {
			sumrec.setElementAt((String) aValue, column);
		}
	}

	public int findColumn(String columnName) {
		return dbmodel.findColumn(columnName);
	}

	public Class<?> getColumnClass(int columnIndex) {
		return dbmodel.getColumnClass(columnIndex);
	}

	public void addTableModelListener(TableModelListener l) {
		dbmodel.addTableModelListener(l);
	}

	public void removeTableModelListener(TableModelListener l) {
		dbmodel.removeTableModelListener(l);
	}

	public TableModelListener[] getTableModelListeners() {
		return dbmodel.getTableModelListeners();
	}

	public void fireTableDataChanged() {
		dbmodel.fireTableDataChanged();
	}

	public void fireTableStructureChanged() {
		dbmodel.fireTableStructureChanged();
	}

	public void fireTableRowsInserted(int firstRow, int lastRow) {
		dbmodel.fireTableRowsInserted(firstRow, lastRow);
	}

	public void fireTableRowsUpdated(int firstRow, int lastRow) {
		dbmodel.fireTableRowsUpdated(firstRow, lastRow);
	}

	public void fireTableRowsDeleted(int firstRow, int lastRow) {
		dbmodel.fireTableRowsDeleted(firstRow, lastRow);
	}

	public void fireTableCellUpdated(int row, int column) {
		dbmodel.fireTableCellUpdated(row, column);
	}

	public void fireTableChanged(TableModelEvent e) {
		dbmodel.fireTableChanged(e);
	}

	public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
		return dbmodel.getListeners(listenerType);
	}

	/**
	 * 一行数据变化。
	 * 
	 * @param row
	 */
	public void fireRowDatachanged(int row) {
		calcSum();
	}

	/**
	 * 记录增或删
	 */
	public void fireDatachanged() {
		// logger.debug("calc sum begin");
		calcSum();
		// logger.debug("calc sum end");
	}

	private void calcSum() {
		if (sumcolnames == null || sumcolnames.size() == 0) {
			return;
		} else {
			calcSumByparam();
		}
	}

	/**
	 * 猜列
	 */
	/*
	 * private void calcSumByguess(){
	 * sumrec.setSumflag(RecordTrunk.SUMFLAG_SUMMARY);
	 * sumrec.setDbstatus(RecordTrunk.DBSTATUS_SAVED);
	 * 
	 * Enumeration<DBColumnDisplayInfo>
	 * en=dbmodel.getDisplaycolumninfos().elements(); while
	 * (en.hasMoreElements()) { DBColumnDisplayInfo colinfo =
	 * (DBColumnDisplayInfo) en.nextElement(); String
	 * colname=colinfo.getColname(); if(colinfo.getColtype().equals("number") &&
	 * !isNotneedSumcol(colinfo.getColname())){ //logger.debug("begin calc
	 * column "+colname); BigDecimal decsum = dbmodel.sum(colname);
	 * //logger.debug("finish calc column "+colname); int colindex =
	 * dbmodel.getColumnindex(colname);
	 * sumrec.setElementAt(decsum.toPlainString(),colindex); } } }
	 * 
	 */

	private void calcSumByparam() {
		sumrec.setSumflag(RecordTrunk.SUMFLAG_SUMMARY);
		sumrec.setDbstatus(RecordTrunk.DBSTATUS_SAVED);

		Enumeration<String> en1 = sumcolnames.elements();
		while (en1.hasMoreElements()) {
			String colname = en1.nextElement();
			BigDecimal decsum = dbmodel.sum(colname);
			int colindex = dbmodel.getColumnindex(colname);
			sumrec.setElementAt(decsum.toPlainString(), colindex);
		}

	}

	/*
	 * protected boolean isNotneedSumcol(String cn){ cn = cn.toLowerCase();
	 * if(cn.endsWith("type"))return true; if(cn.endsWith("flag"))return true;
	 * if(cn.endsWith("rate"))return true; if(cn.endsWith("id"))return true;
	 * if(cn.equals("packsize"))return true; if(cn.indexOf("status")>=0)return
	 * true; if(cn.indexOf("flag")>=0)return true;
	 * if(cn.indexOf("priority")>=0)return true;
	 * if(cn.equalsIgnoreCase("result"))return true;
	 * if(cn.equalsIgnoreCase("usetime"))return true;
	 * if(cn.equalsIgnoreCase("remainqty"))return true;
	 * if(cn.indexOf("price")>=0)return true; if(cn.indexOf("percent")>=0)return
	 * true;
	 * 
	 * String[] nosumcolumns=DefaultNPParam.nosumcolumns; for(int
	 * i=0;nosumcolumns!=null && i<nosumcolumns.length;i++){
	 * if(cn.equalsIgnoreCase(nosumcolumns[i]))return true; }
	 * 
	 * Enumeration<String>en=neednotsumcolumns.elements();
	 * while(en.hasMoreElements()){ String tmpname=en.nextElement();
	 * if(cn.equalsIgnoreCase(tmpname))return true; }
	 * 
	 * return false; }
	 */

	public void sort(String expr) throws Exception {
		dbmodel.sort(expr);
	}

	public void freeMemory() {
		dbmodel = null;
		sumcolnames = null;
		sumrec = null;
	}

	/**
	 * 设置一行记录的文件附件信息
	 * 
	 * @param row
	 * @param filedbmodel
	 */
	public void setFiledbmodel(int row, DBTableModel filedbmodel) {
		dbmodel.setFiledbmodel(row, filedbmodel);
	}

	/**
	 * 取一行记录的文件附件信息
	 * 
	 * @param row
	 * @return
	 */
	public DBTableModel getFiledbmodel(int row) {
		return dbmodel.getFiledbmodel(row);
	}

}
