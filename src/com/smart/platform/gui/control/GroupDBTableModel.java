package com.smart.platform.gui.control;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.util.DecimalHelper;
import com.smart.platform.util.DefaultNPParam;

/**
 * 分组的数据源 根据分组信息,建立一个对应关系 2008-11-04 修改. 先按大组,再按小组. 生成组合计时,先小组后大组.
 * 
 * @author Administrator
 * 
 */
public class GroupDBTableModel extends Sumdbmodel {

	/**
	 * 分组信息
	 */
	Vector<SplitGroupInfo> groupinfos = new Vector<SplitGroupInfo>();

	/**
	 * 行号换算. 第rownumbers[n]值为相对于dbmodel的列. 如果rownumbers[n]值小于0, 表示是分组信息.
	 */
	int rownumbers[] = null;

	/**
	 * 分组后的实例
	 */
	HashMap<Integer, GroupInstance> groupinstmap = new HashMap<Integer, GroupInstance>();

	/**
	 * 构造函数
	 * 
	 * @param dbmodel
	 *            数据源
	 * @param groupinfos
	 *            分组信息,按组级别由小到大排列
	 */
	public GroupDBTableModel(DBTableModel dbmodel,
			Vector<SplitGroupInfo> groupinfos) {
		super(dbmodel, new Vector<String>());
		this.dbmodel = dbmodel;
		this.groupinfos = groupinfos;

		fireDatachanged();
	}

	public GroupDBTableModel(DBTableModel dbmodel) {
		super(dbmodel, new Vector<String>());
		this.dbmodel = dbmodel;
		fireDatachanged();
	}

	/**
	 * 进行分组
	 */
	protected void doSplitgroup() {
		if (groupinfos == null)
			return;
		int level = 1;
		Vector<SplitGroupInfo> sbgroups = new Vector<SplitGroupInfo>();
		Enumeration<SplitGroupInfo> en = groupinfos.elements();
		while (en.hasMoreElements()) {
			SplitGroupInfo groupinfo = en.nextElement();
			sbgroups.add(groupinfo);
			doSplitgroup(sbgroups, level++);
		}
	}

	/**
	 * 进行一个组的分组
	 * 
	 * @param groupinfo
	 */
	protected void doSplitgroup(Vector<SplitGroupInfo> groupinfos,
			int grouplevel) {
		String priorkey = null;
		GroupInstance groupinst = null;
		SplitGroupInfo groupinfo = groupinfos.lastElement();
		for (int i = 0; i < dbmodel.getRowCount(); i++) {
			String key = getGroupKey(i, groupinfos);
			if (!key.equals(priorkey)) {
				// 开始新组
				if (groupinst != null) {
					// 先保存原来的组
					groupinst.endrow = i - 1;
					calcGroupSum(groupinfo, groupinst);
					insertRow(i, groupinst);
					//calcGroupSum(groupinfo, groupinst);
				}
				priorkey = key;
				groupinst = new GroupInstance();
				groupinst.groupinfo = groupinfo;
				groupinst.grouplevel = grouplevel;
				groupinst.rec = new RecordTrunk(dbmodel.getColumnCount());
				groupinst.rec.setGroupname(groupinfo.getTitle());
				groupinst.rec.setDbstatus(RecordTrunk.DBSTATUS_SAVED);
				groupinst.rec.setSumflag(RecordTrunk.SUMFLAG_SUMMARY);
				groupinst.rec.setGrouplevel(groupinfo.getLevel());
				groupinst.startrow = i;
				setGroupcolumn(i, groupinfo, groupinst);
				groupinst.sumrec = new RecordTrunk(dbmodel.getColumnCount());
				groupinst.maxrec = new RecordTrunk(dbmodel.getColumnCount());
				groupinst.minrec = new RecordTrunk(dbmodel.getColumnCount());
				groupinst.countrec = new RecordTrunk(dbmodel.getColumnCount());
			}

			// 最后一行了
			if (i == dbmodel.getRowCount() - 1) {
				groupinst.endrow = i;
				calcGroupSum(groupinfo, groupinst);
				insertRow(i + 1, groupinst);
				//calcGroupSum(groupinfo, groupinst);
				break;
			}
		}
	}

	/**
	 * 计算组的合计
	 * 
	 * @param groupinfo
	 * @param groupinst
	 */
	void calcGroupSum(SplitGroupInfo groupinfo, GroupInstance groupinst) {
		Enumeration<DBColumnDisplayInfo> en = dbmodel.getDisplaycolumninfos()
				.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en.nextElement();
			// Enumeration<SplitGroupInfo.Datacolumn> en =
			// groupinfo.getDatacolumn();
			// while (en.hasMoreElements()) {
			// SplitGroupInfo.Datacolumn datacol = en.nextElement();
			// DBColumnDisplayInfo colinfo = dbmodel
			// .getColumninfo(datacol.colname);

			SplitGroupInfo.Datacolumn datacol = null;
			Enumeration<SplitGroupInfo.Datacolumn> en1 = groupinfo
					.getDatacolumn();
			while (en1.hasMoreElements()) {
				SplitGroupInfo.Datacolumn tmpdatacol = en1.nextElement();
				if (tmpdatacol.colname.equals(colinfo.getColname())) {
					datacol = tmpdatacol;
				}
			}

			int colindex = dbmodel.getColumnindex(colinfo.getColname());
			// 填写计数

			// if (datacol.method.equals(SplitGroupInfo.DATACOLUMN_COUNT)) {
			int grouprowcount = groupinst.endrow - groupinst.startrow + 1;
			groupinst.countrec.setElementAt(String.valueOf(grouprowcount), colindex);
			// }

			//缺省不要算sum
			String calcmethod="";
			if (datacol != null && datacol.method != null) {
				calcmethod = datacol.method ;
			}
			if (colinfo.getColtype().equalsIgnoreCase("number")) {

				// 求和
				BigDecimal sum = new BigDecimal(0);
				for (int r = groupinst.startrow; r <= groupinst.endrow; r++) {
					sum = sum.add(DecimalHelper.toDec(dbmodel.getItemValue(r,
							colindex)));
				}

				// 求最大最小.
				String max = "";
				String min = "";
				max = dbmodel.getItemValue(groupinst.startrow, colindex);
				min = dbmodel.getItemValue(groupinst.startrow, colindex);

				for (int r = groupinst.startrow + 1; r <= groupinst.endrow; r++) {
					String v = dbmodel.getItemValue(r, colindex);
					if (DecimalHelper.comparaDecimal(v, max) > 0) {
						max = v;
					}
					if (DecimalHelper.comparaDecimal(v, min) < 0) {
						min = v;
					}
				}

				// 看是求和还是平均
				if (datacol != null
						&& calcmethod.equals(SplitGroupInfo.DATACOLUMN_AVG)) {
					String sgrouprowcount = String.valueOf(groupinst.endrow
							- groupinst.startrow + 1);
					String avg = DecimalHelper.divide(sum.toPlainString(),
							sgrouprowcount, 20);
					avg = DecimalHelper.trimZero(avg);
					groupinst.rec.setElementAt(avg, colindex);
				} else if (datacol != null
						&& calcmethod.equals(SplitGroupInfo.DATACOLUMN_MAX)) {
					groupinst.rec.setElementAt(DecimalHelper.trimZero(max),
							colindex);
				} else if (calcmethod.equals(SplitGroupInfo.DATACOLUMN_MIN)) {
					groupinst.rec.setElementAt(DecimalHelper.trimZero(min),
							colindex);
				} else if (datacol != null
						&& calcmethod
								.equals(SplitGroupInfo.DATACOLUMN_COUNT)) {
					grouprowcount = groupinst.endrow - groupinst.startrow + 1;
					groupinst.rec.setElementAt(String.valueOf(grouprowcount),
							colindex);
				} else if (datacol != null
						&& calcmethod
						.equals(SplitGroupInfo.DATACOLUMN_SUM)) {
					groupinst.rec.setElementAt(sum.toPlainString(), colindex);
				}

				// 填写求和
				groupinst.sumrec.setElementAt(sum.toPlainString(), colindex);

				// 最大
				groupinst.maxrec.setElementAt(max, colindex);

				// 最小
				groupinst.minrec.setElementAt(min, colindex);
			} else if (colinfo.getColtype().equalsIgnoreCase(
					DBColumnDisplayInfo.COLTYPE_VARCHAR)
					|| colinfo.getColtype().equalsIgnoreCase(
							DBColumnDisplayInfo.COLTYPE_DATE)) {

				// 求最大最小.
				String max = "";
				String min = "";
				max = dbmodel.getItemValue(groupinst.startrow, colindex);
				min = dbmodel.getItemValue(groupinst.startrow, colindex);

				for (int r = groupinst.startrow + 1; r <= groupinst.endrow; r++) {
					String v = dbmodel.getItemValue(r, colindex);
					if (v.compareTo(max) > 0) {
						max = v;
					}
					if (v.compareTo(min) < 0) {
						min = v;
					}
				}

				if (datacol != null
						&& calcmethod.equals(SplitGroupInfo.DATACOLUMN_MAX)) {
					groupinst.rec.setElementAt(max, colindex);
				} else if (datacol != null
						&& calcmethod.equals(SplitGroupInfo.DATACOLUMN_MIN)) {
					groupinst.rec.setElementAt(min, colindex);
				} else if (datacol != null
						&& calcmethod
								.equals(SplitGroupInfo.DATACOLUMN_COUNT)) {
					grouprowcount = groupinst.endrow - groupinst.startrow + 1;
					groupinst.rec.setElementAt(String.valueOf(grouprowcount),
							colindex);
				}

				// 最大
				groupinst.maxrec.setElementAt(max, colindex);

				// 最小
				groupinst.minrec.setElementAt(min, colindex);

			}
		}

	}

	@Override
	public String getItemValue(int row, String colname) {
		int colindex = dbmodel.getColumnindex(colname);
		if (colindex < 0) {
			return "不存在的列" + colname;
		}
		return getItemValue(row, colindex);
	}

	@Override
	public int getRowCount() {
		// 加1是合计行
		if (rownumbers == null) {
			return 1;
		}
		return rownumbers.length + 1;
	}

	/**
	 * 设置分组列名. 取分组列最后一列的位置
	 * 
	 * @param i
	 * @param groupinfo
	 * @param groupinst
	 */
	void setGroupcolumn(int i, SplitGroupInfo groupinfo, GroupInstance groupinst) {
		String lastcolname = "";
		Enumeration<String> en = groupinfo.getGroupGroupcolumns();
		while (en.hasMoreElements()) {
			lastcolname = en.nextElement();
		}

		String v = groupinfo.getTitle();
		int colindex = dbmodel.getColumnindex(lastcolname);
		groupinst.rec.setElementAt(v, colindex);

	}

	/**
	 * 根据分组信息,将第i行的分组列值合成一个字符串,用:隔开
	 * 
	 * @param i
	 * @param groupinfo
	 * @return
	 */
	String getGroupKey(int i, Vector<SplitGroupInfo> groupinfos) {
		StringBuffer sb = new StringBuffer();
		Enumeration<SplitGroupInfo> en1 = groupinfos.elements();
		while (en1.hasMoreElements()) {
			SplitGroupInfo groupinfo = en1.nextElement();
			Enumeration<String> en = groupinfo.getGroupGroupcolumns();
			while (en.hasMoreElements()) {
				String colname = en.nextElement();
				if (sb.length() > 0)
					sb.append(":");
				sb.append(dbmodel.getItemValue(i, colname));
			}
		}
		return sb.toString();

	}

	/**
	 * 分好组的定例
	 * 
	 * @author Administrator
	 * 
	 */
	class GroupInstance {
		int grouplevel = 0;
		SplitGroupInfo groupinfo = null;
		// 放显示的值
		RecordTrunk rec;
		// 放求和值
		RecordTrunk sumrec;
		// 放最大值
		RecordTrunk maxrec;
		// 放最小值
		RecordTrunk minrec;
		// 放计数器.
		RecordTrunk countrec;
		int startrow;
		int endrow;
	}

	/**
	 * 生成分组实例ID,应该为负整数
	 * 
	 * @return
	 */
	int genGroupinstanceid() {
		return -(groupinstmap.size() + 1);
	}

	/**
	 * 在rownumbers的第row个成员前插入rownumber
	 * 
	 * @param row
	 * @param rownumber
	 */
	void insertRow(int row, GroupInstance groupinst) {
		int pos = -1;
		// 找到所在的调整后的位置
		for (int i = 0; i < rownumbers.length; i++) {
			if (rownumbers[i] == row) {
				pos = i;
				break;
			}
		}
		if (pos == -1) {
			pos = rownumbers.length;
		}
		row = pos;

		// 往上找,如果是分组行要往前移.
		int j;
		for (j = row - 1; j >= 0; j--) {
			int rowpos = rownumbers[j];
			if (rowpos >= 0)
				break;
			// 如果type<0说明是分组,要插在前面
			row = j;
		}

		int tmp[] = new int[rownumbers.length + 1];
		System.arraycopy(rownumbers, 0, tmp, 0, row);
		int groupid = this.genGroupinstanceid();
		tmp[row] = groupid;
		groupinstmap.put(new Integer(groupid), groupinst);

		System
				.arraycopy(rownumbers, row, tmp, row + 1, rownumbers.length
						- row);
		rownumbers = tmp;
	}

	public DBTableModel getDbmodel() {
		return dbmodel;
	}

	public DBColumnDisplayInfo getColumninfo(String colname) {
		return dbmodel.getColumninfo(colname);
	}

	public void addDisplayColuminfo(DBColumnDisplayInfo info) {
		dbmodel.addDisplayColuminfo(info); // To change body of overridden
		// methods use File | Settings |
		// File Templates.
	}

	public Vector<DBColumnDisplayInfo> getDisplaycolumninfos() {
		return dbmodel.getDisplaycolumninfos(); // To change body of overridden
		// methods use File | Settings |
		// File Templates.
	}

	public void setDisplaycolumninfos(
			Vector<DBColumnDisplayInfo> displaycolumninfos) {
		dbmodel.setDisplaycolumninfos(displaycolumninfos); // To change body of
		// overridden
		// methods use File
		// | Settings | File
		// Templates.
	}

	public void finishBuild() {
		dbmodel.finishBuild(); // To change body of overridden methods use File
		// | Settings | File Templates.
	}

	public int getColumnindex(String colname) {
		return dbmodel.getColumnindex(colname); // To change body of overridden
		// methods use File | Settings |
		// File Templates.
	}

	public void setItemValue(int row, int colindex, String value) {
		getRecordThunk(row).setElementAt(value, colindex);
	}

	public void setItemValue(int row, String colname, String value) {
		int colindex = dbmodel.getColumnindex(colname);
		if (colindex < 0) {
			System.err.println("找不到列" + colname);
			return;
		}
		setItemValue(row, colindex, value);
	}

	public JComponent getEditComp(String colname) {
		return dbmodel.getEditComp(colname); // To change body of overridden
		// methods use File | Settings |
		// File Templates.
	}

	public String getColumnDBName(int colindex) {
		return dbmodel.getColumnDBName(colindex); // To change body of
		// overridden methods use
		// File | Settings | File
		// Templates.
	}

	public String getColumnDBType(int colindex) {
		return dbmodel.getColumnDBType(colindex);
	}

	public String getColumnDBType(String colname) {
		return dbmodel.getColumnDBType(colname);
	}

	public int getdbStatus(int row) {
		return getRecordThunk(row).getDbstatus();
	}

	public void setdbStatus(int row, int status) {
		getRecordThunk(row).setDbstatus(status);
	}

	public void bindMemds(DBTableModel dbmodel) {
		this.dbmodel.bindMemds(dbmodel);
	}

	public RecordTrunk getRecordThunk(int row) {
		if (dbmodel == null) {
			return null;
		}

		if (row == this.getRowCount() - 1) {
			return sumrec;
		}

		int newrow = rownumbers[row];
		if (newrow >= 0) {
			if (newrow < 0 || newrow > dbmodel.getRowCount() - 1) {
				int m;
				m = 3;
			}
			return dbmodel.getRecordThunk(newrow);
		} else {
			GroupInstance groupinst = groupinstmap.get(new Integer(newrow));
			return groupinst.rec;
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
		return getRecordThunk(row).getSaveresult();
	}

	public String getResultMessage(int row) {
		return getRecordThunk(row).getSavemessage();
	}

	public String getItemValue(int row, int colindex) {
		return (String) getRecordThunk(row).elementAt(colindex);
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
		int colindex = dbmodel.getColumnindex(colname);
		return this.getRecordThunk(row).isColumnmodified(colindex);
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
		return getRecordThunk(row).elementAt(column);
	}

	public void setValueAt(Object aValue, int row, int column) {
		if (dbmodel == null) {
			return;
		}
		getRecordThunk(row).setElementAt(aValue, column);
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
		fireDatachanged();
	}

	/**
	 * 记录增或删
	 */
	public void fireDatachanged() {
		rownumbers = new int[dbmodel.getRowCount()];
		for (int i = 0; i < rownumbers.length; i++) {
			rownumbers[i] = i;
		}
		doSplitgroup();
		calcSum();
	}

	private void calcSum() {
		if (sumcolnames == null || sumcolnames.size() == 0) {
			calcSumByguess();
		} else {
			calcSumByparam();
		}
	}

	/**
	 * 猜列
	 */
	private void calcSumByguess() {
		sumrec.setSumflag(RecordTrunk.SUMFLAG_SUMMARY);
		sumrec.setDbstatus(RecordTrunk.DBSTATUS_SAVED);

		Enumeration<DBColumnDisplayInfo> en = dbmodel.getDisplaycolumninfos()
				.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = (DBColumnDisplayInfo) en
					.nextElement();
			String colname = colinfo.getColname();
			if (colinfo.getColtype().equals("number") /*
													 * &&
													 * !isNotneedSumcol(colinfo
													 * .getColname())
													 */) {
				// logger.debug("begin calc column "+colname);
				BigDecimal decsum = dbmodel.sum(colname);
				// logger.debug("finish calc column "+colname);
				int colindex = dbmodel.getColumnindex(colname);
				sumrec.setElementAt(decsum.toPlainString(), colindex);
			}
		}
	}

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

	/**
	 * 测试
	 */
	public static void debug() {
		Vector<DBColumnDisplayInfo> colinfos = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col = null;
		col = new DBColumnDisplayInfo("行号", "行号");
		colinfos.add(col);

		col = new DBColumnDisplayInfo("deptname", "varchar");
		colinfos.add(col);

		col = new DBColumnDisplayInfo("employeename", "varchar");
		colinfos.add(col);

		col = new DBColumnDisplayInfo("money", "number");
		colinfos.add(col);

		DBTableModel dbmodel = new DBTableModel(colinfos);
		dbmodel.appendRow();
		dbmodel.setItemValue(0, "employeename", "emp_1_1");
		dbmodel.setItemValue(0, "deptname", "dept_1");
		dbmodel.setItemValue(0, "money", "1");
		dbmodel.appendRow();
		dbmodel.setItemValue(1, "employeename", "emp_1_1");
		dbmodel.setItemValue(1, "deptname", "dept_1");
		dbmodel.setItemValue(1, "money", "2");
		dbmodel.appendRow();
		dbmodel.setItemValue(2, "employeename", "emp_1_2");
		dbmodel.setItemValue(2, "deptname", "dept_1");
		dbmodel.setItemValue(2, "money", "3");

		// ///////dept_2
		dbmodel.appendRow();
		dbmodel.setItemValue(3, "employeename", "emp_2_1");
		dbmodel.setItemValue(3, "deptname", "dept_2");
		dbmodel.setItemValue(3, "money", "10");
		dbmodel.appendRow();
		dbmodel.setItemValue(4, "employeename", "emp_2_1");
		dbmodel.setItemValue(4, "deptname", "dept_2");
		dbmodel.setItemValue(4, "money", "20");

		Vector<SplitGroupInfo> gpinfos = new Vector<SplitGroupInfo>();
		SplitGroupInfo gpinfo = new SplitGroupInfo();
		gpinfo.setTitle("人员小计");
		gpinfo.addGroupColumn("deptname");
		gpinfo.addGroupColumn("employeename");
		gpinfo.addDataColumn("money", SplitGroupInfo.DATACOLUMN_SUM);
		gpinfos.add(gpinfo);

		gpinfo = new SplitGroupInfo();
		gpinfo.setTitle("部门小计");
		gpinfo.addGroupColumn("deptname");
		gpinfo.addDataColumn("money", SplitGroupInfo.DATACOLUMN_SUM);
		gpinfos.add(gpinfo);

		GroupDBTableModel gpmodel = new GroupDBTableModel(dbmodel, gpinfos);
		// GroupDBTableModel gpmodel=new GroupDBTableModel(dbmodel);
		for (int r = 0; r < gpmodel.getRowCount(); r++) {
			System.out.print(gpmodel.getItemValue(r, "deptname"));
			System.out.print("\t");
			System.out.print(gpmodel.getItemValue(r, "employeename"));
			System.out.print("\t");
			System.out.println(gpmodel.getItemValue(r, "money"));
		}

	}

	public String getGroupSum(int row, String colname) {
		int newrow = rownumbers[row];
		if (newrow >= 0) {
			return "";
		} else {
			int colindex = dbmodel.getColumnindex(colname);
			GroupInstance groupinst = groupinstmap.get(new Integer(newrow));
			return (String) groupinst.sumrec.elementAt(colindex);
		}
	}

	public String getGroupMax(int row, String colname) {
		int newrow = rownumbers[row];
		if (newrow >= 0) {
			return "";
		} else {
			int colindex = dbmodel.getColumnindex(colname);
			GroupInstance groupinst = groupinstmap.get(new Integer(newrow));
			return (String) groupinst.maxrec.elementAt(colindex);
		}
	}

	public String getGroupMin(int row, String colname) {
		int newrow = rownumbers[row];
		if (newrow >= 0) {
			return "";
		} else {
			int colindex = dbmodel.getColumnindex(colname);
			GroupInstance groupinst = groupinstmap.get(new Integer(newrow));
			return (String) groupinst.minrec.elementAt(colindex);
		}
	}

	public String getGroupRowcount(int row, String colname) {
		int newrow = rownumbers[row];
		if (newrow >= 0) {
			return "";
		} else {
			int colindex = dbmodel.getColumnindex(colname);
			GroupInstance groupinst = groupinstmap.get(new Integer(newrow));
			return (String) groupinst.countrec.elementAt(colindex);
		}
	}

	public static void main(String[] argv) {
		GroupDBTableModel.debug();
	}
}
