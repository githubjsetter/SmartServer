package com.smart.sysmgr.roleopfast;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMdeModel;

import java.awt.*;

/*功能"角色功能授权定义"细单Model*/
public class Roleopap_detail extends CDetailModel{
	public Roleopap_detail(CFrame frame, CMdeModel mdemodel) throws HeadlessException {
		super(frame, "功能授权属性", mdemodel);
	}

	public String getTablename() {
		return "np_op_ap";
	}

	public String getSaveCommandString() {
		return null;
	}
}
