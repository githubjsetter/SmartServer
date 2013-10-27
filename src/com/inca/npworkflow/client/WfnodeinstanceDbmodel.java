package com.inca.npworkflow.client;

import java.util.Vector;

import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;

/**
 * ��Ҫĳ���˴��������ʵ��dbmodel
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
		col=new DBColumnDisplayInfo("�к�","�к�","�к�");
		col.setDbcolumn(false);
		col.setUpdateable(false);
		cols.add(col);

		col=new DBColumnDisplayInfo("wfnodeinstanceid","number","���ʵ��ID");
		cols.add(col);

		col=new DBColumnDisplayInfo("wfid","number","����ID");
		cols.add(col);

		col=new DBColumnDisplayInfo("wfname","varchar","��������");
		cols.add(col);

		col=new DBColumnDisplayInfo("wfnodeid","number","���ID");
		cols.add(col);

		col=new DBColumnDisplayInfo("wfnodename","varchar","�������");
		cols.add(col);

		col=new DBColumnDisplayInfo("summary","varchar","ժҪ");
		cols.add(col);

		col=new DBColumnDisplayInfo("startdate","date","��ʼ����");
		cols.add(col);

		col=new DBColumnDisplayInfo("employeeid","number","��ԱID");
		cols.add(col);

		col=new DBColumnDisplayInfo("approveflag","number","����״̬");
		cols.add(col);

		col=new DBColumnDisplayInfo("approvemsg","varchar","�������");
		cols.add(col);

		col=new DBColumnDisplayInfo("treateresult","varchar","������");
		cols.add(col);

		col=new DBColumnDisplayInfo("refflag","number","�����־");
		cols.add(col);

		col=new DBColumnDisplayInfo("refmessage","number","�������");
		cols.add(col);

		col=new DBColumnDisplayInfo("refnodeinstanceid","number","������ID");
		cols.add(col);

		return cols;
	}
}
