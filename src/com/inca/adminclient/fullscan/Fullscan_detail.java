package com.inca.adminclient.fullscan;

import com.inca.adminclient.auth.AdminSendHelper;
import com.inca.adminclient.gui.AdminClientframe;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.mde.CDetailModel;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.mde.CQueryDetailModel;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.util.SendHelper;

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
