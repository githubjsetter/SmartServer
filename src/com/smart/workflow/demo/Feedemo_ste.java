package com.smart.workflow.demo;

import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.util.SendHelper;

import java.awt.*;

/*功能"费用申请演示"单表编辑Model*/
public class Feedemo_ste extends CSteModel {
	public Feedemo_ste(CFrame frame) throws HeadlessException {
		super(frame, "费用申请");
	}

	public String getTablename() {
		return "np_wf_demo_fee_v";
	}

	public String getSaveCommandString() {
		return "npworkflow.demo.Feedemo_ste.保存费用";
	}

	@Override
	protected int on_actionPerformed(String command) {
		if (command.equals("提交")) {
			confirm();
			return 0;
		} else {
			return super.on_actionPerformed(command);
		}
	}

	void confirm() {
		int row=getRow();
		if(row<0){
			warnMessage("提示","选择一行再提交");
			return;
		}
		String feedocid=getItemValue(row, "feedocid");
		String cmd="npworkflow.demo.Feedemo.提交";
		ClientRequest req=new ClientRequest(cmd);
		ParamCommand pcmd=new ParamCommand();
		pcmd.addParam("feedocid",feedocid);
		req.addCommand(pcmd);
		try {
			ServerResponse resp=SendHelper.sendRequest(req);
			String respcmd=resp.getCommand();
			if(respcmd.startsWith("+OK")){
				infoMessage("成功","提交成功");
				doRequery();
				return;
			}
			errorMessage("错误",respcmd);
		} catch (Exception e) {
			errorMessage("错误", e.getMessage());
			
		}
		
	}
}
