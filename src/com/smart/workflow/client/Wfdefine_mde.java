package com.smart.workflow.client;
import java.util.HashMap;

import org.apache.log4j.Category;

import com.smart.extension.mde.CMdeModelAp;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.demo.communicate.RemotesqlHelper;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CTable;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMasterModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.util.SendHelper;

/*����"���̶���"�ܵ�ϸĿModel*/
public class Wfdefine_mde extends CMdeModelAp{
	Category logger=Category.getInstance(Wfdefine_mde.class);
	
	public Wfdefine_mde(CFrame frame, String title) {
		super(frame, title);
		saveimmdiate=true;
	}
	protected CMasterModel createMastermodel() {
		return new Wfdefine_master(frame,this);
	}
	protected CDetailModel createDetailmodel() {
		return new Wfdefine_detail(frame,this);
	}
	public String getMasterRelatecolname() {
		return "wfid";
	}
	public String getDetailRelatecolname() {
		return "wfid";
	}
	public String getSaveCommandString() {
		return "Wfdefine_mde.�������̶���";
	}
	
	@Override
	protected int on_beforemodifymaster(int row) {
		return 0;
	}
	@Override
	protected int on_actionPerformed(String command) {
		if("����������".equals(command)){
			setupData();
			return 0;
		}else if("����������".equals(command)){
			setupNodeinstData();
		}else if("����ɫ��Ȩ".equals(command)){
			setupNodeRole();
		}else if("�����Ա��Ȩ".equals(command)){
			setupNodeEmployee();
		}else if("�������".equals(command)){
			checkWfdefine();
		}else if("����������Ȩ".equals(command)){
			setupAp();
		}
		return super.on_actionPerformed(command);
	}

	void checkWfdefine() {
		//�������
		int row=getMasterModel().getRow();
		if(row<0){
			warnMessage("��ʾ", "ѡ��һ������");
			return;
		}
		if(getMasterModel().getdbStatus(row)!=RecordTrunk.DBSTATUS_SAVED){
			warnMessage("��ʾ", "�������̺��ټ��");
			return;
		}
		
		String wfid=getMasterModel().getItemValue(row, "wfid");
		ParamCommand pcmd=new ParamCommand();
		pcmd.addParam("wfid", wfid);
		ClientRequest req=new ClientRequest("npworkflow:������̱��ʽ");
		req.addCommand(pcmd);
		
		try {
			ServerResponse resp=SendHelper.sendRequest(req);
			String rcmd=resp.getCommand();
			if(!rcmd.startsWith("+OK")){
				errorMessage("����",rcmd);
				return;
			}
			DataCommand dcmd=(DataCommand) resp.commandAt(1);
			DBTableModel checkdm=dcmd.getDbmodel();
			if(checkdm.getRowCount()==0){
				infoMessage("���ɹ�","û�з��ִ���");
				return;
			}
			//infoMessage("���ɹ�","����"+checkdm.getRowCount()+"����");
			ShowerrormsgDlg dlg=new ShowerrormsgDlg(getParentFrame(),checkdm);
			dlg.pack();
			dlg.setVisible(true);
			
		} catch (Exception e) {
			errorMessage("����",e.getMessage());
			return;
		}
		
		
	}
	/**
	 * ������Ա
	 */
	void setupNodeEmployee() {
		int row=getDetailModel().getRow();
		if(row<0){
			warnMessage("��ʾ","���������������,�����ý����Ա");
			return;
		}
		
		if(getDetailModel().getdbStatus(row)==RecordTrunk.DBSTATUS_NEW){
			warnMessage("��ʾ","���ȱ�����������,�����ý����Ա");
			return;
		}
		
		String wfnodeid=getDetailModel().getItemValue(row, "wfnodeid");
		//String wfid=getDetailModel().getItemValue(row, "wfid");
		
		Wfnodeemp_frame frm=new Wfnodeemp_frame();
		frm.pack();
		frm.setWfnodeid(wfnodeid);
		frm.setVisible(true);
		
	}

	void setupNodeRole() {
		int row=getDetailModel().getRow();
		if(row<0){
			warnMessage("��ʾ","���������������,�����ý���ɫ");
			return;
		}
		
		if(getDetailModel().getdbStatus(row)==RecordTrunk.DBSTATUS_NEW){
			warnMessage("��ʾ","���ȱ�����������,�����ý���ɫ");
			return;
		}
		
		String wfnodeid=getDetailModel().getItemValue(row, "wfnodeid");
		//String wfid=getDetailModel().getItemValue(row, "wfid");
		
		Wfnoderole_frame frm=new Wfnoderole_frame();
		frm.pack();
		frm.setWfnodeid(wfnodeid);
		frm.setVisible(true);
		
	}
	
	/**
	 * ���ý���������
	 */
	void setupNodeinstData() {
		int row=getDetailModel().getRow();
		if(row<0){
			warnMessage("��ʾ","���������������,�����ý���������");
			return;
		}
		
		if(getDetailModel().getdbStatus(row)==RecordTrunk.DBSTATUS_NEW){
			warnMessage("��ʾ","���ȱ�����������,�����ý���������");
			return;
		}
		
		String wfnodeid=getDetailModel().getItemValue(row, "wfnodeid");
		String wfid=getDetailModel().getItemValue(row, "wfid");
		//��ѯ���ڵĶ���
		Nodeinstdata_ste ste=new Nodeinstdata_ste(null,"");
		ste.getRootpanel();
		try {
			ste.setUsequerythread(false);
			ste.doQuery("wfnodeid="+wfnodeid);
			DBTableModel dm=ste.getDBtableModel();
			NodeinstData_hov hov=new NodeinstData_hov();
			hov.setNodedatadm(dm);
			DBTableModel result=hov.showDialog(getParentFrame(), "������ʾ�ľ������� ��Ctrl��Shift��ѡ","","","wfid="+wfid);
			if(result==null)return;
			CTable table=hov.getDlgtable();
			int rows[]=table.getSelectedRows();
			DBTableModel hovdm=(DBTableModel) table.getModel();
			HashMap<String, String> selecteddataitemmap=new HashMap<String, String>();
			for(int i=0;i<rows.length;i++){
				String dataitemid=hovdm.getItemValue(rows[i], "dataitemid");
				String sortno=hovdm.getItemValue(rows[i], "sortno");
				selecteddataitemmap.put(dataitemid,dataitemid);
				
				//�Ƿ�Ҫ����?
				boolean has=false;
				int foundrow=0;
				for(int j=0;j<dm.getRowCount();j++){
					String tmpdataitemid=dm.getItemValue(j, "dataitemid");
					if(tmpdataitemid.equals(dataitemid)){
						has=true;
						foundrow=j;
						break;
					}
				}
				if(!has){
					//����
					int newrow=dm.getRowCount();
					ste.doNew();
					dm.setItemValue(newrow, "dataitemid", dataitemid);
					dm.setItemValue(newrow, "wfnodeid", wfnodeid);
					dm.setItemValue(newrow, "sortno", sortno);
					
				}else{
					dm.setItemValue(foundrow, "sortno", sortno);
				}
				
			}
			
			//���ɾ��
			for(int r=0;r<dm.getRowCount();r++){
				String dataitemid=dm.getItemValue(r, "dataitemid");
				if(selecteddataitemmap.get(dataitemid)==null){
					//ɾ��
					dm.setdbStatus(r, RecordTrunk.DBSTATUS_DELETE);
				}
			}
			
			ste.savetoserver(null);
			
		} catch (Exception e) {
			logger.error("error",e);
			errorMessage("����",e.getMessage());
			return;
		}
		
		
	}

	void setupData(){
		int row=getMasterModel().getRow();
		if(row<0){
			warnMessage("��ʾ","�����������̲�����ɹ����ٶ���������");
			return;
		}
		
		if(getMasterModel().getdbStatus(row)==RecordTrunk.DBSTATUS_NEW){
			warnMessage("��ʾ","���ȱ������̳ɹ����ٶ���������");
			return;
		}
		
		String wfid=getMasterModel().getItemValue(row, "wfid");
		String basetablename=getMasterModel().getItemValue(row, "viewname");
		if(basetablename.length()==0){
			basetablename=getMasterModel().getItemValue(row, "tablename");
		}
		if(basetablename.length()==0){
			warnMessage("��ʾ","��������������ƣ�������ɹ����ٶ���������");
			return;
		}
		Dataitemedit_frm datafrm=new Dataitemedit_frm();
		datafrm.pack();
		Dataitemedit_ste dataste=(Dataitemedit_ste)datafrm.getCreatedStemodel();
		dataste.setBasetablename(basetablename);
		dataste.setWfid(wfid);
		datafrm.setVisible(true);
		dataste.doQuery("");
	}
	@Override
	protected boolean isAllownodetail() {
		//����û��ϸ��
		return true;
	}
	
	void setupAp(){
		int row=getMasterModel().getRow();
		if(row<0){
			warnMessage("��ʾ","�����������̲�����ɹ����ٶ���������");
			return;
		}
		
		if(getMasterModel().getdbStatus(row)==RecordTrunk.DBSTATUS_NEW){
			warnMessage("��ʾ","���ȱ������̳ɹ����ٶ���������");
			return;
		}
		
		String wfid=getMasterModel().getItemValue(row, "wfid");
		Wfap_frame frm=new Wfap_frame();
		frm.pack();
		Wfap_ste ste=(Wfap_ste)frm.getCreatedStemodel();
		ste.setWfid(wfid);
		frm.setVisible(true);
		
	}
}
