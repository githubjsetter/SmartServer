package com.smart.sysmgr.roleopfast;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMdeModel;

import java.awt.*;

/*����"��ɫ������Ȩ����"ϸ��Model*/
public class Roleopap_detail extends CDetailModel{
	public Roleopap_detail(CFrame frame, CMdeModel mdemodel) throws HeadlessException {
		super(frame, "������Ȩ����", mdemodel);
	}

	public String getTablename() {
		return "np_op_ap";
	}

	public String getSaveCommandString() {
		return null;
	}
}
