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

/*����"����ʵ������"����༭Model*/
public class Instance_ste extends CSteModel {

	public Instance_ste(CFrame frame) throws HeadlessException {
		super(frame, "����ʵ��");
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
		return "Instance_ste.����ʵ��";
	}

	@Override
	protected int on_actionPerformed(String command) {
		if (command.equals("��������")) {
			calcInstance();
			return 0;
		} else if (command.equals("��������")) {
			setRecalc();
			return 0;
		} else if (command.equals("Ԥ������")) {
			preview();
			return 0;
		} else if (command.equals("��������")) {
			batchGen();
			return 0;
		} else {
			return super.on_actionPerformed(command);
		}
	}
	
	 void preview() {
		 int row=getRow();
		 if(row<0){
			 warnMessage("��ʾ", "���ѯѡ��һ������ʵ��");
			 return;
		 }
		 String instanceid=getItemValue(row, "instanceid");
		 String reportname=getItemValue(row, "reportname");
		 Reportpreview_frame frm=new Reportpreview_frame(reportname+"Ԥ��",instanceid);
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
			warnMessage("��ʾ", "��ѡ��Ҫ���µļ�¼");
			return;
		}
		for(int i=0;i<rows.length;i++){
			int row=rows[i];
			int newrow=dm.getRowCount();
			dm.appendRow();
			dm.setItemValue(newrow, "instanceid", dbmodel.getItemValue(row, "instanceid"));
		}
		ClientRequest req=new ClientRequest("npbi.���㱨��ʵ��");
		DataCommand dcmd=new DataCommand();
		dcmd.setDbmodel(dm);
		req.addCommand(dcmd);
		try {
			SendHelper.sendRequest(req);
			infoMessage("��ʾ","�������������������,�Ժ�ˢ�²�ѯ�����չ���");
			return;
		} catch (Exception e) {
			errorMessage("����",e.getMessage());
		}
		
	}
	
	void setRecalc(){
		
		int rows[]=getTable().getSelectedRows();
		if(rows==null || rows.length==0){
			warnMessage("��ʾ", "��ѡ��Ҫ���µļ�¼");
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
		String year=JOptionPane.showInputDialog(getParentFrame(),"��������λ�����",String.valueOf(y));
		if(year==null)return;
		
		
		ClientRequest req=new ClientRequest("npbi:��������instance");
		ParamCommand pcmd=new ParamCommand();
		pcmd.addParam("year", year);
		req.addCommand(pcmd);
		try {
			SendHelper.sendRequest(req);
			infoMessage("��ʾ","������,�����²�ѯ");
			return;
		} catch (Exception e) {
			errorMessage("����",e.getMessage());
		}
		
	}
}
