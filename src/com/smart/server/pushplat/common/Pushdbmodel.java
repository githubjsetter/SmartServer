package com.smart.server.pushplat.common;

import java.util.Vector;

import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;

public class Pushdbmodel extends DBTableModel{

	public Pushdbmodel() {
		super(createCols());
	}

	public static Vector<DBColumnDisplayInfo> createCols(){
		Vector<DBColumnDisplayInfo> cols=new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col=new DBColumnDisplayInfo("�к�","�к�","�к�");
		cols.add(col);
		col=new DBColumnDisplayInfo("pushid","number","����ID");
		cols.add(col);
		col=new DBColumnDisplayInfo("pushname","varchar","��������");
		cols.add(col);
		col=new DBColumnDisplayInfo("groupname","varchar","������");
		cols.add(col);
		col=new DBColumnDisplayInfo("level","number","����");
		col.setEditcomptype(DBColumnDisplayInfo.EDITCOMP_COMBOBOX);
		col.addComboxBoxItem("1", "�ǳ�����");
		col.addComboxBoxItem("2", "����");
		col.addComboxBoxItem("3", "��ͨ");
		cols.add(col);

		col=new DBColumnDisplayInfo("callopid","number","���ù���ID");
		cols.add(col);
		col=new DBColumnDisplayInfo("callopname","varchar","���ù���");
		cols.add(col);

		col=new DBColumnDisplayInfo("wheres","varchar","����where����");
		cols.add(col);

		col=new DBColumnDisplayInfo("otherwheres","varchar","��Ȩwhere����");
		cols.add(col);
		return cols;
	}
}
