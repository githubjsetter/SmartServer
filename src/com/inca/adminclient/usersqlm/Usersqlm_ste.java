package com.inca.adminclient.usersqlm;

import java.awt.HeadlessException;
import java.awt.dnd.Autoscroll;
import java.util.Vector;

import javax.swing.JOptionPane;

import com.inca.adminclient.auth.AdminSendHelper;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.RecordTrunk;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.CSteModel;

/**
 * ĳ�û�sql���
 * @author user
 *
 */
public class Usersqlm_ste extends CSteModel{

	String userid="0";
	public Usersqlm_ste(CFrame frame, String title) throws HeadlessException {
		super(frame, title);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getTablename() {
		return "";
	}

	@Override
	public String getSaveCommandString() {
		return "";
	}

	@Override
	protected int on_actionPerformed(String command) {
		if("�����û����".equals(command)){
			addUser();
		}else if("ȡ���û����".equals(command)){
			removeUser();
		}
		return super.on_actionPerformed(command);
	}
	
	protected void addUser(){
		String u=JOptionPane.showInputDialog("�������Ӽ�ص��û�ID", userid);
		if(u==null)return;
		userid=u;
		ClientRequest req=new ClientRequest("npserver:addusersqlmonitor");
		ParamCommand pcmd=new ParamCommand();
		req.addCommand(pcmd);
		pcmd.addParam("action","add");
		pcmd.addParam("userid",userid);
		try {
			AdminSendHelper.sendRequest(req);
		} catch (Exception e) {
			errorMessage("����",e.getMessage());
			return;
		}
		
	}
	protected void removeUser(){
		String u=JOptionPane.showInputDialog("����ȡ����ص��û�ID", userid);
		if(u==null)return;
		userid=u;
		ClientRequest req=new ClientRequest("npserver:addusersqlmonitor");
		ParamCommand pcmd=new ParamCommand();
		req.addCommand(pcmd);
		pcmd.addParam("action","del");
		pcmd.addParam("userid",userid);
		try {
			AdminSendHelper.sendRequest(req);
		} catch (Exception e) {
			errorMessage("����",e.getMessage());
			return;
		}
	}

	@Override
	public void doQuery() {
		String u=JOptionPane.showInputDialog("�����ص��û�ID", userid);
		if(u==null)return;
		userid=u;
		ClientRequest req=new ClientRequest("npserver:fetchusersqlmonitor");
		ParamCommand pcmd=new ParamCommand();
		req.addCommand(pcmd);
		pcmd.addParam("userid",userid);
		try {
			ServerResponse resp=AdminSendHelper.sendRequest(req);
			DataCommand dcmd=(DataCommand) resp.commandAt(1);
			DBTableModel dm=dcmd.getDbmodel();
			
			dbmodel.clearAll();
			dbmodel.bindMemds(dm);
			
			for(int i=0;i<dbmodel.getRowCount();i++){
				dbmodel.setdbStatus(i, RecordTrunk.DBSTATUS_SAVED);
			}
			sumdbmodel.fireDatachanged();
			tableChanged();
			table.autoSize();
		} catch (Exception e) {
			errorMessage("����",e.getMessage());
			return;
		}
	}
	
	
	
	
/*
	@Override
	protected void loadDBColumnInfos() {
		this.formcolumndisplayinfos=new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col=new DBColumnDisplayInfo("�к�","�к�","�к�");
		formcolumndisplayinfos.add(col);
		
		col=new DBColumnDisplayInfo("userid","number","�û�ID");
		formcolumndisplayinfos.add(col);

		col=new DBColumnDisplayInfo("sql","varchar","sql");
		formcolumndisplayinfos.add(col);
		col=new DBColumnDisplayInfo("param","varchar","���ò���");
		formcolumndisplayinfos.add(col);
		col=new DBColumnDisplayInfo("usetime","number","��ʱ(ms)");
		formcolumndisplayinfos.add(col);

	}
*/
	
}
