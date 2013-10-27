package com.inca.npbi.client.instance;

import java.awt.HeadlessException;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.JOptionPane;

import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.Hovdefine;
import com.inca.np.util.SendHelper;
import com.inca.npbi.client.preview.Reportpreview_frame;

/*功能"报表实例管理"单表编辑Model*/
public class Instance_ste extends CSteModel {

	public Instance_ste(CFrame frame) throws HeadlessException {
		super(frame, "报表实例");
		Hovdefine hovdefine = new Hovdefine(
				"com.inca.npbi.client.report.Report_hov", "hovid");
		hovdefine.putColpair("reportid", "reportid");
		hovdefine.putColpair("reportname", "reportname");
		DBColumnDisplayInfo col = getDBColumnDisplayInfo("reportid");
		col.setHovdefine(hovdefine);

	}

	public String getTablename() {
		return "npbi_instance_v";
	}

	public String getSaveCommandString() {
		return "Instance_ste.保存实例";
	}

	@Override
	protected int on_actionPerformed(String command) {
		if (command.equals("立即计算")) {
			calcInstance();
			return 0;
		} else if (command.equals("设置重算")) {
			setRecalc();
			return 0;
		} else if (command.equals("预览数据")) {
			preview();
			return 0;
		} else if (command.equals("批量生成")) {
			batchGen();
			return 0;
		} else {
			return super.on_actionPerformed(command);
		}
	}
	
	 void preview() {
		 int row=getRow();
		 if(row<0){
			 warnMessage("提示", "请查询选择一个报表实例");
			 return;
		 }
		 String instanceid=getItemValue(row, "instanceid");
		 String reportname=getItemValue(row, "reportname");
		 Reportpreview_frame frm=new Reportpreview_frame(reportname+"预览",instanceid);
		 frm.pack();
		 frm.setVisible(true);
	}

	void calcInstance(){
		Vector<DBColumnDisplayInfo> cols=new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col=new DBColumnDisplayInfo("instanceid","number");
		cols.add(col);
		DBTableModel dm=new DBTableModel(cols);

		int rows[]=getTable().getSelectedRows();
		if(rows==null || rows.length==0){
			warnMessage("提示", "先选择要重新的记录");
			return;
		}
		for(int i=0;i<rows.length;i++){
			int row=rows[i];
			int newrow=dm.getRowCount();
			dm.appendRow();
			dm.setItemValue(newrow, "instanceid", dbmodel.getItemValue(row, "instanceid"));
		}
		ClientRequest req=new ClientRequest("npbi.计算报表实例");
		DataCommand dcmd=new DataCommand();
		dcmd.setDbmodel(dm);
		req.addCommand(dcmd);
		try {
			SendHelper.sendRequest(req);
			infoMessage("提示","服务器已启动报表计算,稍候刷新查询计算进展情况");
			return;
		} catch (Exception e) {
			errorMessage("错误",e.getMessage());
		}
		
	}
	
	void setRecalc(){
		
		int rows[]=getTable().getSelectedRows();
		if(rows==null || rows.length==0){
			warnMessage("提示", "先选择要重新的记录");
			return;
		}
		for(int i=0;i<rows.length;i++){
			int row=rows[i];
			setItemValue(row, "usestatus", "0");
			setItemValue(row, "lastcalctime", "");
		}
		doSave();
	}

	@Override
	protected int on_new(int row) {
		Calendar now=Calendar.getInstance();
		int year=now.get(Calendar.YEAR);
		int month=now.get(Calendar.MONTH)+1;
		int day=now.get(Calendar.DAY_OF_MONTH);
		setItemValue(row, "year", String.valueOf(year));
		setItemValue(row, "month", String.valueOf(month));
		setItemValue(row, "day", String.valueOf(day));
		
		return super.on_new(row);
	}

	void batchGen(){
		
		int y=Calendar.getInstance().get(Calendar.YEAR);
		String year=JOptionPane.showInputDialog(getParentFrame(),"请输入四位的年份",String.valueOf(y));
		if(year==null)return;
		
		
		ClientRequest req=new ClientRequest("npbi:批量生成instance");
		ParamCommand pcmd=new ParamCommand();
		pcmd.addParam("year", year);
		req.addCommand(pcmd);
		try {
			SendHelper.sendRequest(req);
			infoMessage("提示","已生成,请重新查询");
			return;
		} catch (Exception e) {
			errorMessage("错误",e.getMessage());
		}
		
	}
}
