package com.inca.npworkflow.client;

import java.awt.HeadlessException;
import java.util.HashMap;

import javax.swing.JTextField;


import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.RecordTrunk;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.demo.communicate.RemotesqlHelper;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.CTextArea;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.util.SendHelper;

/**
 * ������������Ȩ
 * 
 * @author user
 * 
 */
public class Wfap_ste extends CSteModel {
	String wfid = "";

	public Wfap_ste(CFrame frame, String title) throws HeadlessException {
		super(frame, title);
	}

	@Override
	public String getTablename() {
		return "np_wf_role_ap";
	}

	@Override
	public String getSaveCommandString() {
		return "";
	}

	@Override
	public void doQuery() {
		// ��ѯ���еĽ�ɫ,�������е����ap
		String sql = "select roleid,rolename from np_role";
		RemotesqlHelper sh = new RemotesqlHelper();
		try {
			DBTableModel roledm = sh.doSelect(sql, 0, 10000);
			DBTableModel dm = getDBtableModel();
			dm.clearAll();
			int row = 0;
/*			dm.appendRow();
			dm.setItemValue(row, "roleid", "0");
			dm.setItemValue(row, "rolename", "���н�ɫ");
			dm.setItemValue(row, "apname", "wheres");
			dm.setItemValue(row, "apvalue", getRolewheres("0"));
			dm.setdbStatus(row, RecordTrunk.DBSTATUS_SAVED);
*/
			for (int i = 0; i < roledm.getRowCount(); i++) {
				row = dm.getRowCount();
				dm.appendRow();
				String roleid = roledm.getItemValue(i, "roleid");
				dm.setItemValue(row, "roleid", roleid);
				dm.setItemValue(row, "rolename", roledm.getItemValue(i,
						"rolename"));
				dm.setItemValue(row, "apname", "wheres");
				dm.setItemValue(row, "apvalue", getRolewheres(roleid));
				dm.setdbStatus(row, RecordTrunk.DBSTATUS_SAVED);

			}

			tableChanged();
			table.autoSize();
		} catch (Exception e) {
			errorMessage("����", e.getMessage());
			return;
		}

	}

	HashMap<String, String> rolewheres = new HashMap<String, String>();

	/**
	 * ��ѯ��ɫ��Ȩwheres
	 */
	void queryWfroleap() {
		rolewheres.clear();
		String sql = "select * From np_wf_role_ap where wfid=" + wfid;
		RemotesqlHelper sh = new RemotesqlHelper();
		try {
			DBTableModel dm = sh.doSelect(sql, 0, 10000);
			for (int row = 0; row < dm.getRowCount(); row++) {
				String roleid = dm.getItemValue(row, "roleid");
				String wheres = dm.getItemValue(row, "apvalue");
				rolewheres.put(roleid, wheres);
			}
		} catch (Exception e) {
			errorMessage("����", e.getMessage());
		}

	}

	String getRolewheres(String roleid) {
		String s = rolewheres.get(roleid);
		if (s == null)
			s = "";
		return s;
	}

	@Override
	public int doSave() {
		ClientRequest req=new ClientRequest("npserver:���湤������Ȩ����");
		ParamCommand pcmd=new ParamCommand();
		pcmd.addParam("wfid", wfid);
		req.addCommand(pcmd);
		DataCommand dcmd=new DataCommand();
		dcmd.setDbmodel(getDBtableModel());
		req.addCommand(dcmd);
		
		try {
			ServerResponse svrresp=SendHelper.sendRequest(req);
			String respcmd=svrresp.getCommand();
			if(!respcmd.startsWith("+OK")){
				errorMessage("����", respcmd);
				return 0;
			}
			
			for(int i=0;i<getDBtableModel().getRowCount();i++){
				getDBtableModel().setdbStatus(i, RecordTrunk.DBSTATUS_SAVED);
			}
			
			infoMessage("��ʾ","����ɹ�");
			doExit();
		} catch (Exception e) {
			errorMessage("����", e.getMessage());
		}
		
		return 0;
	}

	public String getWfid() {
		return wfid;
	}

	public void setWfid(String wfid) {
		this.wfid = wfid;
		queryWfroleap();
	}

	@Override
	protected int on_actionPerformed(String command) {
		if(command.equals("��ǰ��ԱID")){
			setText("<��ǰ��ԱID>");
			return 0;
		}else if(command.equals("��ǰ����ID")){
			setText("<��ǰ����ID>");
			return 0;
		}else if(command.equals("��ǰ��ɫID")){
			setText("<��ǰ��ɫID>");
			return 0;
		}
		return super.on_actionPerformed(command);
	}

	void setText(String s){
		int row=getRow();
		if(row<0)return;
		DBColumnDisplayInfo col=getDBColumnDisplayInfo("apvalue");
		CTextArea textarea=(CTextArea) col.getEditComponent();
		textarea.getTextarea().replaceSelection(s);
	}
}
