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

/*功能"查询fullscan"细单Model*/
public class Fullscan_detail extends CDetailModel{
	Category logger = Category.getInstance(Fullscan_detail.class);
	public Fullscan_detail(CFrame frame, CMdeModel mdemodel) throws HeadlessException {
		super(frame, "执行计划", mdemodel);
	}

	public String getTablename() {
		return "plan_table";
	}

	public String getSaveCommandString() {
		return null;
	}

	@Override
	public void doRetrieve(String wheres,DBTableModel targetdbmodel) {
		//取得sql,并将sql提交服务器进行explain plan分析
		int p=wheres.indexOf("'");
		int p1=wheres.lastIndexOf("'");
		String sql_text=wheres.substring(p+1,p1);
		
		//进行分析
		ClientRequest req = new ClientRequest("explainplan");
		req.addCommand(new StringCommand(sql_text));
		ServerResponse resp = null;
		try {
			this.setWaitCursor();
			resp = AdminSendHelper.sendRequest(req);
		} catch (Exception e) {
			logger.error("ERROR", e);
			errorMessage("错误", e.getMessage());
			return;
		} finally {
			this.setDefaultCursor();
		}
		
		StringCommand cmd0 = (StringCommand) resp.commandAt(0);
		if (!cmd0.getString().startsWith("+OK")) {
			errorMessage("错误", cmd0.getString());
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
