package com.smart.platform.logger;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.mde.CMasterModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.mde.CQueryMastermodel;

import java.awt.*;

/*����"��ѯ������������־"�ܵ�Model*/
public class Logger_master extends CQueryMastermodel{
	public Logger_master(CFrame frame, CMdeModel mdemodel) throws HeadlessException {
		super(frame, "����������", mdemodel);
		//����
		setSort(new String[]{"seqid"},false);
	}

	public String getTablename() {
		return "np_error";
	}

	public String getSaveCommandString() {
		return null;
	}

	@Override
	protected String getSqlOrderby() {
		return " order by seqid desc";
	}
	
	
}
