package com.smart.bi.client.preview;

import java.awt.HeadlessException;
import java.util.Enumeration;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Category;

import com.smart.extension.ste.CSteModelAp;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.util.SendHelper;

public class Reportpreview_ste  extends CSteModel {
	Category logger = Category.getInstance(Reportpreview_ste.class);
	String instanceid = "";
	String basetablename="";
	String npbi_instanceid="";


	public Reportpreview_ste(CFrame frame, String title, String instanceid) throws HeadlessException {
		this.frame = frame;
		this.title = title;
		this.instanceid=instanceid;

		
		// 加载专项 
		loadDBColumnInfos();
		loadRuleenginee();

		if (ruleeng != null) {
			ruleeng.process(this, "设置下拉选择");
			ruleeng.process(this, "设置系统下拉选择");
			ruleeng.process(this, "设置SQL下拉选择");
			ruleeng.process(this, "表格可以编辑");
		}

		if (initdelegate != null) {
			initdelegate.on_init(this);
		}

		DBColumnDisplayInfo colinfo = getDBColumnDisplayInfo("filegroupid");
		useattachfile = colinfo != null;
		useap = true;
	}

	@Override
	protected void loadDBColumnInfos() {
		//从服务器下载定义
		ClientRequest req=new ClientRequest("npbi:取报表预览dbtablemodel");
		ParamCommand pcmd=new ParamCommand();
		pcmd.addParam("instanceid", instanceid);
		req.addCommand(pcmd);
		try {
			ServerResponse resp=SendHelper.sendRequest(req);
			String s=resp.getCommand();
			if(!s.startsWith("+OK")){
				errorMessage("错误", s);
				return;
			}
			DataCommand respdcmd=(DataCommand) resp.commandAt(1);
			DBTableModel dm=respdcmd.getDbmodel();
			this.formcolumndisplayinfos=dm.getDisplaycolumninfos();
			Enumeration<DBColumnDisplayInfo>en=formcolumndisplayinfos.elements();
			while(en.hasMoreElements()){
				DBColumnDisplayInfo col=en.nextElement();
				if(col.getColtype().equals("number")){
					col.setCalcsum(true);
				}
			}
			
			ParamCommand resppcmd=(ParamCommand) resp.commandAt(2);
			basetablename=resppcmd.getValue("basetablename");
			npbi_instanceid=resppcmd.getValue("npbi_instanceid");
		} catch (Exception e) {
			logger.error("Error", e);
			errorMessage("错误",e.getMessage());
			return;
		} finally {
		}
		

	}

	@Override
	public String getTablename() {
		return basetablename;
	}

	@Override
	public String getSaveCommandString() {
		return "do nothind";
	}
	
	@Override
	public void doQuery() {
		super.doQuery("npbi_instanceid='"+npbi_instanceid+"' order by npbi_lineno");
	}

	@Override
	public JPanel getRootpanel() {
		JPanel jp=super.getRootpanel();
		Runnable r=new Runnable(){
			public void run(){
				doQuery();
			}
		};
		SwingUtilities.invokeLater(r);
		return jp;
	}

	
}
