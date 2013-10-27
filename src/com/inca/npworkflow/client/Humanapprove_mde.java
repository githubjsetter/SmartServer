package com.inca.npworkflow.client;

import java.util.Vector;

import javax.swing.JOptionPane;

import org.apache.log4j.Category;

import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.RecordTrunk;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.CTable;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.mde.CDetailModel;
import com.inca.np.gui.mde.CMasterModel;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.mde.MMdeFrame;
import com.inca.np.gui.mde.MdeFrame;
import com.inca.np.gui.runop.Opnode;
import com.inca.np.gui.ste.COpframe;
import com.inca.np.gui.ste.MultisteFrame;
import com.inca.np.gui.ste.Steframe;
import com.inca.np.util.SendHelper;
import com.inca.npclient.system.Clientframe;
import com.inca.npclient.system.NpopManager;
import com.inca.npserver.install.Installinfo.Opinfo;
import com.inca.npserver.server.sysproc.GetroleoplistProcessor;
import com.inca.npx.mde.CMdeModelAp;
import com.inca.npx.mde.MdeapSetupDialog;
import com.inca.npx.ste.Apinfo;

public class Humanapprove_mde extends CMdeModelAp{
	Category logger=Category.getInstance(Humanapprove_mde.class);

	public Humanapprove_mde(CFrame frame, String title) {
		super(frame, title);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected CDetailModel createDetailmodel() {
		return new Nodedata_ste(getParentFrame(),"��������",this);
	}

	@Override
	protected CMasterModel createMastermodel() {
		return new Humanapprove_ste(getParentFrame(),"����������",this);
	}

	@Override
	public String getDetailRelatecolname() {
		
		return "wfnodeinstanceid";
	}

	@Override
	public String getMasterRelatecolname() {
		
		return "wfnodeinstanceid";
	}

	@Override
	public String getSaveCommandString() {
		
		return "";
	}

	@Override
	protected void retrieveDetail(int newrow, int oldrow) {
		
		super.retrieveDetail(newrow, oldrow);
	}

	@Override
	protected int on_actionPerformed(String command) {
		String approvemsg=((Humanapprove_ste)getMasterModel()).getApprovemsg();
		if(command.equals("����ͨ��")){
			humanApprove(true,approvemsg);
			return 0;
		}else if(command.equals("�����ܾ�")){
			humanApprove(false,approvemsg);
			return 0;
		}else if(command.equals("�������")){
			reqRef();
			return 0;
		}else if("�鿴����".equals(command)){
			callOp();
		}
		return super.on_actionPerformed(command);
	}
	
	/**
	 * ���ù���
	 */
	void callOp(){
		int row=getMasterModel().getRow();
		if(row<0){
			warnMessage("��ʾ","��ѡ�����ĵ���");
			return;
		}
		String wfnodeinstanceid=getMasterModel().getItemValue(row, "wfnodeinstanceid");
		//��ѯҪ���õĹ���
		ClientRequest req=new ClientRequest("npserver:��������ѯ���ù���");
		ParamCommand pcmd=new ParamCommand();
		req.addCommand(pcmd);
		pcmd.addParam("wfnodeinstanceid", wfnodeinstanceid);
		try {
			ServerResponse resp=SendHelper.sendRequest(req);
			String cmd=resp.getCommand();
			if(cmd.startsWith("+OK")==false){
				errorMessage("����", cmd);
				return;
			}
			ParamCommand resppcmd=(ParamCommand) resp.commandAt(1);
			String callopid=resppcmd.getValue("callopid");
			String opname=resppcmd.getValue("opname");
			String groupname=resppcmd.getValue("groupname");
			String callcond=resppcmd.getValue("callcond");
			String classname=resppcmd.getValue("classname");
			String prodname=resppcmd.getValue("prodname");
			String modulename=resppcmd.getValue("modulename");
			
			if(callopid.length()==0 || callcond.length()==0){
				warnMessage("��ʾ", "��Ҫ�ڹ��������幦����,���õ��õĹ���ID�͵��ò�������ʹ�ñ�����");
				return;
			}
			
			//���ù���
			Opnode opnode=new Opnode(callopid,opname);
			opnode.setClassname(classname);
			opnode.setGroupname(groupname);
			opnode.setProdname(prodname);
			opnode.setModulename(modulename);
			
			NpopManager.getInst().addOpinfo(callopid, opnode);
			Clientframe.getClientframe().runOp(callopid,false);
			
			//���õ���
			COpframe frm=(COpframe) opnode.getRunningframe();
			if(frm instanceof Steframe){
				Steframe stefrm=(Steframe)frm;
				stefrm.getCreatedStemodel().doQuery(callcond);
			}else if(frm instanceof MdeFrame){
				MdeFrame mdefrm=(MdeFrame)frm;
				mdefrm.getCreatedMdemodel().getMasterModel().doQuery(callcond);
			}else if(frm instanceof MultisteFrame){
				MultisteFrame mste=(MultisteFrame)frm;
				mste.getCreatedStemodel().doQuery(callcond);
			}else if(frm instanceof MMdeFrame){
				MMdeFrame mmdefrm=(MMdeFrame)frm;
				mmdefrm.getCreatedStemodel().doQuery(callcond);
			}else{
				errorMessage("�޷�����", "��������"+frm.getClass().getName());
			}
			
		} catch (Exception e) {
			errorMessage("����",e.getMessage());
		}
		
	}
	

	/**
	 * �������
	 */
	void reqRef() {
		int row=getMasterModel().getRow();
		if(row<0){
			warnMessage("��ʾ","����ѡ��Ҫ�����ļ�¼");
			return;
		}
		String wfnodeinstanceid=getMasterModel().getItemValue(row, "wfnodeinstanceid");
		//��ѡ����Ա.
		Pub_employee_hov hov=new Pub_employee_hov();
		DBTableModel result=hov.showDialog(getParentFrame(), "ѡ�������Ա");
		if(result==null)return;
		String refemployeeid=result.getItemValue(0,"employeeid");
		String refemployeename=result.getItemValue(0,"employeename");
		
		//�����������.
		String refmessage=JOptionPane.showInputDialog(getParentFrame(), "�������������", refemployeename+"����:�����....");
		if(refmessage==null)return;
		
		//���Ͳ���
		ClientRequest req=new ClientRequest("npserver:�������");
		ParamCommand pcmd=new ParamCommand();
		req.addCommand(pcmd);
		pcmd.addParam("wfnodeinstanceid", wfnodeinstanceid);
		pcmd.addParam("refemployeeid", refemployeeid);
		pcmd.addParam("refmessage", refmessage);
		try {
			ServerResponse resp=SendHelper.sendRequest(req);
			StringCommand scmd=(StringCommand) resp.commandAt(0);
			if(scmd.getString().startsWith("+OK")){
				getMasterModel().setItemValue(row, "refflag", "1");
				getMasterModel().setdbStatus(row, RecordTrunk.DBSTATUS_SAVED);
				infoMessage("�ɹ�","��������ɹ�");
			}else{
				warnMessage("ʧ��",scmd.getString());
			}
		} catch (Exception e) {
			logger.error("error",e);
			errorMessage("����", e.getMessage());
		}
	}

	void humanApprove(boolean approveflag,String approvemsg){
		WfnodeinstanceDbmodel senddm=new WfnodeinstanceDbmodel();
		DBTableModel dbmodel=getMasterModel().getDBtableModel();
		CTable table=getMasterModel().getTable();
		int rows[]= table.getSelectedRows();
		if(rows==null || rows.length==0){
			warnMessage("��ʾ","ѡ��һ����������������");
			return;
		}
		
		for(int i=0;i<rows.length;i++){
			int row=rows[i];
			String wfnodeinstanceid=dbmodel.getItemValue(row, "wfnodeinstanceid");
			int newrow=senddm.getRowCount();
			senddm.appendRow();
			senddm.setItemValue(newrow, "wfnodeinstanceid",wfnodeinstanceid);
			senddm.setItemValue(newrow, "approveflag",approveflag?"1":"0");
			senddm.setItemValue(newrow, "approvemsg",approvemsg);
		}

		ClientRequest req=new ClientRequest("npserver:����������");
		DataCommand dcmd=new DataCommand();
		req.addCommand(dcmd);
		dcmd.setDbmodel(senddm);
		
		try {
			ServerResponse resp=SendHelper.sendRequest(req);
			if(!resp.getCommand().startsWith("+OK")){
				logger.error(resp.getCommand());
				errorMessage("����",resp.getCommand());
				return;
			}
			DataCommand respdcmd=(DataCommand) resp.commandAt(1);
			DBTableModel respdm=respdcmd.getDbmodel();
			//���÷���ֵ��
			setResult(respdm);
			getMasterModel().tableChanged();
		} catch (Exception e) {
			logger.error("error",e);
			errorMessage("����",e.getMessage());
			return;
		}
	}

	void setResult(DBTableModel respdm) {
		DBTableModel dbmodel=getMasterModel().getDBtableModel();

		for(int i=0;i<respdm.getRowCount();i++){
			String wfnodeinstanceid=respdm.getItemValue(i, "wfnodeinstanceid");
			String treateresult=respdm.getItemValue(i, "treateresult");
			for(int j=0;j<dbmodel.getRowCount();j++){
				if(dbmodel.getItemValue(j, "wfnodeinstanceid").equals(wfnodeinstanceid)){
					if(treateresult.startsWith("+OK")){
						dbmodel.getRecordThunk(j).setSaveresult(0, "�����ɹ�");
					}else{
						dbmodel.getRecordThunk(j).setSaveresult(1, treateresult);
					}
					break;
				}
			}
		}
	}

	@Override
	public boolean setupAp(String roleid) {
		warnMessage("��ʾ", "�ڹ������̶�����ֱ������������Ȩ");
		return false;
	}
	
	
}
