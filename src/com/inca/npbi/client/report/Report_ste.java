package com.inca.npbi.client.report;

import java.awt.HeadlessException;
import java.util.Vector;

import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.RecordTrunk;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.CTable;
import com.inca.np.gui.control.CTextArea;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.Hovdefine;
import com.inca.np.util.SendHelper;
import com.inca.npbi.client.tablecolumn.Tablecolumn_frame;
import com.inca.npbi.client.tablecolumn.Tablecolumn_ste;
import com.inca.npbi.client.view.View_frame;
import com.inca.npbi.client.view.View_ste;

/*功能"报表定义"单表编辑Model*/
public class Report_ste extends CSteModel {

	public Report_ste(CFrame frame) throws HeadlessException {
		super(frame, "报表定义");
		Hovdefine hovdef=new Hovdefine("com.inca.npbi.client.report.Posttreate_hov","posttreate");
		hovdef.putColpair("expr", "posttreate");
		getDBColumnDisplayInfo("posttreate").setHovdefine(hovdef);
		
		hovdef=new Hovdefine("com.inca.npbi.client.report.Calccolumn_hov","calccolumns");
		hovdef.putColpair("expr", "calccolumns");
		getDBColumnDisplayInfo("calccolumns").setHovdefine(hovdef);
	}

	public String getTablename() {
		return "npbi_report_def";
	}

	public String getSaveCommandString() {
		return "Report_ste.保存报表定义";
	}

	@Override
	protected int on_actionPerformed(String command) {
		if (command.equals("定义基表列")) {
			setupColumn();
			return 0;
		} else if (command.equals("定义视图")) {
			setupView();
			return 0;
		} else if (command.equals("检查")) {
			checkReport();
		}
		return super.on_actionPerformed(command);
	}

	void checkReport() {
		int row = getRow();
		if (row < 0) {
			warnMessage("提示", "保存报表定义后再检查");
			return;
		}

		int status = getdbStatus(row);
		if (status != RecordTrunk.DBSTATUS_SAVED) {
			warnMessage("提示", "保存报表定义后再检查");
			return;
		}
		String reportid=getItemValue(row, "reportid");

		Vector<DBColumnDisplayInfo> cols=new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col=new DBColumnDisplayInfo("reportid","number");
		cols.add(col);
		
		col=new DBColumnDisplayInfo("status","varchar");
		cols.add(col);

		col=new DBColumnDisplayInfo("message","varchar");
		cols.add(col);
		
		DBTableModel dm=new DBTableModel(cols);
		int newrow=dm.getRowCount();
		dm.appendRow();
		dm.setItemValue(newrow, "reportid", reportid);
		ClientRequest req=new ClientRequest("npbi.检查报表定义");
		DataCommand dcmd=new DataCommand();
		dcmd.setDbmodel(dm);
		req.addCommand(dcmd);
		try {
			ServerResponse resp=SendHelper.sendRequest(req);
			String s=resp.getCommand();
			if(!s.startsWith("+OK")){
				errorMessage("错误", s);
				return;
			}
			DataCommand respdcmd=(DataCommand) resp.commandAt(1);
			dm=respdcmd.getDbmodel();
			String respstatus=dm.getItemValue(0, "status");
			if(respstatus.length()>0){
				errorMessage("检查出错误",dm.getItemValue(0, "message"));
				return;
			}else{
				infoMessage("检查结果","检查无误");
			}
		} catch (Exception e) {
			errorMessage("检查失败",e.getMessage());
		}
		
	}

	void setupColumn() {
		int row = getRow();
		if (row < 0) {
			warnMessage("提示", "保存报表定义后再定义基表列");
			return;
		}

		int status = getdbStatus(row);
		if (status != RecordTrunk.DBSTATUS_SAVED) {
			warnMessage("提示", "保存报表定义后再定义基表列");
			return;
		}
		String reportid = getItemValue(row, "reportid");
		Tablecolumn_frame frm = new Tablecolumn_frame();
		frm.pack();
		Tablecolumn_ste ste = (Tablecolumn_ste) frm.getCreatedStemodel();
		ste.setReportid(reportid);
		frm.setVisible(true);
		ste.doQuery("reportid=" + reportid);

	}

	void setupView() {
		int row = getRow();
		if (row < 0) {
			warnMessage("提示", "保存报表定义后再定义视图");
			return;
		}

		int status = getdbStatus(row);
		if (status != RecordTrunk.DBSTATUS_SAVED) {
			warnMessage("提示", "保存报表定义后再定义视图");
			return;
		}
		String reportid = getItemValue(row, "reportid");
		View_frame frm = new View_frame();
		frm.pack();
		View_ste ste = (View_ste) frm.getCreatedStemodel();
		ste.setReportid(reportid);
		frm.setVisible(true);
		ste.doQuery("reportid=" + reportid);
	}
	
	
	@Override
	protected void invokeMultimdehov(int row, String colname, String value) {
		if(colname.equalsIgnoreCase("posttreate")){
			Posttreate_hov hov=new Posttreate_hov();
			DBTableModel result=hov.showDialog(getParentFrame(), "后处理帮助");
			if(result==null)return;
			CTable table=hov.getDlgtable();
			DBTableModel dm=(DBTableModel) table.getModel();
			int rows[]=table.getSelectedRows();
			
			for(int i=0;i<rows.length;i++){
				int tmprow=rows[i];
				String expr=dm.getItemValue(tmprow, "expr");
				CTextArea textarea=(CTextArea) getDBColumnDisplayInfo(colname).getEditComponent();
				textarea.getTextarea().replaceSelection(expr+"\n");
			}
			return;
		}else if(colname.equalsIgnoreCase("calccolumns")){
			Calccolumn_hov hov=new Calccolumn_hov();
			DBTableModel result=hov.showDialog(getParentFrame(), "计算列帮助");
			if(result==null)return;
			CTable table=hov.getDlgtable();
			DBTableModel dm=(DBTableModel) table.getModel();
			int rows[]=table.getSelectedRows();
			
			for(int i=0;i<rows.length;i++){
				int tmprow=rows[i];
				String expr=dm.getItemValue(tmprow, "expr");
				CTextArea textarea=(CTextArea) getDBColumnDisplayInfo(colname).getEditComponent();
				textarea.getTextarea().replaceSelection(expr+"\n");
			}
			return;
		}
		super.invokeMultimdehov(row, colname, value);
	}
	
}
