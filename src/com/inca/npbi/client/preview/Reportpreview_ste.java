package com.inca.npbi.client.preview;

import java.awt.HeadlessException;
import java.util.Enumeration;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Category;

import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.util.SendHelper;
import com.inca.npx.ste.CSteModelAp;

public class Reportpreview_ste  extends CSteModel {
	Category logger = Category.getInstance(Reportpreview_ste.class);
	String instanceid = "";
	String basetablename="";
	String npbi_instanceid="";


	public Reportpreview_ste(CFrame frame, String title, String instanceid) throws HeadlessException {
		this.frame = frame;
		this.title = title;
		this.instanceid=instanceid;

		
		// ����ר�� 
		loadDBColumnInfos();
		loadRuleenginee();

		if (ruleeng != null) {
			ruleeng.process(this, "��������ѡ��");
			ruleeng.process(this, "����ϵͳ����ѡ��");
			ruleeng.process(this, "����SQL����ѡ��");
			ruleeng.process(this, "�����Ա༭");
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
		//�ӷ��������ض���
		ClientRequest req=new ClientRequest("npbi:ȡ����Ԥ��dbtablemodel");
		ParamCommand pcmd=new ParamCommand();
		pcmd.addParam("instanceid", instanceid);
		req.addCommand(pcmd);
		try {
			ServerResponse resp=SendHelper.sendRequest(req);
			String s=resp.getCommand();
			if(!s.startsWith("+OK")){
				errorMessage("����", s);
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
			errorMessage("����",e.getMessage());
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
