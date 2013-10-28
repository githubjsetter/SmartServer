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
 * 内存数据源.可以包含多条记录.
 */
public class DBTableModel extends DefaultTableModel {

	/**
	 * 是否用线程查询
	 */
	boolean usequerythread = true;

	/**
	 * 列定义
	 */
	protected Vector<DBColumnDisplayInfo> displaycolumninfos = new Vector<DBColumnDisplayInfo>();

	/**
	 * 还有更多记录.服务器返回是不是还有其它记录
	 */
	int hasmore = 0;

	/**
	 * 排序器
	 */
	private MSort sorter;

	private Vector<DbtablemodelSortListener> sortlistener = new Vector<DbtablemodelSortListener>();

	/**
	 * 构造.一般不用这个方法
	 */
	public DBTableModel() {
	}

	/**
	 * 构造函数
	 * 
	 * @param displaycolumninfos
	 *            列定义
	 */
	public DBTableModel(Vector<DBColumnDisplayInfo> displaycolumninfos) {
		this.displaycolumninfos = displaycolumninfos;
		finishBuild();
	}

	/**
	 * 增加列的定义
	 * 
	 * @param info
	 */
	public void addDisplayColuminfo(DBColumnDisplayInfo info) {
		displaycolumninfos.add(info);
	}

	/**
	 * 取列的定义
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
	 * 返回某列定义
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
	 * 设置列的定义
	 * 
	 * @param displaycolumninfos
	 */
	public void setDisplaycolumninfos(
			Vector<DBColumnDisplayInfo> displaycolumninfos) {
		this.displaycolumninfos = displaycolumninfos;
	}

	/**
	 * 取列的数量
	 */
	public int getColumnCount() {
		if (displaycolumninfos == null)
			return 0;
		return displaycolumninfos.size();
	}

	/**
	 * 内部函数,完成数据源的构造
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
	 * 由列名查询列序
	 * 
	 * @param colname
	 *            列名
	 * @return 列序
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
		//logger.error("找不到列名=" + colname);
		return -1;
	}

	Category logger = Category.getInstance(DBTableModel.class);

	/**
	 * 设置一项值
	 * 
	 * @param row
	 *            行号
	 * @param col
	 *            列序
	 * @param value
	 *            值
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
					// 只比较前10位
					if (value.equals(coldbvalue.substring(0, 10))) {
						// 数据库中的字段有时间,设置的只有日期,没有时间.并且日期与数据库一致,不要修改
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
	 * 重载Java API函数,一般不使用.应使用setItemValue()
	 */
	public void setValueAt(Object aValue, int row, int column) {
		// 如果CComboxbox，需要区别对待。将aValue转为key
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
	 * 设置一项值
	 * 
	 * @param row
	 *            行
	 * @param colname
	 *            列名
	 * @param value
	 *            值
	 */
	public void setItemValue(int row, String colname, String value) {
		if (value == null)
			value = "";

		int colindex = getColumnindex(colname);
		if (colindex < 0) {
			logger.error("DBTableModel setItemValue 找不到列名" + colname);
			return;
		}
		setItemValue(row, colindex, value);
	}

	/**
	 * 返回编辑控件
	 * 
	 * @param colname
	 * @return
	 */
	public JComponent getEditComp(String colname) {
		int index = this.getColumnindex(colname);
		if (index < 0 || index > displaycolumninfos.size() - 1) {
			logger.error("DBTableModel找不到列" + colname);
			return null;
		}
		DBColumnDisplayInfo editor = displaycolumninfos.elementAt(index);
		return editor.getEditComponent();
	}

	/**
	 * 由列序取列名
	 * 
	 * @param colindex
	 *            列序
	 * @return 列名
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
	 * 取列的数据库类型,如number varchar date
	 * 
	 * @param colindex
	 *            列序
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
	 * 取列的数据库类型,如number varchar date
	 * 
	 * @param colname
	 *            列名
	 * @return
	 */
	public String getColumnDBType(String colname) {
		int colindex = this.getColumnindex(colname);
		if (colindex < 0)
			return null;
		return getColumnDBType(colindex);
	}

	/**
	 * 返回某行的状态.值为RecordTrunk.DBSTATUS_xxxx
	 * 
	 * @param row
	 * @return
	 */
	public int getdbStatus(int row) {
		return getRecordThunk(row).getDbstatus();
	}

	/**
	 * 设置某行状态
	 * 
	 * @param row
	 * @param status
	 *            值为RecordTrunk.DBSTATUS_xxxx
	 */
	public void setdbStatus(int row, int status) {
		this.getRecordThunk(row).setDbstatus(status);
	}

	/**
	 * 将dbmodel中的数据按列名相同为规则,补在现在数据的后面
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

		// 加记录
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
	 * 取某行的记录
	 * 
	 * @param row
	 * @return 一行记录
	 */
	public RecordTrunk getRecordThunk(int row) {
		Vector datavector = this.getDataVector();
		Vector tmprec = (Vector) datavector.elementAt(row);

		if (tmprec instanceof RecordTrunk) {
			return (RecordTrunk) tmprec;
		} else {
			logger.error("严重错误DBTableModel的成员应该是RecordTrunk");

			RecordTrunk rec = new RecordTrunk(tmprec.size());
			for (int i = 0; i < tmprec.size(); i++) {
				rec.setValueAt(i, (String) tmprec.elementAt(i));
			}
			return rec;
		}
	}

	/**
	 * 返回修改过的记录
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
	 * 新增一行
	 * 
	 * @return
	 */
	public RecordTrunk appendRow() {
		RecordTrunk newrec = new RecordTrunk(this.displaycolumninfos.size());
		this.getDataVector().add(newrec);
		return newrec;
	}

	/**
	 * 删除所有记录
	 */
	public void clearAll() {
		getDataVector().removeAllElements();
	}

	/**
	 * @param rowCount
	 * @deprecated 使用appendRow clearAll
	 */
	public void setRowCount(int rowCount) {
		logger.error("donn't call me:setRowCount");
		return;
	}

	/**
	 * 将lineresult的记录,替换掉lineresult第0个单元指定的行号.
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
	 * 设置服务器返回结果集.
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
	 * 删除所有数据库中已删除的记录
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
	 * 查询某行保存的结果
	 * 
	 * @param row
	 * @return 0表示成功
	 */
	public int getResult(int row) {
		RecordTrunk rec = this.getRecordThunk(row);
		if (rec == null) {
			return 0;
		}
		return rec.getSaveresult();
	}

	/**
	 * 设置某行保存的结果信息
	 * 
	 * @param row
	 * @return 如果getResult(row)不为0 ,将返回错误原因.
	 */
	public String getResultMessage(int row) {
		RecordTrunk rec = this.getRecordThunk(row);
		if (rec == null) {
			return "";
		}
		return rec.getSavemessage();
	}

	/**
	 * Java API使用,不要使用.应使用getItemValue()
	 */
	@Override
	public Object getValueAt(int row, int column) {
		return getItemValueWithformat(row, column);
	}

	/**
	 * 取某项值
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
			logger.error("DBTableModel setItemValue 找不到列名" + colname);
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
	 * 取值，number类型不带format
	 * 
	 * @param row
	 * @param colname
	 * @return
	 */
	public String getItemValueWithoutformat(int row, String colname) {
		int col = this.getColumnindex(colname);
		if (col < 0) {
			logger.error("DBTableModel setItemValue 找不到列名" + colname);
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
	 * 取某项值
	 * 
	 * @param row
	 *            行
	 * @param col
	 *            列
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
	 * 取值不带format
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
	 * 将数据输出到out
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
		// 输出记录
		CommandFactory.writeShort(this.getRowCount(), out);
		for (int i = 0; i < getRowCount(); i++) {
			RecordTrunk rec = this.getRecordThunk(i);
			rec.writeData(out);
		}
		CommandFactory.writeShort(hasmore, out);
	}

	/**
	 * 从in构造一个dbmodel
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
	 * 返回是否还有记录
	 * 
	 * @return
	 */
	public boolean hasmore() {
		return hasmore == 1;
	}

	/**
	 * 设置是否还有记录
	 * 
	 * @param hasmore
	 */
	public void setHasmore(boolean hasmore) {
		this.hasmore = hasmore ? 1 : 0;
	}

	/**
	 * 补一行记录.不检查列的次序
	 * 
	 * @param rec
	 */
	public void addRecord(RecordTrunk rec) {
		this.getDataVector().add(rec);
	}

	/**
	 * 返回某行某列数据项是不是修改过了
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
	 * 是否正在查询
	 */
	boolean isquerying = false;

	/**
	 * 查询sql
	 */
	String sql = "";

	/**
	 * 查询线程
	 */
	QueryThread querythread = null;

	/**
	 * 返回是否正在查询
	 * 
	 * @return
	 */
	public boolean isquerying() {
		return isquerying;
	}

	/**
	 * 一次查询最多返回的记录数
	 */
	int maxrowcount = 100;

	/**
	 * 查询数据
	 * 
	 * @param sql
	 *            查询select 语句
	 * @param maxrowcount
	 *            最多查询的行数
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
	 * 查询监听器
	 */
	DBTableModelEvent retrievelistener = null;

	/**
	 * 返回查询监听器
	 * 
	 * @return
	 */
	public DBTableModelEvent getRetrievelistener() {
		return retrievelistener;
	}

	/**
	 * 设置查询监听器
	 * 
	 * @param retrievelistener
	 */
	public void setRetrievelistener(DBTableModelEvent retrievelistener) {
		this.retrievelistener = retrievelistener;
	}

	/**
	 * 中止查询
	 */
	public void stopQuery() {
		if (isquerying) {
			querythread.cancelRetrieve = true;
		}
	}

	/**
	 * 撤消修改
	 * 
	 * @param row
	 */
	public void undo(int row) {
		RecordTrunk rec = getRecordThunk(row);
		if (rec.getDbstatus() == RecordTrunk.DBSTATUS_NEW) {
			this.getDataVector().removeElementAt(row);
			return;
		} else {
			// 设为数据库值
			for (int i = 0; i < rec.size(); i++) {
				rec.setValueAt(i, rec.getdbValueAt(i));
			}
			rec.setDbstatus(RecordTrunk.DBSTATUS_SAVED);
			rec.setSaveresult(0, "");
		}

	}

	/**
	 * 最后排序列
	 */
	private int lastsortcol = -1;

	/**
	 * 最后排序是否增序
	 */
	private boolean lastasc = false;

	/**
	 * 排序
	 * 
	 * @param colindex
	 *            列序
	 * @param asc
	 *            是否增序
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
	 * 按上次排序方法再排序
	 */
	public void resort() {
		if (sorter != null) {
			sorter.sort();
		}
		fireSorted();
	}

	/**
	 * 服务器查询线程
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
			// setStatusmessage("开始查询.........");
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

							// 模拟很慢
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
							logger.error("传送错误", e);
							lasterror = e.getMessage();
							if (e.getMessage().startsWith("服务器返回")) {
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
				// setStatusmessage("查询到"+memds.getRowCount()+"条记录,还有其它记录可继续下载");
				if (retrievelistener != null) {
					// 在调用 retrieveFinish()前将isquerying置为false。20080421
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
				logger.error("查询错", e);
			}
		}

	}

	/**
	 * 设置是否在查询
	 * 
	 * @param isquerying
	 */
	public void setIsquerying(boolean isquerying) {
		this.isquerying = isquerying;
	}

	/**
	 * 求和
	 * 
	 * @param colname
	 * @return 这列值的和
	 */
	public BigDecimal sum(String colname) {
		// 原函数效率太低,重新编写
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
	 * 返回某行某列是否可编辑
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
	 * 一行数据变化。
	 * 
	 * @param row
	 */
	public void fireRowDatachanged(int row) {

	}

	/**
	 * 记录增或删
	 */
	public void fireDatachanged() {

	}

	/**
	 * 不检查列名地将dbmodel的记录补在本dbmodel后面,bindMemds()是检查列名的
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
	 * 补一条记录,不检查列名
	 * 
	 * @param rec
	 */
	public void appendRecord(RecordTrunk rec) {
		getDataVector().add(rec);
	}

	/**
	 * 在row前插入
	 * 
	 * @param rec
	 * @param row
	 */
	public void insertRecord(RecordTrunk rec, int row) {
		getDataVector().insertElementAt(rec, row);
	}

	/**
	 * 复制一个相同结构的dbmodel,无数据
	 * 
	 * @return
	 */
	public DBTableModel copyStruct() {
		return new DBTableModel(this.displaycolumninfos);
	}

	/**
	 * 按多列名排序
	 * 
	 * @param sortcolumns
	 *            多列名
	 * @param sortasc
	 *            是否增序
	 */
	public void sort(String[] sortcolumns, boolean sortasc) {
		MSort1 sorter = new MSort1(this, sortcolumns, sortasc);
		sorter.sort();
		fireSorted();
	}

	/**
	 * 排序表达式
	 * 
	 * @param expr
	 *            列名:asc|desc[:列名:asc|desc] asc表示增序,desc表示降序
	 *            例如:goodsid:desc:goodsname:asc 表示先按goodsid降序排再按goodsname增序排
	 */
	public void sort(String expr) throws Exception {
		String ss[] = expr.split(":");
		if (ss == null || ss.length % 2 != 0) {
			throw new Exception("排序表达式错误,应为\"列名:asc|desc[:列名:asc|desc]\"");
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
	 * 按列名将dbmodel的数据补到本dbmodel中
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
	 * 随机数发生器,生成临时主键用
	 */

	Random tmppkrandom = new Random(System.currentTimeMillis());

	/**
	 * 返回生成的临时主键.利用当前时间加随
	 * 
	 * @param row
	 * @return
	 */
	public String getTmppkid(int row) {
		RecordTrunk rec = getRecordThunk(row);
		String tmppkid = rec.getTmppkid();
		if (tmppkid != null && tmppkid.length() > 0)
			return tmppkid;

		// 如果为空,生成一个
		tmppkid = String.valueOf(System.currentTimeMillis()
				+ tmppkrandom.nextInt(10000))
				+ String.valueOf(row);
		rec.setTmppkid(tmppkid);
		return tmppkid;

	}

	/**
	 * 清理内存,调用后,本类不能再使用了.被彻底破坏了
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
	 * 设置一行记录的文件附件信息
	 * 
	 * @param row
	 * @param filedbmodel
	 */
	public void setFiledbmodel(int row, DBTableModel filedbmodel) {
		RecordTrunk rec = this.getRecordThunk(row);
		rec.setFiledbmodel(filedbmodel);
	}

	/**
	 * 取一行记录的文件附件信息
	 * 
	 * @param row
	 * @return
	 */
	public DBTableModel getFiledbmodel(int row) {
		RecordTrunk rec = this.getRecordThunk(row);
		return rec.getFiledbmodel();
	}

	/**
	 * 清除要上传的文件
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
	 * 搜索
	 * 
	 * @param colname
	 *            列名
	 * @param target
	 *            找的内容
	 * @return 行号
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
	 * 如果有一列设置过列宽返回true
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
