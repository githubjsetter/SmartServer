package com.smart.workflow.client;

import java.awt.HeadlessException;

import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.util.SendHelper;

/**
 * 查询结点实例数据.决策依据数据.
 * @author user
 *
 */
public class Nodedata_ste extends CDetailModel{

	public Nodedata_ste(CFrame frame, String title,CMdeModel mde) throws HeadlessException {
		super(frame, title,mde);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getTablename() {
		return "";
	}
/*
	@Override
	protected void loadDBColumnInfos() {
		WfnodedataDbmodel dm=new WfnodedataDbmodel();
		this.formcolumndisplayinfos=dm.getDisplaycolumninfos();
	}
*/
	@Override
	public String getSaveCommandString() {
		return "";
	}
	@Override
	public void doQuery(String wheres,DBTableModel dbmodel) {
		int p=wheres.lastIndexOf("=");
		String wfnodeinstanceid=wheres.substring(p+1);
		ClientRequest req=new ClientRequest("npserver:查询决策依据数据");
		ParamCommand pcmd=new ParamCommand();
		req.addCommand(pcmd);
		pcmd.addParam("wfnodeinstanceid", wfnodeinstanceid);
		try {
			ServerResponse resp=SendHelper.sendRequest(req);
			DataCommand dcmd=(DataCommand) resp.commandAt(1);
			DBTableModel dm=dcmd.getDbmodel();
			dbmodel.clearAll();
			dbmodel.bindMemds(dm);
			dbmodel.sort("sortno:asc:wfnodedataid:asc");
			sumdbmodel.fireDatachanged();
			tableChanged();
			table.autoSize();
			if(dbmodel.getRowCount()>0){
				setRow(0);
			}
		} catch (Exception e) {
			errorMessage("错误",e.getMessage());
			return;
		}
		
	}

}
