package com.inca.npserver.servermanager;

import java.util.Vector;

import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;

public class Proddbmodel extends DBTableModel {

	static Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
	static {
		DBColumnDisplayInfo col;

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
	}

	public Proddbmodel() {
		super(cols);
	}
}
