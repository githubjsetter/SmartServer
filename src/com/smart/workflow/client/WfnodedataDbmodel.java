package com.smart.workflow.client;

import java.util.Vector;

import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;

/**
 * ������������
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
		col=new DBColumnDisplayInfo("�к�","�к�","�к�");
		col.setDbcolumn(false);
		col.setUpdateable(false);
		cols.add(col);

		col=new DBColumnDisplayInfo("wfnodeinstanceid","number","���ʵ��ID");
		cols.add(col);

		col=new DBColumnDisplayInfo("wfnodedataid","number","�������ID");
		cols.add(col);

		col=new DBColumnDisplayInfo("dataname","varchar","��������");
		cols.add(col);

		col=new DBColumnDisplayInfo("datavalue","varchar","����");
		cols.add(col);

		col=new DBColumnDisplayInfo("sortno","number","�����");
		cols.add(col);

		return cols;
	}
}
