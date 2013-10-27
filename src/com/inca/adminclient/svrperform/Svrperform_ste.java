package com.inca.adminclient.svrperform;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JPanel;

import com.inca.adminclient.auth.AdminSendHelper;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.RecordTrunk;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.ste.CSteModel;

/**
 * ���������ܼ��
 * @author user
 *
 */
public class Svrperform_ste extends CSteModel{


	public Svrperform_ste(CFrame frame, String title) throws HeadlessException {
		super(frame, title);
		//this.setShowformonly(true);
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
	public JPanel getRootpanel() {
		JPanel rootp= super.getRootpanel();
		doQuery();
		return rootp;
	}

	@Override
	public void doQuery() {
		ClientRequest req=new ClientRequest("npserver:serverperform");
		try {
			setWaitCursor();
			ServerResponse resp=AdminSendHelper.sendRequest(req);
			dbmodel.clearAll();
			dbmodel.appendRow();
			ParamCommand pcmd=(ParamCommand) resp.commandAt(1);
			String sstarttime=pcmd.getValue("starttime");
			String snowtime=pcmd.getValue("nowtime");
			String srequestcount=pcmd.getValue("requestcount");
			String sprocesscount=pcmd.getValue("processcount");
			String sprocessms=pcmd.getValue("processms");
			
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			long ltmp=Long.parseLong(sstarttime);
			Date tmpdt=new Date();
			tmpdt.setTime(ltmp);
			dbmodel.setItemValue(0, "starttime", df.format(tmpdt));

			ltmp=Long.parseLong(snowtime);
			tmpdt.setTime(ltmp);
			dbmodel.setItemValue(0, "nowtime", df.format(tmpdt));

			
			dbmodel.setItemValue(0, "requestcount", srequestcount);
			dbmodel.setItemValue(0, "processcount", sprocesscount);
			dbmodel.setItemValue(0, "processms", sprocessms);
			
			double dprocesscount=Double.parseDouble(sprocesscount);
			double dprocessms=Double.parseDouble(sprocessms);
			String savgms="";
			DecimalFormat decf=new DecimalFormat("0.0000");
			if(dprocessms!=0){
				double avgms=dprocessms/dprocesscount;
				savgms=decf.format(avgms);
			}
			dbmodel.setItemValue(0, "avgms", savgms);
			
			//����������
			long l1=Long.parseLong(sstarttime);
			long l2=Long.parseLong(snowtime);
			double avgcount1 =  dprocesscount / (l2 - l1) * 1000.0;
			dbmodel.setItemValue(0, "avgcount1", decf.format(avgcount1));
			
			dbmodel.setdbStatus(0, RecordTrunk.DBSTATUS_SAVED);
			setRow(0);
			bindDataSetEnable(0);
			showForm();
		} catch (Exception e) {
			errorMessage("����", e.getMessage());
		}finally{
			setDefaultCursor();
		}
		
	}
	
	

/*	
	@Override
	protected void loadDBColumnInfos() {
		super.loadDBColumnInfos();
		if(true){
			return;
		}
		Vector<DBColumnDisplayInfo> cols=new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col=new DBColumnDisplayInfo("�к�","�к�","�к�");
		cols.add(col);
		
		col=new DBColumnDisplayInfo("starttiem","date","��ʼʱ��");
		col.setWithtime(true);
		cols.add(col);
		
		col=new DBColumnDisplayInfo("requestcount","number","�������");
		cols.add(col);
		
		col=new DBColumnDisplayInfo("processcount","number","�����������");
		cols.add(col);

		col=new DBColumnDisplayInfo("processms","number","������ʱ(MS)");
		cols.add(col);

		col=new DBColumnDisplayInfo("avgcount","number","ƽ������/��");
		cols.add(col);
		
		this.formcolumndisplayinfos=cols;
	}
*/

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("���¿�ʼ")){
			resetTime();
			return;
		}
		super.actionPerformed(e);
	}
	
	void resetTime(){
		ClientRequest req=new ClientRequest("npserver:resetserverperform");
		try {
			ServerResponse resp=AdminSendHelper.sendRequest(req);
			
			dbmodel.setItemValue(0, "starttime", "0000-00-00 00:00:00");
			dbmodel.setItemValue(0, "requestcount", "");
			dbmodel.setItemValue(0, "processcount", "");
			dbmodel.setItemValue(0, "processms", "");
			
			tableChanged(0);
			

		} catch (Exception e) {
			errorMessage("����", e.getMessage());
		}

	}

	@Override
	public void doRequery() {
		doQuery();
	}

}
