package com.smart.workflow.client;

import java.util.Vector;

import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;

/**
 * ���ʽ��������Ϣ
 * @author user
 *
 */
public class ErrormessageDbmodel extends DBTableModel{

	public ErrormessageDbmodel() {
		super(createcols());
	}

	static Vector<DBColumnDisplayInfo> createcols(){
		Vector<DBColumnDisplayInfo> cols=new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col=new DBColumnDisplayInfo("datatype","varchar","��������");
		cols.add(col);

		col=new DBColumnDisplayInfo("errormessage","varchar","����");
		cols.add(col);
		return cols;

	}
}
