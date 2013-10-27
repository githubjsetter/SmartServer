package com.inca.npworkflow.client;

import com.inca.np.gui.mde.CDetailModel;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import java.awt.*;

/*功能"流程实例"细单Model*/
public class Wfinst_detail extends CDetailModel{
	public Wfinst_detail(CFrame frame, CMdeModel mdemodel) throws HeadlessException {
		super(frame, "流程结点实例", mdemodel);
	}

	public String getTablename() {
		return "np_wf_node_instance_v";
	}

	public String getSaveCommandString() {
		return null;
	}
}
