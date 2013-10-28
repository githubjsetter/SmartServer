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
 * ���������Դ ���ݷ�����Ϣ,����һ����Ӧ��ϵ 2008-11-04 �޸�. �Ȱ�����,�ٰ�С��. ������ϼ�ʱ,��С������.
 * 
 * @author Administrator
 * 
 */
public class GroupDBTableModel extends Sumdbmodel {

	/**
	 * ������Ϣ
	 */
	Vector<SplitGroupInfo> groupinfos = new Vector<SplitGroupInfo>();

	/**
	 * �кŻ���. ��rownumbers[n]ֵΪ�����dbmodel����. ���rownumbers[n]ֵС��0, ��ʾ�Ƿ�����Ϣ.
	 */
	int rownumbers[] = null;

	/**
	 * ������ʵ��
	 */
	HashMap<Integer, GroupInstance> groupinstmap = new HashMap<Integer, GroupInstance>();

	/**
	 * ���캯��
	 * 
	 * @param dbmodel
	 *            ����Դ
	 * @param groupinfos
	 *            ������Ϣ,���鼶����С��������
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
	 * ���з���
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
	 * ����һ����ķ���
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
				// ��ʼ����
				if (groupinst != null) {
					// �ȱ���ԭ������
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

			// ���һ����
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
	 * ������ĺϼ�
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
			// ��д����

			// if (datacol.method.equals(SplitGroupInfo.DATACOLUMN_COUNT)) {
			int grouprowcount = groupinst.endrow - groupinst.startrow + 1;
			groupinst.countrec.setElementAt(String.valueOf(grouprowcount), colindex);
			// }

			//ȱʡ��Ҫ��sum
			String calcmethod="";
			if (datacol != null && datacol.method != null) {
				calcmethod = datacol.method ;
			}
			if (colinfo.getColtype().equalsIgnoreCase("number")) {

				// ���
				BigDecimal sum = new BigDecimal(0);
				for (int r = groupinst.startrow; r <= groupinst.endrow; r++) {
					sum = sum.add(DecimalHelper.toDec(dbmodel.getItemValue(r,
							colindex)));
				}

				// �������С.
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

				// ������ͻ���ƽ��
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

				// ��д���
				groupinst.sumrec.setElementAt(sum.toPlainString(), colindex);

				// ���
				groupinst.maxrec.setElementAt(max, colindex);

				// ��С
				groupinst.minrec.setElementAt(min, colindex);
			} else if (colinfo.getColtype().equalsIgnoreCase(
					DBColumnDisplayInfo.COLTYPE_VARCHAR)
					|| colinfo.getColtype().equalsIgnoreCase(
							DBColumnDisplayInfo.COLTYPE_DATE)) {

				// �������С.
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

				// ���
				groupinst.maxrec.setElementAt(max, colindex);

				// ��С
				groupinst.minrec.setElementAt(min, colindex);

			}
		}

	}

	@Override
	public String getItemValue(int row, String colname) {
		int colindex = dbmodel.getColumnindex(colname);
		if (colindex < 0) {
			return "�����ڵ���" + colname;
		}
		return getItemValue(row, colindex);
	}

	@Override
	public int getRowCount() {
		// ��1�Ǻϼ���
		if (rownumbers == null) {
			return 1;
		}
		return rownumbers.length + 1;
	}

	/**
	 * ���÷�������. ȡ���������һ�е�λ��
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
	 * ���ݷ�����Ϣ,����i�еķ�����ֵ�ϳ�һ���ַ���,��:����
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
	 * �ֺ���Ķ���
	 * 
	 * @author Administrator
	 * 
	 */
	class GroupInstance {
		int grouplevel = 0;
		SplitGroupInfo groupinfo = null;
		// ����ʾ��ֵ
		RecordTrunk rec;
		// �����ֵ
		RecordTrunk sumrec;
		// �����ֵ
		RecordTrunk maxrec;
		// ����Сֵ
		RecordTrunk minrec;
		// �ż�����.
		RecordTrunk countrec;
		int startrow;
		int endrow;
	}

	/**
	 * ���ɷ���ʵ��ID,Ӧ��Ϊ������
	 * 
	 * @return
	 */
	int genGroupinstanceid() {
		return -(groupinstmap.size() + 1);
	}

	/**
	 * ��rownumbers�ĵ�row����Աǰ����rownumber
	 * 
	 * @param row
	 * @param rownumber
	 */
	void insertRow(int row, GroupInstance groupinst) {
		int pos = -1;
		// �ҵ����ڵĵ������λ��
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

		// ������,����Ƿ�����Ҫ��ǰ��.
		int j;
		for (j = row - 1; j >= 0; j--) {
			int rowpos = rownumbers[j];
			if (rowpos >= 0)
				break;
			// ���type<0˵���Ƿ���,Ҫ����ǰ��
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
			System.err.println("�Ҳ�����" + colname);
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
		logger.error("!!!!!!!!!!SUMDBMODEL!!!!������getDataVector");
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
	 * һ�����ݱ仯��
	 * 
	 * @param row
	 */
	public void fireRowDatachanged(int row) {
		fireDatachanged();
	}

	/**
	 * ��¼����ɾ
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
	 * ����
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
	 * ����
	 */
	public static void debug() {
		Vector<DBColumnDisplayInfo> colinfos = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col = null;
		col = new DBColumnDisplayInfo("�к�", "�к�");
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
		gpinfo.setTitle("��ԱС��");
		gpinfo.addGroupColumn("deptname");
		gpinfo.addGroupColumn("employeename");
		gpinfo.addDataColumn("money", SplitGroupInfo.DATACOLUMN_SUM);
		gpinfos.add(gpinfo);

		gpinfo = new SplitGroupInfo();
		gpinfo.setTitle("����С��");
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
