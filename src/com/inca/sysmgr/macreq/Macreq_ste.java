package com.inca.sysmgr.macreq;

import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.util.SendHelper;

import java.awt.*;

/*����"������������"����༭Model*/
public class Macreq_ste extends CSteModel{
	public Macreq_ste(CFrame frame) throws HeadlessException {
		super(frame, "��������");
	}

	public String getTablename() {
		return "np_mac_req";
	}

	public String getSaveCommandString() {
		return "com.inca.sysmgr.macreq.Macreq_ste.������������";
	}

	@Override
	protected int on_actionPerformed(String command) {
		if(command.equals("����ͨ��")){
			approve();
			return 0;
		}else if(command.equals("����׼")){
			refuse();
			return 0;
		}else{
			return super.on_actionPerformed(command);
		}
	}
	
	void refuse(){
		int row=getRow();
		if(row<0){
			warnMessage("��ʾ","��ѡ��һ�м�¼");
			return;
		}
		setItemValue(row,"approveflag","0");
		doSave();
	}
	
	void approve(){
		int row=getRow();
		if(row<0){
			warnMessage("��ʾ","��ѡ��һ�м�¼");
			return;
		}
		String seqid=getItemValue(row,"seqid");
		ClientRequest req=new ClientRequest("com.inca.sysmgr.macreq.Macreq_ste.����ͨ��");
		ParamCommand pcmd=new ParamCommand();
		req.addCommand(pcmd);
		pcmd.addParam("seqid",seqid);
		
		try {
			ServerResponse resp=SendHelper.sendRequest(req);
			String cmd=resp.getCommand();
			if(!cmd.startsWith("+OK")){
				errorMessage("����",cmd);
				return;
			}
		} catch (Exception e) {
			errorMessage("����",e.getMessage());
			return;
		}

		getDBtableModel().removeRow(row);
		getSumdbmodel().fireDatachanged();
		tableChanged();
	}
}
