package com.inca.npbi.client.view;

import com.inca.np.anyprint.SelectcolumnHov;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.Hovdefine;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.CTextArea;
import com.inca.np.gui.control.CTextField;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.npbi.client.ds.DS_stehov;

import java.awt.*;

/*����"��ͼ����"����༭Model*/
public class View_ste extends CSteModel {
	String reportid = "";

	public View_ste(CFrame frame) throws HeadlessException {
		super(frame, "������ͼ");
		DBColumnDisplayInfo col = getDBColumnDisplayInfo("helpmsg");
		if (col == null) {
			col = new DBColumnDisplayInfo("helpmsg", "varchar", "������Ϣ");
			formcolumndisplayinfos.add(col);
		}
		col.setDbcolumn(false);

		Hovdefine hovdefine = new Hovdefine(
				"com.inca.npbi.client.ds.DS_stehov", "dsid");
		hovdefine.putColpair("dsid", "dsid");
		hovdefine.putColpair("dsname", "dsname");
		col = getDBColumnDisplayInfo("dsid");
		col.setHovdefine(hovdefine);
		
		hovdefine = new Hovdefine(
				"com.inca.np.anyprint.SelectcolumnHov", "sql");
		hovdefine.putColpair("cname", "sql");
		col = getDBColumnDisplayInfo("sql");
		col.setHovdefine(hovdefine);
		
	}

	public String getTablename() {
		return "npbi_view_v";
	}

	public String getSaveCommandString() {
		return "View_ste.������ͼ";
	}

	public String getReportid() {
		return reportid;
	}

	public void setReportid(String reportid) {
		this.reportid = reportid;
	}

	String helpmsg = "ʱ��ά�Ȳ���:\n" + "{ʱ��ά��.��}\n" + "{ʱ��ά��.��}\n" + "{ʱ��ά��.��}\n"
			+ "{ʱ��ά��.������}\n" + "{ʱ��ά��.������}\n" + "{ʱ��ά��.������}\n"
			+ "{ʱ��ά��.��ʼ����}\n" + "{ʱ��ά��.��������}\n"+
			"{ʱ��ά��.����.��}\n{ʱ��ά��.����.��}\n";
			

	@Override
	protected int on_new(int row) {
		if (super.on_new(row) != 0)
			return -1;
		setItemValue(row, "reportid", reportid);
		setItemValue(row, "helpmsg", helpmsg);
		return 0;
	}

	@Override
	protected int on_beforemodify(int row) {
		getDBtableModel().setItemValue(row, "helpmsg", helpmsg);
		return super.on_beforemodify(row);
	}

	@Override
	protected void invokeMultimdehov(int row, String colname, String value) {
		if(colname.equalsIgnoreCase("dsid")){
			DS_stehov hov=new DS_stehov();
			DBTableModel result=hov.showDialog(getParentFrame(), "ѡ������Դ");
			if(result==null)return;
			setItemValue(row, "dsid", result.getItemValue(0, "dsid"));
			setItemValue(row, "dsname", result.getItemValue(0, "dsname"));
		}else if(colname.equalsIgnoreCase("sql")){
			SelectcolumnHov hov=new SelectcolumnHov();
			DBTableModel result=hov.showDialog(getParentFrame(),"ѡ�����");
			if(result==null)return;
			String colstring=hov.getTablecolumns();
			DBColumnDisplayInfo col = getDBColumnDisplayInfo("sql");
			CTextArea textcomp=(CTextArea) col.getEditComponent();
			textcomp.getTextarea().replaceSelection(colstring);
			
		}else{
			super.invokeMultimdehov(row, colname, value);
		}
	}

}
