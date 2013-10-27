package com.inca.np.logger;

import com.inca.np.gui.mde.CDetailModel;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.mde.CQueryDetailModel;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBColumnDisplayInfo;
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
