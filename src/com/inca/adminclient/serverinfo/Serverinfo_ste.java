package com.inca.adminclient.serverinfo;

import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.ste.CQueryStemodel;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.util.SendHelper;

import java.awt.*;

import org.apache.log4j.Category;

/*����"ϵͳ��Ϣ��ѯ"����༭Model*/
public class Serverinfo_ste extends CQueryStemodel{
	Category logger=Category.getInstance(Serverinfo_ste.class);
	public Serverinfo_ste(CFrame frame) throws HeadlessException {
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
		ClientRequest req=new ClientRequest("��ѯ��������Ϣ");
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
