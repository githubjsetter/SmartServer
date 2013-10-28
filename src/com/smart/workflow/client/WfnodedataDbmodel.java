package com.smart.workflow.client;

import java.util.Vector;

import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;

/**
 * 决策依据数据
 * @author user
 *
 */
public class WfnodedataDbmodel  extends DBTableModel{
	
	public WfnodedataDbmodel() {
		super(getCols());
	}

	static Vector<DBColumnDisplayInfo> getCols(){
		Vector<DBColumnDisplayInfo> cols=new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col=new DBColumnDisplayInfo("行号","行号","行号");
		col.setDbcolumn(false);
		col.setUpdateable(false);
		cols.add(col);

		col=new DBColumnDisplayInfo("wfnodeinstanceid","number","结点实例ID");
		cols.add(col);

		col=new DBColumnDisplayInfo("wfnodedataid","number","结点数据ID");
		cols.add(col);

		col=new DBColumnDisplayInfo("dataname","varchar","决策依据");
		cols.add(col);

		col=new DBColumnDisplayInfo("datavalue","varchar","数据");
		cols.add(col);

		col=new DBColumnDisplayInfo("sortno","number","排序号");
		cols.add(col);

		return cols;
	}
}
