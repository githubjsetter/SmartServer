package com.smart.server.pushplat.client;

import java.awt.Frame;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;

import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.gui.control.CMultiHov;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.Querycond;
import com.smart.platform.util.DefaultNPParam;
import com.smart.platform.util.SendHelper;

public class Push_hov extends CMultiHov{

	public Push_hov() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected TableModel createTablemodel() {
		Vector<DBColumnDisplayInfo> cols=new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col=new DBColumnDisplayInfo("pushid","number","推送ID");
		cols.add(col);
		col.setReadonly(true);
		
		
		col=new DBColumnDisplayInfo("pushname","varchar","推送名");
		cols.add(col);
		col.setReadonly(true);
		
		return new DBTableModel(cols);
	}

	@Override
	public String getDefaultsql() {
		return "";
	}

	@Override
	public Querycond getQuerycond() {
		Querycond cond=new Querycond();
		return cond;
	}

	public String[] getColumns() {
		return new String[]{"pushid","pushname"};
	}

	public String getDesc() {
		return "推送hov";
	}

	@Override
	protected void doQuery() {
		ClientRequest req=new ClientRequest("npserver:下载推送");
		try {
			ServerResponse resp=SendHelper.sendRequest(req);
			DataCommand dcmd=(DataCommand) resp.commandAt(1);
			DBTableModel dbmodel=(DBTableModel)dlgtable.getModel();
			DBTableModel tmpdm=dcmd.getDbmodel();
			dbmodel.bindMemds(tmpdm);
			dlgtable.tableChanged(new TableModelEvent(dlgtable.getModel()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	

	@Override
	protected boolean autoSelect() {
		return true;
	}

	public static void main(String[] args) {
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;

		DefaultNPParam.debugdbip = "192.9.200.89";
		DefaultNPParam.debugdbpasswd = "database2";
		DefaultNPParam.debugdbsid = "pb";
		DefaultNPParam.debugdbusrname = "database2";
		DefaultNPParam.prodcontext = "npserver";

		Push_hov hov=new Push_hov();
		hov.showDialog((Frame)null,"select push");
	}
}
