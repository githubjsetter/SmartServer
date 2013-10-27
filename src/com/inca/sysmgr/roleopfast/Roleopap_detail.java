package com.inca.sysmgr.roleopfast;

import com.inca.np.gui.mde.CDetailModel;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBColumnDisplayInfo;
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
