package com.smart.workflow.client;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMdeModel;

import java.awt.*;

/*����"����ʵ��"ϸ��Model*/
public class Wfinst_detail extends CDetailModel{
	public Wfinst_detail(CFrame frame, CMdeModel mdemodel) throws HeadlessException {
		super(frame, "���̽��ʵ��", mdemodel);
	}

	public String getTablename() {
		return "np_wf_node_instance_v";
	}

	public String getSaveCommandString() {
		return null;
	}
}
