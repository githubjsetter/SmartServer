package com.smart.adminclient.prodmanager;

import java.util.Vector;

import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;

public class Proddbmodel extends DBTableModel {

	static Vector<DBColumnDisplayInfo> createCols() {

		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col = new DBColumnDisplayInfo("�к�", "�к�", "�к�");
		cols.add(col);

		col = new DBColumnDisplayInfo("prodname", "varchar", "��Ʒ��");
		cols.add(col);

		col = new DBColumnDisplayInfo("authunit", "varchar", "��Ȩ��λ");
		cols.add(col);

		col = new DBColumnDisplayInfo("serverip", "varchar", "������IP");
		cols.add(col);

		col = new DBColumnDisplayInfo("maxclientuser", "varchar", "���ͻ�����");
		cols.add(col);

		col = new DBColumnDisplayInfo("startdate", "varchar", "��Ȩ��ʼ����");
		cols.add(col);

		col = new DBColumnDisplayInfo("enddate", "varchar", "��Ȩ��������");
		cols.add(col);

		col = new DBColumnDisplayInfo("copyright", "varchar", "��Ȩ����");
		cols.add(col);
		
		col = new DBColumnDisplayInfo("modules", "varchar", "��Ȩģ��");
		cols.add(col);
		return cols;
	}

	public Proddbmodel() {
		super(createCols());
	}
}
