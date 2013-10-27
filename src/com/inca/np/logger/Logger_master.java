package com.inca.np.logger;

import com.inca.np.gui.mde.CMasterModel;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.mde.CQueryMastermodel;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBColumnDisplayInfo;
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
