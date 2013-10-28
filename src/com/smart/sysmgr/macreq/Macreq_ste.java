package com.smart.sysmgr.macreq;

import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.util.SendHelper;

import java.awt.*;

/*功能"入网请求审批"单表编辑Model*/
public class Macreq_ste extends CSteModel{
	public Macreq_ste(CFrame frame) throws HeadlessException {
		super(frame, "入网请求");
	}

	public String getTablename() {
		return "np_mac_req";
	}

	public String getSaveCommandString() {
		return "com.inca.sysmgr.macreq.Macreq_ste.保存入网请求";
	}

	@Override
	protected int on_actionPerformed(String command) {
		if(command.equals("审批通过")){
			approve();
			return 0;
		}else if(command.equals("不批准")){
			refuse();
			return 0;
		}else{
			return super.on_actionPerformed(command);
		}
	}
	
	void refuse(){
		int row=getRow();
		if(row<0){
			warnMessage("提示","请选择一行记录");
			return;
		}
		setItemValue(row,"approveflag","0");
		doSave();
	}
	
	void approve(){
		int row=getRow();
		if(row<0){
			warnMessage("提示","请选择一行记录");
			return;
		}
		String seqid=getItemValue(row,"seqid");
		ClientRequest req=new ClientRequest("com.inca.sysmgr.macreq.Macreq_ste.审批通过");
		ParamCommand pcmd=new ParamCommand();
		req.addCommand(pcmd);
		pcmd.addParam("seqid",seqid);
		
		try {
			ServerResponse resp=SendHelper.sendRequest(req);
			String cmd=resp.getCommand();
			if(!cmd.startsWith("+OK")){
				errorMessage("错误",cmd);
				return;
			}
		} catch (Exception e) {
			errorMessage("错误",e.getMessage());
			return;
		}

		getDBtableModel().removeRow(row);
		getSumdbmodel().fireDatachanged();
		tableChanged();
	}
}
