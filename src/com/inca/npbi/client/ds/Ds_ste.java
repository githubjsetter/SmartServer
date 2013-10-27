package com.inca.npbi.client.ds;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBColumnDisplayInfo;
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
