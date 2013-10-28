package com.smart.workflow.client;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.mde.CMasterModel;
import com.smart.platform.gui.mde.CMdeModel;

import java.awt.*;

/*功能"流程实例"总单Model*/
public class Wfinst_master extends CMasterModel{
	public Wfinst_master(CFrame frame, CMdeModel mdemodel) throws HeadlessException {
		super(frame, "流程实例", mdemodel);
	}

	public String getTablename() {
		return "np_wf_instance_v";
	}

	public String getSaveCommandString() {
		return null;
	}
}
