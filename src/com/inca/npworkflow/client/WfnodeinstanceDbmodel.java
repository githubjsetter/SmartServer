package com.inca.npworkflow.client;

import java.util.Vector;

import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;

/**
 * 需要某个人处理的流程实例dbmodel
 * @author user
 *
 */
public class WfnodeinstanceDbmodel extends DBTableModel{
	
	public WfnodeinstanceDbmodel() {
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

		col=new DBColumnDisplayInfo("wfid","number","流程ID");
		cols.add(col);

		col=new DBColumnDisplayInfo("wfname","varchar","流程名称");
		cols.add(col);

		col=new DBColumnDisplayInfo("wfnodeid","number","结点ID");
		cols.add(col);

		col=new DBColumnDisplayInfo("wfnodename","varchar","结点名称");
		cols.add(col);

		col=new DBColumnDisplayInfo("summary","varchar","摘要");
		cols.add(col);

		col=new DBColumnDisplayInfo("startdate","date","开始日期");
		cols.add(col);

		col=new DBColumnDisplayInfo("employeeid","number","人员ID");
		cols.add(col);

		col=new DBColumnDisplayInfo("approveflag","number","审批状态");
		cols.add(col);

		col=new DBColumnDisplayInfo("approvemsg","varchar","审批意见");
		cols.add(col);

		col=new DBColumnDisplayInfo("treateresult","varchar","处理结果");
		cols.add(col);

		col=new DBColumnDisplayInfo("refflag","number","参审标志");
		cols.add(col);

		col=new DBColumnDisplayInfo("refmessage","number","参审意见");
		cols.add(col);

		col=new DBColumnDisplayInfo("refnodeinstanceid","number","参审结点ID");
		cols.add(col);

		return cols;
	}
}
