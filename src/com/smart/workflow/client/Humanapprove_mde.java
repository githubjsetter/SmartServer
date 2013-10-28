package com.smart.workflow.client;

import java.util.Vector;

import javax.swing.JOptionPane;

import org.apache.log4j.Category;

import com.smart.client.system.Clientframe;
import com.smart.client.system.NpopManager;
import com.smart.extension.mde.CMdeModelAp;
import com.smart.extension.mde.MdeapSetupDialog;
import com.smart.extension.ste.Apinfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CTable;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMasterModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.mde.MMdeFrame;
import com.smart.platform.gui.mde.MdeFrame;
import com.smart.platform.gui.runop.Opnode;
import com.smart.platform.gui.ste.COpframe;
import com.smart.platform.gui.ste.MultisteFrame;
import com.smart.platform.gui.ste.Steframe;
import com.smart.platform.util.SendHelper;
import com.smart.server.install.Installinfo.Opinfo;
import com.smart.server.server.sysproc.GetroleoplistProcessor;

public class Humanapprove_mde extends CMdeModelAp{
	Category logger=Category.getInstance(Humanapprove_mde.class);

	public Humanapprove_mde(CFrame frame, String title) {
		super(frame, title);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected CDetailModel createDetailmodel() {
		return new Nodedata_ste(getParentFrame(),"审批依据",this);
	}

	@Override
	protected CMasterModel createMastermodel() {
		return new Humanapprove_ste(getParentFrame(),"待审批数据",this);
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
		if(command.equals("审批通过")){
			humanApprove(true,approvemsg);
			return 0;
		}else if(command.equals("审批拒绝")){
			humanApprove(false,approvemsg);
			return 0;
		}else if(command.equals("请求参批")){
			reqRef();
			return 0;
		}else if("查看单据".equals(command)){
			callOp();
		}
		return super.on_actionPerformed(command);
	}
	
	/**
	 * 调用功能
	 */
	void callOp(){
		int row=getMasterModel().getRow();
		if(row<0){
			warnMessage("提示","请选择待审的单据");
			return;
		}
		String wfnodeinstanceid=getMasterModel().getItemValue(row, "wfnodeinstanceid");
		//查询要调用的功能
		ClientRequest req=new ClientRequest("npserver:工作流查询调用功能");
		ParamCommand pcmd=new ParamCommand();
		req.addCommand(pcmd);
		pcmd.addParam("wfnodeinstanceid", wfnodeinstanceid);
		try {
			ServerResponse resp=SendHelper.sendRequest(req);
			String cmd=resp.getCommand();
			if(cmd.startsWith("+OK")==false){
				errorMessage("错误", cmd);
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
				warnMessage("提示", "需要在工作流定义功能中,设置调用的功能ID和调用参数才能使用本功能");
				return;
			}
			
			//调用功能
			Opnode opnode=new Opnode(callopid,opname);
			opnode.setClassname(classname);
			opnode.setGroupname(groupname);
			opnode.setProdname(prodname);
			opnode.setModulename(modulename);
			
			NpopManager.getInst().addOpinfo(callopid, opnode);
			Clientframe.getClientframe().runOp(callopid,false);
			
			//设置调用
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
				errorMessage("无法调用", "不明类型"+frm.getClass().getName());
			}
			
		} catch (Exception e) {
			errorMessage("错误",e.getMessage());
		}
		
	}
	

	/**
	 * 请求参批
	 */
	void reqRef() {
		int row=getMasterModel().getRow();
		if(row<0){
			warnMessage("提示","请先选择要参批的记录");
			return;
		}
		String wfnodeinstanceid=getMasterModel().getItemValue(row, "wfnodeinstanceid");
		//先选择人员.
		Pub_employee_hov hov=new Pub_employee_hov();
		DBTableModel result=hov.showDialog(getParentFrame(), "选择参批人员");
		if(result==null)return;
		String refemployeeid=result.getItemValue(0,"employeeid");
		String refemployeename=result.getItemValue(0,"employeename");
		
		//输入参批请求.
		String refmessage=JOptionPane.showInputDialog(getParentFrame(), "请输入参批请求", refemployeename+"您好:请审核....");
		if(refmessage==null)return;
		
		//发送参批
		ClientRequest req=new ClientRequest("npserver:请求参批");
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
				infoMessage("成功","请求参批成功");
			}else{
				warnMessage("失败",scmd.getString());
			}
		} catch (Exception e) {
			logger.error("error",e);
			errorMessage("错误", e.getMessage());
		}
	}

	void humanApprove(boolean approveflag,String approvemsg){
		WfnodeinstanceDbmodel senddm=new WfnodeinstanceDbmodel();
		DBTableModel dbmodel=getMasterModel().getDBtableModel();
		CTable table=getMasterModel().getTable();
		int rows[]= table.getSelectedRows();
		if(rows==null || rows.length==0){
			warnMessage("提示","选择一个或多个申请再审批");
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

		ClientRequest req=new ClientRequest("npserver:工作流审批");
		DataCommand dcmd=new DataCommand();
		req.addCommand(dcmd);
		dcmd.setDbmodel(senddm);
		
		try {
			ServerResponse resp=SendHelper.sendRequest(req);
			if(!resp.getCommand().startsWith("+OK")){
				logger.error(resp.getCommand());
				errorMessage("错误",resp.getCommand());
				return;
			}
			DataCommand respdcmd=(DataCommand) resp.commandAt(1);
			DBTableModel respdm=respdcmd.getDbmodel();
			//设置返回值．
			setResult(respdm);
			getMasterModel().tableChanged();
		} catch (Exception e) {
			logger.error("error",e);
			errorMessage("错误",e.getMessage());
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
						dbmodel.getRecordThunk(j).setSaveresult(0, "审批成功");
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
		warnMessage("提示", "在工作流程定义中直接设置数据授权");
		return false;
	}
	
	
}
