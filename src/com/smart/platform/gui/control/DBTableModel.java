package com.smart.platform.gui.control;

import javax.swing.table.DefaultTableModel;
import javax.swing.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;
import java.util.Enumeration;
import java.util.HashMap;
import java.awt.EventQueue;
import java.io.OutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

import org.apache.log4j.Category;

import com.smart.platform.communicate.*;
import com.smart.platform.demo.communicate.RemotesqlHelper;
import com.smart.platform.util.DecimalHelper;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-3-28 Time: 16:07:19
 * �ڴ�����Դ.���԰���������¼.
 */
public class DBTableModel extends DefaultTableModel {

	/**
	 * �Ƿ����̲߳�ѯ
	 */
	boolean usequerythread = true;

	/**
	 * �ж���
	 */
	protected Vector<DBColumnDisplayInfo> displaycolumninfos = new Vector<DBColumnDisplayInfo>();

	/**
	 * ���и����¼.�����������ǲ��ǻ���������¼
	 */
	int hasmore = 0;

	/**
	 * ������
	 */
	private MSort sorter;

	private Vector<DbtablemodelSortListener> sortlistener = new Vector<DbtablemodelSortListener>();

	/**
	 * ����.һ�㲻���������
	 */
	public DBTableModel() {
	}

	/**
	 * ���캯��
	 * 
	 * @param displaycolumninfos
	 *            �ж���
	 */
	public DBTableModel(Vector<DBColumnDisplayInfo> displaycolumninfos) {
		this.displaycolumninfos = displaycolumninfos;
		finishBuild();
	}

	/**
	 * �����еĶ���
	 * 
	 * @param info
	 */
	public void addDisplayColuminfo(DBColumnDisplayInfo info) {
		displaycolumninfos.add(info);
	}

	/**
	 * ȡ�еĶ���
	 * 
	 * @return
	 */
	public Vector<DBColumnDisplayInfo> getDisplaycolumninfos() {
		return displaycolumninfos;
	}

	public String getDBColumnName(int column) {
		if (column < 0 || column > displaycolumninfos.size() - 1)
			return "unknown column";
		return displaycolumninfos.elementAt(column).getColname();
	}

	/**
	 * ����ĳ�ж���
	 * 
	 * @return
	 */
	public DBColumnDisplayInfo getColumninfo(String colname) {
		Enumeration<DBColumnDisplayInfo> en = displaycolumninfos.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo info = en.nextElement();
			if (info.getColname().equalsIgnoreCase(colname)) {
				return info;
			}
		}
		return null;
	}

	/**
	 * �����еĶ���
	 * 
	 * @param displaycolumninfos
	 */
	public void setDisplaycolumninfos(
			Vector<DBColumnDisplayInfo> displaycolumninfos) {
		this.displaycolumninfos = displaycolumninfos;
	}

	/**
	 * ȡ�е�����
	 */
	public int getColumnCount() {
		if (displaycolumninfos == null)
			return 0;
		return displaycolumninfos.size();
	}

	/**
	 * �ڲ�����,�������Դ�Ĺ���
	 */
	public void finishBuild() {
		Vector columnnames = new Vector();

		Enumeration<DBColumnDisplayInfo> en = displaycolumninfos.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo info = en.nextElement();
			columnnames.add(info.getTitle());
		}

		this.setDataVector(new Vector(), columnnames);
	}

	/**
	 * ��������ѯ����
	 * 
	 * @param colname
	 *            ����
	 * @return ����
	 */
	public int getColumnindex(String colname) {
		Enumeration<DBColumnDisplayInfo> en = displaycolumninfos.elements();
		int i = 0;
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo info = en.nextElement();
			if (colname.equalsIgnoreCase(info.getColname())) {
				return i;
			}
			i++;
		}
		//logger.error("�Ҳ�������=" + colname);
		return -1;
	}

	Category logger = Category.getInstance(DBTableModel.class);

	/**
	 * ����һ��ֵ
	 * 
	 * @param row
	 *            �к�
	 * @param col
	 *            ����
	 * @param value
	 *            ֵ
	 */
	public void setItemValue(int row, int col, String value) {
		if (value == null)
			value = "";
		RecordTrunk rec = this.getRecordThunk(row);
		int dbstatus = rec.getDbstatus();
		String coltype = displaycolumninfos.elementAt(col).getColtype();
		String coldbvalue = rec.getdbValueAt(col);
		if (value == null)
			value = "";
		if (coldbvalue == null)
			coldbvalue = "";
		setValueAt(value, row, col);
		if (dbstatus == RecordTrunk.DBSTATUS_SAVED) {
			if (coltype.equalsIgnoreCase("number")) {
				boolean diff = false;
				if (value.length() > 0 && coldbvalue.length() == 0
						|| value.length() == 0 && coldbvalue.length() > 0) {
					diff = true;
				} else {
					if (DecimalHelper.toDec(value).compareTo(
							DecimalHelper.toDec(coldbvalue)) != 0) {
						diff = true;
					}
				}

				if (diff) {
					if (displaycolumninfos.elementAt(col).isDbcolumn())
						setdbStatus(row, RecordTrunk.DBSTATUS_MODIFIED);
				}
			} else if (coltype.equalsIgnoreCase("date")) {
				if (value.length() == 10 && coldbvalue.length() == 19) {
					// ֻ�Ƚ�ǰ10λ
					if (value.equals(coldbvalue.substring(0, 10))) {
						// ���ݿ��е��ֶ���ʱ��,���õ�ֻ������,û��ʱ��.�������������ݿ�һ��,��Ҫ�޸�
						return;
					}
				}
				if (false == value.equals(coldbvalue)) {
					if (displaycolumninfos.elementAt(col).isDbcolumn())
						setdbStatus(row, RecordTrunk.DBSTATUS_MODIFIED);
				}

			} else {
				if (false == value.equals(coldbvalue)) {
					if (displaycolumninfos.elementAt(col).isDbcolumn())
						setdbStatus(row, RecordTrunk.DBSTATUS_MODIFIED);
				}
			}
		}
	}

	/**
	 * ����Java API����,һ�㲻ʹ��.Ӧʹ��setItemValue()
	 */
	public void setValueAt(Object aValue, int row, int column) {
		// ���CComboxbox����Ҫ����Դ�����aValueתΪkey
		RecordTrunk rec = (RecordTrunk) this.getDataVector().elementAt(row);
		if (aValue instanceof String) {
			rec.setValueAt(column, (String) aValue);
		} else {
			rec.setElementAt(aValue, column);
		}
		/*
		 * DBColumnDisplayInfo colinfo = this.getDisplaycolumninfos().elementAt(
		 * column);
		 * 
		 * JComponent editcomp = colinfo.getEditComponent(); if (editcomp
		 * instanceof CComboBox) { CComboBoxModel cbmodel = (CComboBoxModel)
		 * ((CComboBox) editcomp) .getModel(); String key =
		 * cbmodel.getKey((String) aValue); rec.setElementAt(key, column); }
		 */}

	/**
	 * ����һ��ֵ
	 * 
	 * @param row
	 *            ��
	 * @param colname
	 *            ����
	 * @param value
	 *            ֵ
	 */
	public void setItemValue(int row, String colname, String value) {
		if (value == null)
			value = "";

		int colindex = getColumnindex(colname);
		if (colindex < 0) {
			logger.error("DBTableModel setItemValue �Ҳ�������" + colname);
			return;
		}
		setItemValue(row, colindex, value);
	}

	/**
	 * ���ر༭�ؼ�
	 * 
	 * @param colname
	 * @return
	 */
	public JComponent getEditComp(String colname) {
		int index = this.getColumnindex(colname);
		if (index < 0 || index > displaycolumninfos.size() - 1) {
			logger.error("DBTableModel�Ҳ�����" + colname);
			return null;
		}
		DBColumnDisplayInfo editor = displaycolumninfos.elementAt(index);
		return editor.getEditComponent();
	}

	/**
	 * ������ȡ����
	 * 
	 * @param colindex
	 *            ����
	 * @return ����
	 */
	public String getColumnDBName(int colindex) {
		int index = colindex;
		if (index < 0 || index > displaycolumninfos.size() - 1) {
			return null;
		}
		DBColumnDisplayInfo editor = displaycolumninfos.elementAt(index);
		return editor.getColname();
	}

	/**
	 * ȡ�е����ݿ�����,��number varchar date
	 * 
	 * @param colindex
	 *            ����
	 * @return
	 */
	public String getColumnDBType(int colindex) {
		int index = colindex;
		if (index < 0 || index > displaycolumninfos.size() - 1) {
			return null;
		}
		DBColumnDisplayInfo editor = displaycolumninfos.elementAt(index);
		return editor.getColtype();
	}

	/**
	 * ȡ�е����ݿ�����,��number varchar date
	 * 
	 * @param colname
	 *            ����
	 * @return
	 */
	public String getColumnDBType(String colname) {
		int colindex = this.getColumnindex(colname);
		if (colindex < 0)
			return null;
		return getColumnDBType(colindex);
	}

	/**
	 * ����ĳ�е�״̬.ֵΪRecordTrunk.DBSTATUS_xxxx
	 * 
	 * @param row
	 * @return
	 */
	public int getdbStatus(int row) {
		return getRecordThunk(row).getDbstatus();
	}

	/**
	 * ����ĳ��״̬
	 * 
	 * @param row
	 * @param status
	 *            ֵΪRecordTrunk.DBSTATUS_xxxx
	 */
	public void setdbStatus(int row, int status) {
		this.getRecordThunk(row).setDbstatus(status);
	}

	/**
	 * ��dbmodel�е����ݰ�������ͬΪ����,�����������ݵĺ���
	 * 
	 * @param dbmodel
	 */
	public void bindMemds(DBTableModel dbmodel) {
		int thissize = this.getColumnCount();
		int newsize = dbmodel.getColumnCount();

		ArrayList<Integer> ar = new ArrayList<Integer>();
		for (int c = 0; c < newsize; c++) {
			String cname = dbmodel.getColumnDBName(c);
			int thisindex = this.getColumnindex(cname);
			ar.add(new Integer(thisindex));
		}

		int indexmap[] = new int[newsize];
		for (int i = 0; i < newsize; i++) {
			indexmap[i] = ar.get(i).intValue();
		}

		// �Ӽ�¼
		Vector<RecordTrunk> recs = (Vector<RecordTrunk>) getDataVector();
		RecordTrunk rec = null;
		RecordTrunk thisnewrec = null;
		int thiscolindex;
		for (int r = 0; r < dbmodel.getRowCount(); r++) {
			rec = dbmodel.getRecordThunk(r);
			thisnewrec = new RecordTrunk(thissize);
			for (int c = 0; c < newsize; c++) {
				thiscolindex = indexmap[c];
				if (thiscolindex < 0)
					continue;
				thisnewrec.setElementAt(rec.elementAt(c), thiscolindex);
				thisnewrec
						.setdbValueAt(thiscolindex, (String) rec.elementAt(c));
			}
			thisnewrec.setDbstatus(rec.getDbstatus());
			recs.add(thisnewrec);
		}

	}

	/**
	 * ȡĳ�еļ�¼
	 * 
	 * @param row
	 * @return һ�м�¼
	 */
	public RecordTrunk getRecordThunk(int row) {
		Vector datavector = this.getDataVector();
		Vector tmprec = (Vector) datavector.elementAt(row);

		if (tmprec instanceof RecordTrunk) {
			return (RecordTrunk) tmprec;
		} else {
			logger.error("���ش���DBTableModel�ĳ�ԱӦ����RecordTrunk");

			RecordTrunk rec = new RecordTrunk(tmprec.size());
			for (int i = 0; i < tmprec.size(); i++) {
				rec.setValueAt(i, (String) tmprec.elementAt(i));
			}
			return rec;
		}
	}

	/**
	 * �����޸Ĺ��ļ�¼
	 * 
	 * @return
	 */
	public DBTableModel getModifiedData() {
		DBTableModel saveds = new DBTableModel(displaycolumninfos);
		for (int row = 0; row < getRowCount(); row++) {
			RecordTrunk rec = getRecordThunk(row);
			int status = rec.getDbstatus();
			if (status == RecordTrunk.DBSTATUS_SAVED) {
				continue;
			}
			saveds.addRecord(rec);
		}
		return saveds;
	}

	/**
	 * ����һ��
	 * 
	 * @return
	 */
	public RecordTrunk appendRow() {
		RecordTrunk newrec = new RecordTrunk(this.displaycolumninfos.size());
		this.getDataVector().add(newrec);
		return newrec;
	}

	/**
	 * ɾ�����м�¼
	 */
	public void clearAll() {
		getDataVector().removeAllElements();
	}

	/**
	 * @param rowCount
	 * @deprecated ʹ��appendRow clearAll
	 */
	public void setRowCount(int rowCount) {
		logger.error("donn't call me:setRowCount");
		return;
	}

	/**
	 * ��lineresult�ļ�¼,�滻��lineresult��0����Ԫָ�����к�.
	 * 
	 * @param lineresult
	 */
	public void setLineresult(RecordTrunk lineresult) {
		int row = Integer.parseInt(lineresult.getValueAt(0));
		for (int i = 0; i < lineresult.size(); i++) {
			// lineresult.setdbValueAt(i, (String)lineresult.elementAt(i));
		}

		this.getDataVector().setElementAt(lineresult, row);
	}

	/**
	 * ���÷��������ؽ����.
	 * 
	 * @param results
	 */
	public void setLineresults(Vector<RecordTrunk> results) {
		Enumeration<RecordTrunk> en = results.elements();
		while (en.hasMoreElements()) {
			RecordTrunk lineresult = (RecordTrunk) en.nextElement();
			setLineresult(lineresult);
		}
	}

	/**
	 * ɾ���������ݿ�����ɾ���ļ�¼
	 */
	public void clearDeleted() {
		for (int i = 0; i < getRowCount(); i++) {
			RecordTrunk rec = this.getRecordThunk(i);
			if (1 == rec.getDbdeleted()) {
				getDataVector().removeElementAt(i);
				i--;
			}
		}

	}

	/**
	 * ��ѯĳ�б���Ľ��
	 * 
	 * @param row
	 * @return 0��ʾ�ɹ�
	 */
	public int getResult(int row) {
		RecordTrunk rec = this.getRecordThunk(row);
		if (rec == null) {
			return 0;
		}
		return rec.getSaveresult();
	}

	/**
	 * ����ĳ�б���Ľ����Ϣ
	 * 
	 * @param row
	 * @return ���getResult(row)��Ϊ0 ,�����ش���ԭ��.
	 */
	public String getResultMessage(int row) {
		RecordTrunk rec = this.getRecordThunk(row);
		if (rec == null) {
			return "";
		}
		return rec.getSavemessage();
	}

	/**
	 * Java APIʹ��,��Ҫʹ��.Ӧʹ��getItemValue()
	 */
	@Override
	public Object getValueAt(int row, int column) {
		return getItemValueWithformat(row, column);
	}

	/**
	 * ȡĳ��ֵ
	 * 
	 * @param row
	 * @param colname
	 * @return
	 */
	public String getItemValue(int row, String colname) {
		return getItemValueWithoutformat(row, colname);
	}

	public String getItemValueWithformat(int row, String colname) {
		int col = this.getColumnindex(colname);
		if (col < 0) {
			logger.error("DBTableModel setItemValue �Ҳ�������" + colname);
			return null;
		}
		RecordTrunk rec = this.getRecordThunk(row);
		if (rec == null) {
			return null;
		}
		String value = rec.getValueAt(col);
		DBColumnDisplayInfo colinfo = this.displaycolumninfos.elementAt(col);
		// if (colinfo.getColtype().equals("number")) {
		value = colinfo.getFormatvalue(value);
		// }
		return value;
	}

	/**
	 * ȡֵ��number���Ͳ���format
	 * 
	 * @param row
	 * @param colname
	 * @return
	 */
	public String getItemValueWithoutformat(int row, String colname) {
		int col = this.getColumnindex(colname);
		if (col < 0) {
			logger.error("DBTableModel setItemValue �Ҳ�������" + colname);
			return null;
		}
		RecordTrunk rec = this.getRecordThunk(row);
		if (rec == null) {
			return null;
		}
		String value = rec.getValueAt(col);
		DBColumnDisplayInfo colinfo = this.displaycolumninfos.elementAt(col);
		if (colinfo.getColtype().equals("number")) {
			value = value.replaceAll(",", "");
		}
		return value;

	}

	/**
	 * ȡĳ��ֵ
	 * 
	 * @param row
	 *            ��
	 * @param col
	 *            ��
	 * @return
	 */
	public String getItemValue(int row, int col) {
		return getItemValueWithoutformat(row, col);
	}

	public String getItemValueWithformat(int row, int col) {
		if (col < 0)
			return null;
		RecordTrunk rec = this.getRecordThunk(row);
		if (rec == null) {
			return null;
		}
		String value = rec.getValueAt(col);
		DBColumnDisplayInfo colinfo = this.displaycolumninfos.elementAt(col);
		if (colinfo.getColtype().equals("number")) {
			value = colinfo.getFormatvalue(value);
		}
		if (value.indexOf(",") >= 0) {
			int m;
			m = 3;
		}
		return value;
	}

	/**
	 * ȡֵ����format
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public String getItemValueWithoutformat(int row, int col) {
		if (col < 0)
			return null;
		RecordTrunk rec = this.getRecordThunk(row);
		if (rec == null) {
			return null;
		}
		String value = rec.getValueAt(col);

		DBColumnDisplayInfo colinfo = this.displaycolumninfos.elementAt(col);
		if (colinfo.getColtype().equals("number")) {
			value = value.replaceAll(",", "");
		}
		return value;
	}

	/**
	 * �����������out
	 * 
	 * @param out
	 * @throws Exception
	 */
	public void writeData(OutputStream out) throws Exception {
		CommandFactory.writeShort(displaycolumninfos.size(), out);
		Enumeration<DBColumnDisplayInfo> en = displaycolumninfos.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo info = en.nextElement();
			info.writeData(out);
		}
		// �����¼
		CommandFactory.writeShort(this.getRowCount(), out);
		for (int i = 0; i < getRowCount(); i++) {
			RecordTrunk rec = this.getRecordThunk(i);
			rec.writeData(out);
		}
		CommandFactory.writeShort(hasmore, out);
	}

	/**
	 * ��in����һ��dbmodel
	 * 
	 * @param in
	 * @throws Exception
	 */
	public void readData(InputStream in) throws Exception {
		int colct = CommandFactory.readShort(in);
		if (displaycolumninfos == null) {
			displaycolumninfos = new Vector<DBColumnDisplayInfo>();
		}
		displaycolumninfos.clear();
		for (int i = 0; i < colct; i++) {
			DBColumnDisplayInfo info = new DBColumnDisplayInfo("", "");
			info.readData(in);
			displaycolumninfos.add(info);
		}
		int recct = CommandFactory.readShort(in);
		for (int i = 0; i < recct; i++) {
			RecordTrunk rec = RecordTrunk.readData(in);
			this.addRecord(rec);
		}
		hasmore = CommandFactory.readShort(in);
	}

	/**
	 * �����Ƿ��м�¼
	 * 
	 * @return
	 */
	public boolean hasmore() {
		return hasmore == 1;
	}

	/**
	 * �����Ƿ��м�¼
	 * 
	 * @param hasmore
	 */
	public void setHasmore(boolean hasmore) {
		this.hasmore = hasmore ? 1 : 0;
	}

	/**
	 * ��һ�м�¼.������еĴ���
	 * 
	 * @param rec
	 */
	public void addRecord(RecordTrunk rec) {
		this.getDataVector().add(rec);
	}

	/**
	 * ����ĳ��ĳ���������ǲ����޸Ĺ���
	 * 
	 * @param row
	 * @param colname
	 * @return
	 */
	public boolean isColumnmodified(int row, String colname) {
		int col = this.getColumnindex(colname);
		if (col < 0)
			return false;
		RecordTrunk rec = this.getRecordThunk(row);
		return rec.isColumnmodified(col);
	}

	/**
	 * �Ƿ����ڲ�ѯ
	 */
	boolean isquerying = false;

	/**
	 * ��ѯsql
	 */
	String sql = "";

	/**
	 * ��ѯ�߳�
	 */
	QueryThread querythread = null;

	/**
	 * �����Ƿ����ڲ�ѯ
	 * 
	 * @return
	 */
	public boolean isquerying() {
		return isquerying;
	}

	/**
	 * һ�β�ѯ��෵�صļ�¼��
	 */
	int maxrowcount = 100;

	/**
	 * ��ѯ����
	 * 
	 * @param sql
	 *            ��ѯselect ���
	 * @param maxrowcount
	 *            ����ѯ������
	 * @return
	 */
	public boolean doRetrieve(String sql, int maxrowcount) {
		if (isquerying) {
			return false;
		}
		isquerying = true;

		clearAll();

		this.sql = sql;
		this.maxrowcount = maxrowcount;

		querythread = new QueryThread();
		querythread.setDaemon(true);
		if (usequerythread) {
			querythread.start();
		} else {
			querythread.run();
		}
		return true;

	}

	/**
	 * ��ѯ������
	 */
	DBTableModelEvent retrievelistener = null;

	/**
	 * ���ز�ѯ������
	 * 
	 * @return
	 */
	public DBTableModelEvent getRetrievelistener() {
		return retrievelistener;
	}

	/**
	 * ���ò�ѯ������
	 * 
	 * @param retrievelistener
	 */
	public void setRetrievelistener(DBTableModelEvent retrievelistener) {
		this.retrievelistener = retrievelistener;
	}

	/**
	 * ��ֹ��ѯ
	 */
	public void stopQuery() {
		if (isquerying) {
			querythread.cancelRetrieve = true;
		}
	}

	/**
	 * �����޸�
	 * 
	 * @param row
	 */
	public void undo(int row) {
		RecordTrunk rec = getRecordThunk(row);
		if (rec.getDbstatus() == RecordTrunk.DBSTATUS_NEW) {
			this.getDataVector().removeElementAt(row);
			return;
		} else {
			// ��Ϊ���ݿ�ֵ
			for (int i = 0; i < rec.size(); i++) {
				rec.setValueAt(i, rec.getdbValueAt(i));
			}
			rec.setDbstatus(RecordTrunk.DBSTATUS_SAVED);
			rec.setSaveresult(0, "");
		}

	}

	/**
	 * ���������
	 */
	private int lastsortcol = -1;

	/**
	 * ��������Ƿ�����
	 */
	private boolean lastasc = false;

	/**
	 * ����
	 * 
	 * @param colindex
	 *            ����
	 * @param asc
	 *            �Ƿ�����
	 */
	public void sort(int colindex, boolean asc) {
		if (lastsortcol == colindex && lastasc == asc) {
			return;
		}
		sorter = new MSort(this, colindex, asc);
		sorter.sort();
		lastsortcol = colindex;
		lastasc = asc;
		fireSorted();
	}

	/**
	 * ���ϴ����򷽷�������
	 */
	public void resort() {
		if (sorter != null) {
			sorter.sort();
		}
		fireSorted();
	}

	/**
	 * ��������ѯ�߳�
	 * 
	 * @author Administrator
	 * 
	 */
	class QueryThread extends Thread {
		boolean cancelRetrieve = false;

		public void run() {
			cancelRetrieve = false;
			if (retrievelistener != null) {
				if (EventQueue.isDispatchThread()) {
					retrievelistener.retrieveStart(DBTableModel.this);
				} else {
					Runnable r = new Runnable() {
						public void run() {
							retrievelistener.retrieveStart(DBTableModel.this);
						}
					};
					try {
						SwingUtilities.invokeAndWait(r);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
			retrieveData();
			isquerying = false;
		}

		protected void retrieveData() {
			// setStatusmessage("��ʼ��ѯ.........");
			try {
				RemotesqlHelper rmtsqlhelper = new RemotesqlHelper();

				int startrow = 0;
				while (isquerying && !cancelRetrieve) {
					DBTableModel memds = null;
					int maxtrycount = 3;
					int trycount = 0;
					String lasterror = "";
					while (isquerying && !cancelRetrieve
							&& trycount < maxtrycount) {
						try {
							trycount++;
							memds = rmtsqlhelper.doSelect(sql, startrow,
									maxrowcount);

							// ģ�����
							// try {
							// Thread.sleep(3000);
							// } catch (InterruptedException e) {
							// TODO Auto-generated catch block
							// e.printStackTrace();
							// }

							bindMemds(memds);
							if (retrievelistener != null) {
								final int tmp_startrow = startrow;
								final int tmp_endrow = startrow;
								final int tmp_retrievesize = rmtsqlhelper
										.getRetrievesize();
								final int tmp_inflatsize = rmtsqlhelper
										.getInflatSize();
								if (EventQueue.isDispatchThread()) {
									int ret = retrievelistener.retrievePart(
											DBTableModel.this, tmp_startrow,
											tmp_endrow, tmp_retrievesize,
											tmp_inflatsize);
									if (ret != 0) {
										cancelRetrieve = true;
										break;
									}
								} else {
									Runnable r = new Runnable() {
										public void run() {
											int ret = retrievelistener
													.retrievePart(
															DBTableModel.this,
															tmp_startrow,
															tmp_endrow,
															tmp_retrievesize,
															tmp_inflatsize);

											if (ret != 0) {
												cancelRetrieve = true;
											}
										}
									};
									SwingUtilities.invokeAndWait(r);
								}
							}
							break;
						} catch (Exception e) {
							logger.error("���ʹ���", e);
							lasterror = e.getMessage();
							if (e.getMessage().startsWith("����������")) {
								final String errmsg = e.getMessage();
								if (EventQueue.isDispatchThread()) {
									retrievelistener.retrieveError(
											DBTableModel.this, errmsg);
								} else {
									if (retrievelistener != null) {
										Runnable r = new Runnable() {
											public void run() {
												retrievelistener.retrieveError(
														DBTableModel.this,
														errmsg);
											}
										};
										SwingUtilities.invokeAndWait(r);
									}
								}
								return;
							}
						}
					}

					if (memds == null) {
						if (retrievelistener != null) {
							final String tmp_lasterror = lasterror;
							if (EventQueue.isDispatchThread()) {
								retrievelistener.retrieveError(
										DBTableModel.this, tmp_lasterror);
							} else {
								Runnable r = new Runnable() {
									public void run() {
										retrievelistener.retrieveError(
												DBTableModel.this,
												tmp_lasterror);
									}
								};
								SwingUtilities.invokeAndWait(r);
							}
						}
						return;
					}

					if (false == memds.hasmore()) {
						break;
					}
					startrow += memds.getRowCount();
				}
				// setStatusmessage("��ѯ��"+memds.getRowCount()+"����¼,����������¼�ɼ�������");
				if (retrievelistener != null) {
					// �ڵ��� retrieveFinish()ǰ��isquerying��Ϊfalse��20080421
					isquerying = false;
					// logger.debug("dbtablemodel
					// retrieveFinish,this="+DBTableModel.this);
					if (EventQueue.isDispatchThread()) {
						retrievelistener.retrieveFinish(DBTableModel.this);
					} else {
						Runnable r = new Runnable() {
							public void run() {
								retrievelistener
										.retrieveFinish(DBTableModel.this);
							}
						};
						SwingUtilities.invokeAndWait(r);
					}
				}
				if (cancelRetrieve) {
					return;
				}
			} catch (Exception e) {
				logger.error("��ѯ��", e);
			}
		}

	}

	/**
	 * �����Ƿ��ڲ�ѯ
	 * 
	 * @param isquerying
	 */
	public void setIsquerying(boolean isquerying) {
		this.isquerying = isquerying;
	}

	/**
	 * ���
	 * 
	 * @param colname
	 * @return ����ֵ�ĺ�
	 */
	public BigDecimal sum(String colname) {
		// ԭ����Ч��̫��,���±�д
		int colindex = getColumnindex(colname);
		if (colindex < 0) {
			return new BigDecimal(0);
		}
		BigDecimal sum = new BigDecimal(0);
		Enumeration<RecordTrunk> en = getDataVector().elements();
		String v;
		BigDecimal tmpd;
		while (en.hasMoreElements()) {
			RecordTrunk rec = en.nextElement();
			if (rec.getDbstatus() == RecordTrunk.DBSTATUS_DELETE)
				continue;
			v = (String) rec.elementAt(colindex);
			if (v == null || v.length() == 0)
				continue;
			try {
				tmpd = DecimalHelper.toDec(v);
				sum = sum.add(tmpd);
			} catch (Exception e) {
			}
		}
		return sum;

		/*
		 * BigDecimal sum = new BigDecimal(0.00); sum.setScale(1); for (int r =
		 * 0; r < getRowCount(); r++) { String itemValue=""; try { itemValue =
		 * getItemValue(r,colname); BigDecimal decv = new BigDecimal(itemValue);
		 * sum = sum.add(decv); } catch (Exception e) { } }
		 * 
		 * return sum;
		 */
	}

	/**
	 * ����ĳ��ĳ���Ƿ�ɱ༭
	 */
	public boolean isCellEditable(int row, int column) {
		if (row >= 0 && row < this.getDataVector().size()) {
			RecordTrunk rec = this.getRecordThunk(row);
			if (rec.getSumflag() != RecordTrunk.SUMFLAG_RECORD) {
				return false;
			}
		}
		DBColumnDisplayInfo colinfo = displaycolumninfos.elementAt(column);
		return !colinfo.isReadonly();
	}

	/**
	 * һ�����ݱ仯��
	 * 
	 * @param row
	 */
	public void fireRowDatachanged(int row) {

	}

	/**
	 * ��¼����ɾ
	 */
	public void fireDatachanged() {

	}

	/**
	 * ����������ؽ�dbmodel�ļ�¼���ڱ�dbmodel����,bindMemds()�Ǽ��������
	 * 
	 * @param dbmodel
	 */
	public void appendDbmodel(DBTableModel dbmodel) {
		Vector recs = this.getDataVector();
		Enumeration en = dbmodel.getDataVector().elements();
		while (en.hasMoreElements()) {
			RecordTrunk rec = (RecordTrunk) en.nextElement();
			recs.add(rec);
		}
	}

	/**
	 * ��һ����¼,���������
	 * 
	 * @param rec
	 */
	public void appendRecord(RecordTrunk rec) {
		getDataVector().add(rec);
	}

	/**
	 * ��rowǰ����
	 * 
	 * @param rec
	 * @param row
	 */
	public void insertRecord(RecordTrunk rec, int row) {
		getDataVector().insertElementAt(rec, row);
	}

	/**
	 * ����һ����ͬ�ṹ��dbmodel,������
	 * 
	 * @return
	 */
	public DBTableModel copyStruct() {
		return new DBTableModel(this.displaycolumninfos);
	}

	/**
	 * ������������
	 * 
	 * @param sortcolumns
	 *            ������
	 * @param sortasc
	 *            �Ƿ�����
	 */
	public void sort(String[] sortcolumns, boolean sortasc) {
		MSort1 sorter = new MSort1(this, sortcolumns, sortasc);
		sorter.sort();
		fireSorted();
	}

	/**
	 * ������ʽ
	 * 
	 * @param expr
	 *            ����:asc|desc[:����:asc|desc] asc��ʾ����,desc��ʾ����
	 *            ����:goodsid:desc:goodsname:asc ��ʾ�Ȱ�goodsid�������ٰ�goodsname������
	 */
	public void sort(String expr) throws Exception {
		String ss[] = expr.split(":");
		if (ss == null || ss.length % 2 != 0) {
			throw new Exception("������ʽ����,ӦΪ\"����:asc|desc[:����:asc|desc]\"");
		}
		String sortcolumns[] = new String[ss.length / 2];
		boolean sortascs[] = new boolean[ss.length / 2];

		for (int i = 0; i < ss.length; i++) {
			String colname = ss[i];
			String strasc = ss[i + 1];
			sortcolumns[i / 2] = colname;
			sortascs[i / 2] = strasc.equals("asc");
			i++;
		}

		MSort2 sorter = new MSort2(this, sortcolumns, sortascs);
		sorter.sort();
		fireSorted();
	}

	/**
	 * ��������dbmodel�����ݲ�����dbmodel��
	 * 
	 * @param dbmodel
	 *            public void appendDatabyColname(DBTableModel dbmodel) {
	 *            for(int r=0;r<dbmodel.getRowCount();r++){ int
	 *            newrow=this.getRowCount(); this.appendRow();
	 *            Enumeration<DBColumnDisplayInfo>
	 *            en=displaycolumninfos.elements(); while(en.hasMoreElements()){
	 *            DBColumnDisplayInfo colinfo=en.nextElement(); String
	 *            v=dbmodel.getItemValue(r,colinfo.getColname()); if(v!=null){
	 *            setItemValue(newrow,colinfo.getColname(),v); } } } }
	 */

	/**
	 * �����������,������ʱ������
	 */

	Random tmppkrandom = new Random(System.currentTimeMillis());

	/**
	 * �������ɵ���ʱ����.���õ�ǰʱ�����
	 * 
	 * @param row
	 * @return
	 */
	public String getTmppkid(int row) {
		RecordTrunk rec = getRecordThunk(row);
		String tmppkid = rec.getTmppkid();
		if (tmppkid != null && tmppkid.length() > 0)
			return tmppkid;

		// ���Ϊ��,����һ��
		tmppkid = String.valueOf(System.currentTimeMillis()
				+ tmppkrandom.nextInt(10000))
				+ String.valueOf(row);
		rec.setTmppkid(tmppkid);
		return tmppkid;

	}

	/**
	 * �����ڴ�,���ú�,���಻����ʹ����.�������ƻ���
	 */
	public void freeMemory() {
		clearAll();
		if (displaycolumninfos != null) {
			Enumeration<DBColumnDisplayInfo> en = displaycolumninfos.elements();
			while (en.hasMoreElements()) {
				DBColumnDisplayInfo colinfo = en.nextElement();
				colinfo.freeMemory();
			}
			displaycolumninfos.removeAllElements();
			displaycolumninfos = null;
		}
		displaycolumninfos = null;
		sorter = null;
		querythread = null;
	}

	/**
	 * ����һ�м�¼���ļ�������Ϣ
	 * 
	 * @param row
	 * @param filedbmodel
	 */
	public void setFiledbmodel(int row, DBTableModel filedbmodel) {
		RecordTrunk rec = this.getRecordThunk(row);
		rec.setFiledbmodel(filedbmodel);
	}

	/**
	 * ȡһ�м�¼���ļ�������Ϣ
	 * 
	 * @param row
	 * @return
	 */
	public DBTableModel getFiledbmodel(int row) {
		RecordTrunk rec = this.getRecordThunk(row);
		return rec.getFiledbmodel();
	}

	/**
	 * ���Ҫ�ϴ����ļ�
	 */
	public void resetWantuploadfiles() {
		for (int i = 0; i < this.getRowCount(); i++) {
			getRecordThunk(i).getWantuploadfiles().removeAllElements();
		}
	}

	private boolean crosstable;

	public boolean isCrosstable() {
		return crosstable;
	}

	public void setCrosstable(boolean crosstable) {
		this.crosstable = crosstable;
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

	public String getPkcolname() {
		Enumeration<DBColumnDisplayInfo> en = displaycolumninfos.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo col = en.nextElement();
			if (col.ispk) {
				return col.getColname();
			}
		}
		return null;
	}

	/**
	 * ����
	 * 
	 * @param colname
	 *            ����
	 * @param target
	 *            �ҵ�����
	 * @return �к�
	 */
	public int searchColumnvalue(String colname, String target) {
		for (int i = 0; i < getRowCount(); i++) {
			if (getItemValue(i, colname).equals(target))
				return i;
		}
		return -1;
	}

	public boolean isUsequerythread() {
		return usequerythread;
	}

	public void setUsequerythread(boolean usequerythread) {
		this.usequerythread = usequerythread;
	}

	/**
	 * �����һ�����ù��п���true
	 * @return
	 */
	public boolean isFixtablecolumnwidth(){
		Enumeration<DBColumnDisplayInfo> en=displaycolumninfos.elements();
		while(en.hasMoreElements()){
			if(en.nextElement().getTablecolumnwidth()>=0)return true;
		}
		return false;
	}
}
