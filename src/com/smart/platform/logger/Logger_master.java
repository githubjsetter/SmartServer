package com.smart.platform.logger;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.mde.CMasterModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.mde.CQueryMastermodel;

import java.awt.*;

/*功能"查询服务器错误日志"总单Model*/
public class Logger_master extends CQueryMastermodel{
	public Logger_master(CFrame frame, CMdeModel mdemodel) throws HeadlessException {
		super(frame, "服务器错误", mdemodel);
		//倒序
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
