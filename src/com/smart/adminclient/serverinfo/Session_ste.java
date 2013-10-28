package com.smart.adminclient.serverinfo;

import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.CQueryStemodel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.util.SendHelper;

import java.awt.*;

import org.apache.log4j.Category;

/*����"ϵͳ��Ϣ��ѯ"����༭Model*/
public class Session_ste extends CQueryStemodel{
	Category logger=Category.getInstance(Session_ste.class);
	public Session_ste(CFrame frame) throws HeadlessException {
		super(frame, "ϵͳ��Ϣ");
	}

	public String getTablename() {
		return "pub_goods_detail";
	}

	public String getSaveCommandString() {
		return null;
	}

	@Override
	public void doQuery() {
		ClientRequest req=new ClientRequest("��ѯ����������");
		ServerResponse resp=null;
		try {
			this.setWaitCursor();
			resp=SendHelper.sendRequest(req);
		} catch (Exception e) {
			logger.error("ERROR",e);
			errorMessage("����",e.getMessage());
			return;
		} finally{
			this.setDefaultCursor();
		}
		
		StringCommand cmd0=(StringCommand)resp.commandAt(0);
		if(!cmd0.getString().startsWith("+OK")){
			errorMessage("����",cmd0.getString());
			return;
		}
		
		DataCommand cmd1=(DataCommand)resp.commandAt(1);
		DBTableModel dbmodel=cmd1.getDbmodel();
		
		this.setDBtableModel(dbmodel);
		
	}
	
	
	
}
