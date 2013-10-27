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
 * ���һ����¼
 */
public class RecordTrunk extends Vector {
	/**
	 * �ѱ���
	 */
	public static final int DBSTATUS_SAVED = 0;

	/**
	 * ����δ����
	 */
	public static final int DBSTATUS_NEW = 1;

	/**
	 * ��ѯ�������޸�
	 */
	public static final int DBSTATUS_MODIFIED = 2;

	/**
	 * ��ѯ������ɾ��
	 */

	public static final int DBSTATUS_DELETE = 3;

	/**
	 * ��ͨ��¼
	 */
	public static final int SUMFLAG_RECORD = 0;

	/**
	 * �ϼ�
	 */
	public static final int SUMFLAG_SUMMARY = 9999;

	/**
	 * ��n�����ϼ�
	 */

	/**
	 * ���ݿ�ֵ
	 */
	Vector<String> dbvalues = new Vector<String>();

	int dbstatus = DBSTATUS_NEW;

	/**
	 * ���ڱ����ܵ�ϸĿ����ʱ��ع�ϵ
	 */
	String relatevalue = "";

	/**
	 * �������ݿ���ɾ��
	 */
	int dbdeleted = 0;

	int sumflag = SUMFLAG_RECORD;

	/**
	 * ������ֵ.���������,ʹ��ϵͳʱ����������ʱ����һ��. �����ܵ�ϸĿ���й���
	 */
	String tmppkid = "";
	
	/**
	 * ��ϼ��С�
	 */
	String groupname="";

	/**
	 * ����.
	 * 
	 * @param fieldcount
	 *            ������
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
	 * ����ֵ.��col��λ�õ�ֵΪv
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
	 * ȡ����ֵ,�����ܵ�ϸĿ��ʱ��¼��Ӧ��ϵ
	 * 
	 * @return
	 */
	public String getRelatevalue() {
		return relatevalue;
	}

	/**
	 * ��������е�ֵ
	 * 
	 * @param relatevalue
	 */
	public void setRelatevalue(String relatevalue) {
		this.relatevalue = relatevalue;
	}

	/**
	 * �������ݿ��ֵ
	 * 
	 * @param i
	 *            ����
	 * @param v
	 *            ���ݿ�ֵ
	 */
	public void setdbValueAt(int i, String v) {
		if (v == null)
			v = "";
		setElementAt(v, i);
		dbvalues.setElementAt(v, i);
	}

	/**
	 * ���ص�ǰֵ
	 * 
	 * @param i
	 *            ����
	 * @return ��ǰֵ
	 */
	public String getValueAt(int i) {
		return (String) elementAt(i);
	}

	/**
	 * �������ݿ���ֵ
	 * 
	 * @param i
	 *            ����
	 * @return ���ݿ�ֵ
	 */
	public String getdbValueAt(int i) {
		return dbvalues.elementAt(i);
	}

	/**
	 * �������ݿ���ɾ����־
	 * 
	 * @return
	 */
	public int getDbdeleted() {
		return dbdeleted;
	}

	/**
	 * �������ݿ���ɾ����־
	 * 
	 * @param dbdeleted
	 */
	public void setDbdeleted(int dbdeleted) {
		this.dbdeleted = dbdeleted;
	}

	/**
	 * ����ֶ�����ֵ
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
	 * ��in�д���
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
	 * java api����,����indexλ��ֵΪo
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
	 * java api����,ȡindexλ�õ�ֵ
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
	 * �������ݿ�ֵ
	 * 
	 * @return
	 */
	public int getDbstatus() {
		return dbstatus;
	}

	/**
	 * �������ݿ�ֵ
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
	 * �����ǰֵ�����ݿ�ֵ��ͬ�����޸�
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
	 * ���Ƽ�¼
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
	 * ���ݿⱣ����.0Ϊ�ɹ�
	 */
	int saveresult = 0;

	/**
	 * ���ݿⱣ����.
	 */
	String savemessage = "";

	/**
	 * �������ݿⱣ����,0�ɹ�
	 * 
	 * @return
	 */
	public int getSaveresult() {
		return saveresult;
	}

	/**
	 * �������ݿⱣ����
	 * 
	 * @param saveresult
	 */
	public void setSaveresult(int saveresult) {
		this.saveresult = saveresult;
	}

	/**
	 * ȡ���ݿⱣ������Ϣ
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
	 * �������ݿⱣ������Ϣ
	 * 
	 * @param savemessage
	 */
	public void setSavemessage(String savemessage) {
		this.savemessage = savemessage;
	}

	/**
	 * ���ñ�����״̬����Ϣ
	 * 
	 * @param saveresult
	 * @param savemessage
	 */
	public void setSaveresult(int saveresult, String savemessage) {
		this.saveresult = saveresult;
		this.savemessage = savemessage;
	}

	/**
	 * �Ƿ��ǺϼƼ�¼
	 * 
	 * @return
	 */
	public int getSumflag() {
		return sumflag;
	}

	/**
	 * �����Ƿ��ǺϼƼ�¼
	 * 
	 * @param sumflag
	 */
	public void setSumflag(int sumflag) {
		this.sumflag = sumflag;
	}

	/**
	 * ������ʱ����
	 * 
	 * @return
	 */
	public String getTmppkid() {
		return tmppkid;
	}

	/**
	 * ������ʱ����
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

		// Ҫ�޸�״̬
		if (getDbstatus() == DBSTATUS_SAVED) {
			setDbstatus(DBSTATUS_MODIFIED);
		}
	}

	public void setWantuploadfile(Vector<File> fs) {
		wantuploadfiles = fs;
		/* ��Ҫ�޸�״̬
		if (getDbstatus() == DBSTATUS_SAVED) {
			setDbstatus(DBSTATUS_MODIFIED);
		}*/
	}

	/**
	 * ��صĸ�����Ϣ
	 */
	DBTableModel filedbmodel = null;

	public DBTableModel getFiledbmodel() {
		return filedbmodel;
	}

	public void setFiledbmodel(DBTableModel filedbmodel) {
		this.filedbmodel = filedbmodel;
	}
////////////////////////////////////�����Ϊ��ʱ����Ҫ���ƺʹ���/////////////////////////////////////
	/**
	 * keyΪ������ֵΪ��̬������ѡ��ddlmodel
	 */
	HashMap<String,CComboBoxModel> ddldbmodelmap=new HashMap<String, CComboBoxModel>();
	public void putDdldbmodel(String colname,CComboBoxModel cdbmodel){
		ddldbmodelmap.put(colname,cdbmodel);
	}
	
	public CComboBoxModel getColumnddlmodel(String colname){
		return ddldbmodelmap.get(colname);
	}

	/**
	 * ���ڷ���ϼ��У�ȡ����
	 * @return
	 */
	public String getGroupname() {
		return groupname;
	}

	/**
	 * ���ڷ���ϼ��У�������
	 * @param groupname
	 */
	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}
	
	/**
	 * ��������group dbtablemodel��,�Է�����,grouplevelΪ���ж�Ӧ�����level
	 * �����������,grouplevel=-1;
	 */
	int grouplevel=-1;

	public int getGrouplevel() {
		return grouplevel;
	}

	public void setGrouplevel(int grouplevel) {
		this.grouplevel = grouplevel;
	}
	
}
