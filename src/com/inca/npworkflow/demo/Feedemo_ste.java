package com.inca.npworkflow.demo;

import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.util.SendHelper;

import java.awt.*;

/*����"����������ʾ"����༭Model*/
public class Feedemo_ste extends CSteModel {
	public Feedemo_ste(CFrame frame) throws HeadlessException {
		super(frame, "��������");
	}

	public String getTablename() {
		return "np_wf_demo_fee_v";
	}

	public String getSaveCommandString() {
		return "npworkflow.demo.Feedemo_ste.�������";
	}

	@Override
	protected int on_actionPerformed(String command) {
		if (command.equals("�ύ")) {
			confirm();
			return 0;
		} else {
			return super.on_actionPerformed(command);
		}
	}

	void confirm() {
		int row=getRow();
		if(row<0){
			warnMessage("��ʾ","ѡ��һ�����ύ");
			return;
		}
		String feedocid=getItemValue(row, "feedocid");
		String cmd="npworkflow.demo.Feedemo.�ύ";
		ClientRequest req=new ClientRequest(cmd);
		ParamCommand pcmd=new ParamCommand();
		pcmd.addParam("feedocid",feedocid);
		req.addCommand(pcmd);
		try {
			ServerResponse resp=SendHelper.sendRequest(req);
			String respcmd=resp.getCommand();
			if(respcmd.startsWith("+OK")){
				infoMessage("�ɹ�","�ύ�ɹ�");
				doRequery();
				return;
			}
			errorMessage("����",respcmd);
		} catch (Exception e) {
			errorMessage("����", e.getMessage());
			
		}
		
	}
}
