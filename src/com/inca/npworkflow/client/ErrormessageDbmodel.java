package com.inca.npworkflow.client;

import java.util.Vector;

import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;

/**
 * 表达式检查错误信息
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
		col=new DBColumnDisplayInfo("datatype","varchar","数据类型");
		cols.add(col);

		col=new DBColumnDisplayInfo("errormessage","varchar","错误");
		cols.add(col);
		return cols;

	}
}
