package com.smart.bi.client.ds;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.ste.CSteModel;

import java.awt.*;

/*功能"数据源连接管理"单表编辑Model*/
public class Ds_ste extends CSteModel{
	public Ds_ste(CFrame frame) throws HeadlessException {
		super(frame, "数据源连接");
	}

	public String getTablename() {
		return "npbi_ds";
	}

	public String getSaveCommandString() {
		return "Ds_ste.保存数据源连接";
	}
}
