package com.inca.sysmgr.roleopfast;

import com.inca.np.gui.mde.CDetailModel;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBColumnDisplayInfo;
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
