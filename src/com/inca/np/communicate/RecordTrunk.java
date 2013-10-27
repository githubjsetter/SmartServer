package com.inca.np.communicate;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Vector;

import com.inca.np.gui.control.CComboBoxModel;
import com.inca.np.gui.control.DBTableModel;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-2-27 Time: 9:56:13
 * 表达一条记录
 */
public class RecordTrunk extends Vector {
	/**
	 * 已保存
	 */
	public static final int DBSTATUS_SAVED = 0;

	/**
	 * 新增未保存
	 */
	public static final int DBSTATUS_NEW = 1;

	/**
	 * 查询出，并修改
	 */
	public static final int DBSTATUS_MODIFIED = 2;

	/**
	 * 查询出，并删除
	 */

	public static final int DBSTATUS_DELETE = 3;

	/**
	 * 普通记录
	 */
	public static final int SUMFLAG_RECORD = 0;

	/**
	 * 合计
	 */
	public static final int SUMFLAG_SUMMARY = 9999;

	/**
	 * 第n组的组合计
	 */

	/**
	 * 数据库值
	 */
	Vector<String> dbvalues = new Vector<String>();

	int dbstatus = DBSTATUS_NEW;

	/**
	 * 用于保存总单细目的临时相关关系
	 */
	String relatevalue = "";

	/**
	 * 已在数据库中删除
	 */
	int dbdeleted = 0;

	int sumflag = SUMFLAG_RECORD;

	/**
	 * 主键列值.如果是新增,使用系统时间和随机数临时生成一个. 用于总单细目进行关联
	 */
	String tmppkid = "";
	
	/**
	 * 组合计行。
	 */
	String groupname="";

	/**
	 * 构造.
	 * 
	 * @param fieldcount
	 *            列数量
	 */
	public RecordTrunk(int fieldcount) {
		setSize(fieldcount);
		dbvalues = new Vector<String>();
		dbvalues.setSize(fieldcount);
		for (int i = 0; i < fieldcount; i++) {
			setElementAt("", i);
			dbvalues.setElementAt("", i);
		}
	}

	/**
	 * 设置值.第col个位置的值为v
	 * 
	 * @param col
	 * @param v
	 */
	public void setValueAt(int col, String v) {
		if (v == null)
			v = "";
		setElementAt(v, col);
	}

	/**
	 * 取关联值,用于总单细目临时记录对应关系
	 * 
	 * @return
	 */
	public String getRelatevalue() {
		return relatevalue;
	}

	/**
	 * 设置相关列的值
	 * 
	 * @param relatevalue
	 */
	public void setRelatevalue(String relatevalue) {
		this.relatevalue = relatevalue;
	}

	/**
	 * 设置数据库的值
	 * 
	 * @param i
	 *            列序
	 * @param v
	 *            数据库值
	 */
	public void setdbValueAt(int i, String v) {
		if (v == null)
			v = "";
		setElementAt(v, i);
		dbvalues.setElementAt(v, i);
	}

	/**
	 * 返回当前值
	 * 
	 * @param i
	 *            列序
	 * @return 当前值
	 */
	public String getValueAt(int i) {
		return (String) elementAt(i);
	}

	/**
	 * 返回数据库列值
	 * 
	 * @param i
	 *            列序
	 * @return 数据库值
	 */
	public String getdbValueAt(int i) {
		return dbvalues.elementAt(i);
	}

	/**
	 * 返回数据库已删除标志
	 * 
	 * @return
	 */
	public int getDbdeleted() {
		return dbdeleted;
	}

	/**
	 * 设置数据库已删除标志
	 * 
	 * @param dbdeleted
	 */
	public void setDbdeleted(int dbdeleted) {
		this.dbdeleted = dbdeleted;
	}

	/**
	 * 输出字段数和值
	 * 
	 * @param out
	 * @throws Exception
	 */
	public void writeData(OutputStream out) throws Exception {
		CommandFactory.writeShort(size(), out);
		for (int i = 0; i < size(); i++) {
			CommandFactory.writeString((String) elementAt(i), out);
			CommandFactory.writeString(dbvalues.elementAt(i), out);
		}
		CommandFactory.writeString(relatevalue, out);
		CommandFactory.writeShort(dbstatus, out);
		CommandFactory.writeShort(dbdeleted, out);
		CommandFactory.writeShort(saveresult, out);
		CommandFactory.writeString(savemessage, out);
		CommandFactory.writeString(tmppkid, out);

	}

	/**
	 * 从in中创建
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public static RecordTrunk readData(InputStream in) throws Exception {
		int colct = CommandFactory.readShort(in);
		if (colct < 0)
			return null;
		RecordTrunk rec = new RecordTrunk(colct);
		for (int i = 0; i < colct; i++) {
			rec.setElementAt(CommandFactory.readString(in), i);
			rec.dbvalues.setElementAt(CommandFactory.readString(in), i);
		}
		rec.relatevalue = CommandFactory.readString(in);
		rec.dbstatus = CommandFactory.readShort(in);
		rec.dbdeleted = CommandFactory.readShort(in);
		rec.saveresult = CommandFactory.readShort(in);
		rec.savemessage = CommandFactory.readString(in);
		rec.tmppkid = CommandFactory.readString(in);
		return rec;
	}

	/**
	 * java api函数,设置index位置值为o
	 */
	public void setElementAt(Object o, int index) {
		if (o == null) {
			super.setElementAt("", index); // To change body of overridden
											// methods use File | Settings |
											// File Templates.
		} else {
			super.setElementAt(o, index); // To change body of overridden
											// methods use File | Settings |
											// File Templates.
		}
	}

	/**
	 * java api函数,取index位置的值
	 */
	public Object elementAt(int index) {
		Object o = super.elementAt(index);// To change body of overridden
											// methods use File | Settings |
											// File Templates.
		if (o == null) {
			return "";
		} else {
			return o;
		}
	}

	/**
	 * 返回数据库值
	 * 
	 * @return
	 */
	public int getDbstatus() {
		return dbstatus;
	}

	/**
	 * 设置数据库值
	 * 
	 * @param dbstatus
	 */
	public void setDbstatus(int dbstatus) {
		this.dbstatus = dbstatus;
		if (dbstatus == DBSTATUS_NEW || dbstatus == DBSTATUS_DELETE
				|| dbstatus == DBSTATUS_MODIFIED) {
			this.setSaveresult(0, "");
		}
	}

	/**
	 * @deprecated
	 * @param insertindex
	 * @param v
	 */
	public void insertColumn(int insertindex, String v) {
		this.insertElementAt(v, insertindex);
		dbvalues.insertElementAt("", insertindex);
	}

	/**
	 * 如果当前值和数据库值不同就算修改
	 * 
	 * @param colindex
	 * @return
	 */
	public boolean isColumnmodified(int colindex) {
		if (dbstatus == DBSTATUS_NEW)
			return true;
		String v = this.getValueAt(colindex);
		String dbv = this.getdbValueAt(colindex);

		return !v.equals(dbv);
	}

	/**
	 * 复制记录
	 * 
	 * @return
	 */
	public RecordTrunk copy() {
		RecordTrunk newrec = new RecordTrunk(this.size());
		for (int i = 0; i < size(); i++) {
			newrec.setElementAt(this.elementAt(i), i);
			newrec.dbvalues.setElementAt(this.dbvalues.elementAt(i), i);
		}
		newrec.dbstatus = dbstatus;
		newrec.relatevalue = relatevalue;
		newrec.dbdeleted = dbdeleted;
		newrec.saveresult = saveresult;
		newrec.savemessage = savemessage;
		newrec.tmppkid = tmppkid;
		return newrec;
	}

	/**
	 * 数据库保存结果.0为成功
	 */
	int saveresult = 0;

	/**
	 * 数据库保存结果.
	 */
	String savemessage = "";

	/**
	 * 返回数据库保存结果,0成功
	 * 
	 * @return
	 */
	public int getSaveresult() {
		return saveresult;
	}

	/**
	 * 设置数据库保存结果
	 * 
	 * @param saveresult
	 */
	public void setSaveresult(int saveresult) {
		this.saveresult = saveresult;
	}

	/**
	 * 取数据库保存结果信息
	 * 
	 * @return
	 */
	public String getSavemessage() {
		if (savemessage == null) {
			return "null";
		}
		return savemessage;
	}

	/**
	 * 设置数据库保存结果信息
	 * 
	 * @param savemessage
	 */
	public void setSavemessage(String savemessage) {
		this.savemessage = savemessage;
	}

	/**
	 * 设置保存结果状态和信息
	 * 
	 * @param saveresult
	 * @param savemessage
	 */
	public void setSaveresult(int saveresult, String savemessage) {
		this.saveresult = saveresult;
		this.savemessage = savemessage;
	}

	/**
	 * 是否是合计记录
	 * 
	 * @return
	 */
	public int getSumflag() {
		return sumflag;
	}

	/**
	 * 设置是否是合计记录
	 * 
	 * @param sumflag
	 */
	public void setSumflag(int sumflag) {
		this.sumflag = sumflag;
	}

	/**
	 * 返回临时主键
	 * 
	 * @return
	 */
	public String getTmppkid() {
		return tmppkid;
	}

	/**
	 * 设置临时主键
	 * 
	 * @param tmppkid
	 */
	public void setTmppkid(String tmppkid) {
		this.tmppkid = tmppkid;
	}

	public Vector<String> getdbValues() {
		return dbvalues;
	}

	Vector<File> wantuploadfiles = new Vector<File>();

	public Vector<File> getWantuploadfiles() {
		return wantuploadfiles;
	}

	public void addWantuploadfile(File f) {
		wantuploadfiles.add(f);

		// 要修改状态
		if (getDbstatus() == DBSTATUS_SAVED) {
			setDbstatus(DBSTATUS_MODIFIED);
		}
	}

	public void setWantuploadfile(Vector<File> fs) {
		wantuploadfiles = fs;
		/* 不要修改状态
		if (getDbstatus() == DBSTATUS_SAVED) {
			setDbstatus(DBSTATUS_MODIFIED);
		}*/
	}

	/**
	 * 相关的附件信息
	 */
	DBTableModel filedbmodel = null;

	public DBTableModel getFiledbmodel() {
		return filedbmodel;
	}

	public void setFiledbmodel(DBTableModel filedbmodel) {
		this.filedbmodel = filedbmodel;
	}
////////////////////////////////////下面的为临时，不要复制和传输/////////////////////////////////////
	/**
	 * key为列名。值为动态的下拉选择ddlmodel
	 */
	HashMap<String,CComboBoxModel> ddldbmodelmap=new HashMap<String, CComboBoxModel>();
	public void putDdldbmodel(String colname,CComboBoxModel cdbmodel){
		ddldbmodelmap.put(colname,cdbmodel);
	}
	
	public CComboBoxModel getColumnddlmodel(String colname){
		return ddldbmodelmap.get(colname);
	}

	/**
	 * 对于分组合计行，取组名
	 * @return
	 */
	public String getGroupname() {
		return groupname;
	}

	/**
	 * 对于分组合计行，设组名
	 * @param groupname
	 */
	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}
	
	/**
	 * 在制作成group dbtablemodel是,对分组行,grouplevel为该行对应的组的level
	 * 如果是数据行,grouplevel=-1;
	 */
	int grouplevel=-1;

	public int getGrouplevel() {
		return grouplevel;
	}

	public void setGrouplevel(int grouplevel) {
		this.grouplevel = grouplevel;
	}
	
}
