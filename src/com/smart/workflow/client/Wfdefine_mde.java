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

/*功能"流程定义"总单细目Model*/
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
		return "Wfdefine_mde.保存流程定义";
	}
	
	@Override
	protected int on_beforemodifymaster(int row) {
		return 0;
	}
	@Override
	protected int on_actionPerformed(String command) {
		if("定义数据项".equals(command)){
			setupData();
			return 0;
		}else if("结点决策数据".equals(command)){
			setupNodeinstData();
		}else if("结点角色授权".equals(command)){
			setupNodeRole();
		}else if("结点人员授权".equals(command)){
			setupNodeEmployee();
		}else if("检查流程".equals(command)){
			checkWfdefine();
		}else if("设置数据授权".equals(command)){
			setupAp();
		}
		return super.on_actionPerformed(command);
	}

	void checkWfdefine() {
		//检查流程
		int row=getMasterModel().getRow();
		if(row<0){
			warnMessage("提示", "选择一个流程");
			return;
		}
		if(getMasterModel().getdbStatus(row)!=RecordTrunk.DBSTATUS_SAVED){
			warnMessage("提示", "保存流程后再检查");
			return;
		}
		
		String wfid=getMasterModel().getItemValue(row, "wfid");
		ParamCommand pcmd=new ParamCommand();
		pcmd.addParam("wfid", wfid);
		ClientRequest req=new ClientRequest("npworkflow:检查流程表达式");
		req.addCommand(pcmd);
		
		try {
			ServerResponse resp=SendHelper.sendRequest(req);
			String rcmd=resp.getCommand();
			if(!rcmd.startsWith("+OK")){
				errorMessage("错误",rcmd);
				return;
			}
			DataCommand dcmd=(DataCommand) resp.commandAt(1);
			DBTableModel checkdm=dcmd.getDbmodel();
			if(checkdm.getRowCount()==0){
				infoMessage("检查成功","没有发现错误");
				return;
			}
			//infoMessage("检查成功","发现"+checkdm.getRowCount()+"错误");
			ShowerrormsgDlg dlg=new ShowerrormsgDlg(getParentFrame(),checkdm);
			dlg.pack();
			dlg.setVisible(true);
			
		} catch (Exception e) {
			errorMessage("错误",e.getMessage());
			return;
		}
		
		
	}
	/**
	 * 设置人员
	 */
	void setupNodeEmployee() {
		int row=getDetailModel().getRow();
		if(row<0){
			warnMessage("提示","请先新增保存结点后,再设置结点人员");
			return;
		}
		
		if(getDetailModel().getdbStatus(row)==RecordTrunk.DBSTATUS_NEW){
			warnMessage("提示","请先保存新增结点后,再设置结点人员");
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
			warnMessage("提示","请先新增保存结点后,再设置结点角色");
			return;
		}
		
		if(getDetailModel().getdbStatus(row)==RecordTrunk.DBSTATUS_NEW){
			warnMessage("提示","请先保存新增结点后,再设置结点角色");
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
	 * 设置结点决策数据
	 */
	void setupNodeinstData() {
		int row=getDetailModel().getRow();
		if(row<0){
			warnMessage("提示","请先新增保存结点后,再设置结点决策数据");
			return;
		}
		
		if(getDetailModel().getdbStatus(row)==RecordTrunk.DBSTATUS_NEW){
			warnMessage("提示","请先保存新增结点后,再设置结点决策数据");
			return;
		}
		
		String wfnodeid=getDetailModel().getItemValue(row, "wfnodeid");
		String wfid=getDetailModel().getItemValue(row, "wfid");
		//查询现在的定义
		Nodeinstdata_ste ste=new Nodeinstdata_ste(null,"");
		ste.getRootpanel();
		try {
			ste.setUsequerythread(false);
			ste.doQuery("wfnodeid="+wfnodeid);
			DBTableModel dm=ste.getDBtableModel();
			NodeinstData_hov hov=new NodeinstData_hov();
			hov.setNodedatadm(dm);
			DBTableModel result=hov.showDialog(getParentFrame(), "设置显示的决策数据 按Ctrl或Shift多选","","","wfid="+wfid);
			if(result==null)return;
			CTable table=hov.getDlgtable();
			int rows[]=table.getSelectedRows();
			DBTableModel hovdm=(DBTableModel) table.getModel();
			HashMap<String, String> selecteddataitemmap=new HashMap<String, String>();
			for(int i=0;i<rows.length;i++){
				String dataitemid=hovdm.getItemValue(rows[i], "dataitemid");
				String sortno=hovdm.getItemValue(rows[i], "sortno");
				selecteddataitemmap.put(dataitemid,dataitemid);
				
				//是否要新增?
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
					//新增
					int newrow=dm.getRowCount();
					ste.doNew();
					dm.setItemValue(newrow, "dataitemid", dataitemid);
					dm.setItemValue(newrow, "wfnodeid", wfnodeid);
					dm.setItemValue(newrow, "sortno", sortno);
					
				}else{
					dm.setItemValue(foundrow, "sortno", sortno);
				}
				
			}
			
			//检查删除
			for(int r=0;r<dm.getRowCount();r++){
				String dataitemid=dm.getItemValue(r, "dataitemid");
				if(selecteddataitemmap.get(dataitemid)==null){
					//删除
					dm.setdbStatus(r, RecordTrunk.DBSTATUS_DELETE);
				}
			}
			
			ste.savetoserver(null);
			
		} catch (Exception e) {
			logger.error("error",e);
			errorMessage("错误",e.getMessage());
			return;
		}
		
		
	}

	void setupData(){
		int row=getMasterModel().getRow();
		if(row<0){
			warnMessage("提示","请先新增流程并保存成功后再定义数据项");
			return;
		}
		
		if(getMasterModel().getdbStatus(row)==RecordTrunk.DBSTATUS_NEW){
			warnMessage("提示","请先保存流程成功后再定义数据项");
			return;
		}
		
		String wfid=getMasterModel().getItemValue(row, "wfid");
		String basetablename=getMasterModel().getItemValue(row, "viewname");
		if(basetablename.length()==0){
			basetablename=getMasterModel().getItemValue(row, "tablename");
		}
		if(basetablename.length()==0){
			warnMessage("提示","请先输入基表名称，并保存成功后再定义数据项");
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
		//可以没有细单
		return true;
	}
	
	void setupAp(){
		int row=getMasterModel().getRow();
		if(row<0){
			warnMessage("提示","请先新增流程并保存成功后再定义数据项");
			return;
		}
		
		if(getMasterModel().getdbStatus(row)==RecordTrunk.DBSTATUS_NEW){
			warnMessage("提示","请先保存流程成功后再定义数据项");
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
