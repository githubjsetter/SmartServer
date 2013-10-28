package com.smart.adminclient.fullscan;

import com.smart.adminclient.auth.AdminSendHelper;
import com.smart.adminclient.gui.AdminClientframe;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.mde.CQueryDetailModel;
import com.smart.platform.util.SendHelper;

import java.awt.*;

import org.apache.log4j.Category;

/*����"��ѯfullscan"ϸ��Model*/
public class Fullscan_detail extends CDetailModel{
	Category logger = Category.getInstance(Fullscan_detail.class);
	public Fullscan_detail(CFrame frame, CMdeModel mdemodel) throws HeadlessException {
		super(frame, "ִ�мƻ�", mdemodel);
	}

	public String getTablename() {
		return "plan_table";
	}

	public String getSaveCommandString() {
		return null;
	}

	@Override
	public void doRetrieve(String wheres,DBTableModel targetdbmodel) {
		//ȡ��sql,����sql�ύ����������explain plan����
		int p=wheres.indexOf("'");
		int p1=wheres.lastIndexOf("'");
		String sql_text=wheres.substring(p+1,p1);
		
		//���з���
		ClientRequest req = new ClientRequest("explainplan");
		req.addCommand(new StringCommand(sql_text));
		ServerResponse resp = null;
		try {
			this.setWaitCursor();
			resp = AdminSendHelper.sendRequest(req);
		} catch (Exception e) {
			logger.error("ERROR", e);
			errorMessage("����", e.getMessage());
			return;
		} finally {
			this.setDefaultCursor();
		}
		
		StringCommand cmd0 = (StringCommand) resp.commandAt(0);
		if (!cmd0.getString().startsWith("+OK")) {
			errorMessage("����", cmd0.getString());
			return;
		}

		DataCommand cmd1 = (DataCommand) resp.commandAt(1);
		DBTableModel dbmodel = cmd1.getDbmodel();
		targetdbmodel.bindMemds(dbmodel);
		//getDBtableModel().appendDbmodel(dbmodel);
		//getSumdbmodel().fireDatachanged();
		tableChanged();
		table.autoSize();
		
		
	}
	
	
	
}
