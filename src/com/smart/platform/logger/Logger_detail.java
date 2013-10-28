package com.smart.platform.logger;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.mde.CQueryDetailModel;

import java.awt.*;

/*����"��ѯ������������־"ϸ��Model*/
public class Logger_detail extends CQueryDetailModel{
	public Logger_detail(CFrame frame, CMdeModel mdemodel) throws HeadlessException {
		super(frame, "������������ϸ", mdemodel);
		setSort(new String[]{"dtlid"},true);
	}

	public String getTablename() {
		return "np_error_dtl";
	}

	public String getSaveCommandString() {
		return null;
	}
	
	protected String getSqlOrderby() {
		return " order by dtlid asc";
	}
	
}
