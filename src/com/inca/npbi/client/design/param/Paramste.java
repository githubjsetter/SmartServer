package com.inca.npbi.client.design.param;

import java.awt.HeadlessException;
import java.util.Enumeration;

import com.inca.np.communicate.RecordTrunk;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.Hovdefine;
import com.inca.npbi.client.design.BIReportdsDefine;
import com.inca.npbi.client.design.ReportcanvasFrame;

public class Paramste extends CSteModel{
	ReportcanvasFrame canvasframe;


	public Paramste(CFrame frame, String title) throws HeadlessException {
		super(frame, title);
		DBColumnDisplayInfo col;
		col=dbmodel.getColumninfo("hovclass");
		Hovdefine hovdefine = new Hovdefine(
				"com.inca.np.gui.design.SelecthovHov", "hovclass");
		hovdefine.putColpair("classname", "hovclass");
		col.setHovdefine(hovdefine);
	}
	BIReportdsDefine dsdefine;
	
	public void setDsdefine(BIReportdsDefine dsdefine){
		this.dsdefine=dsdefine;
		dbmodel.clearAll();
		Enumeration<BIReportparamdefine>en=		dsdefine.params.elements();
		while(en.hasMoreElements()){
			BIReportparamdefine param=en.nextElement();
			int r=dbmodel.getRowCount();
			dbmodel.appendRow();
			dbmodel.setItemValue(r, "paramname", param.paramname);
			dbmodel.setItemValue(r, "paramtype", param.paramtype);
			dbmodel.setItemValue(r, "title", param.title);
			dbmodel.setItemValue(r, "initvalue", param.initvalue);
			dbmodel.setItemValue(r, "numberwidth", String.valueOf(param.numberwidth));
			dbmodel.setItemValue(r, "mustinput", param.mustinput?"1":"0");
			dbmodel.setItemValue(r, "autocond", param.autocond);
			dbmodel.setItemValue(r, "hovclass", param.hovclass);
			dbmodel.setItemValue(r, "hovcols", param.hovcols);
			dbmodel.setdbStatus(r, RecordTrunk.DBSTATUS_SAVED);
		}
		tableChanged();
		table.autoSize();
	}
	

	public void setCanvasframe(ReportcanvasFrame canvasframe) {
		this.canvasframe = canvasframe;
	}


	@Override
	public void doDel() {
		int r=getRow();
		if(r<0)return;
		//设成新增状态,这样一定会被删
		dbmodel.setdbStatus(r, RecordTrunk.DBSTATUS_NEW);
		super.doDel();
	}

	@Override
	public int doSave() {
		table.stopEdit();
		doHideform();
		if(dsdefine==null){
			doExit();
			return 0;
		}
		dsdefine.params.clear();
		for(int i=0;i<dbmodel.getRowCount();i++){
			BIReportparamdefine param=new BIReportparamdefine();
			dsdefine.params.add(param);
			param.paramname=dbmodel.getItemValue(i, "paramname");
			param.paramtype=dbmodel.getItemValue(i, "paramtype");
			param.title=dbmodel.getItemValue(i, "title");
			param.initvalue=dbmodel.getItemValue(i, "initvalue");
			try{
			param.numberwidth=Integer.parseInt(dbmodel.getItemValue(i, "numberwidth"));
			}catch(Exception e){}
			param.mustinput=dbmodel.getItemValue(i, "mustinput").equals("1");
			param.autocond=dbmodel.getItemValue(i, "autocond");
			param.hovclass=dbmodel.getItemValue(i, "hovclass");
			param.hovcols=dbmodel.getItemValue(i, "hovcols");
			dbmodel.setdbStatus(i, RecordTrunk.DBSTATUS_SAVED);
		}
		canvasframe.onParamsdefchanged();
		doExit();
		return 0;
	}

	
	
/*	
	static Vector<DBColumnDisplayInfo> createCols(){
		Vector<DBColumnDisplayInfo> cols=new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col=new DBColumnDisplayInfo("行号","行号","行号");
		cols.add(col);
		
		col=new DBColumnDisplayInfo("paramname","varchar","参数名称");
		cols.add(col);
		
		col=new DBColumnDisplayInfo("paramtype","varchar","参数类型");
		col.addComboxBoxItem("number", "数字");
		col.addComboxBoxItem("varchar", "字符串");
		col.addComboxBoxItem("datetime", "日期时间");
		cols.add(col);
		
		
		col=new DBColumnDisplayInfo("title","varchar","中文名称");
		cols.add(col);

		col=new DBColumnDisplayInfo("mustinput","varchar","必填");
		col.setEditcomptype(DBColumnDisplayInfo.EDITCOMP_CHECKBOX);
		cols.add(col);

		col=new DBColumnDisplayInfo("hovclass","varchar","使用的HOV");
		cols.add(col);

		col=new DBColumnDisplayInfo("hovcolumn","varchar","HOV列");
		cols.add(col);

		
		return cols;
	}

*/



	@Override
	public String getTablename() {
		return "";
	}

	@Override
	public String getSaveCommandString() {
		return "";
	}

	@Override
	protected int on_actionPerformed(String command) {
		if("设置自动条件".equals(command)){
			setAutocond();
		}
		return super.on_actionPerformed(command);
	}
	

	void setAutocond(){
		int r=getRow();
		if(r<0){
			warnMessage("提示","选新增并选中一个参数再设置自动条件");
			return;
		}
		
		String paramname=getItemValue(r, "paramname");
		if(paramname.length()==0){
			warnMessage("提示","请先输入参数名称");
			return;
		}
		String paramtype=getItemValue(r, "paramtype");
		String cond="";
		if(paramtype.equals("varchar")){
			cond=paramname+"={"+paramname+"}";
		}else if(paramtype.equals("date")){
			cond=paramname+" between to_date('{"+paramname+"} 00:00:00','yyyy-mm-dd hh24:mi:ss')";
			cond+=" and to_date('{"+paramname+"} 23:59:59','yyyy-mm-dd hh24:mi:ss')";
		}else{
			cond=paramname+"={"+paramname+"}";
		}
		setItemValue(r, "autocond",cond);
		bindDataSetEnable(r);
		
	}


	@Override
	public int on_hov(int row, String colname, DBTableModel hovmodel) {
		if(colname.equalsIgnoreCase("hovclass")){
			String classname=hovmodel.getItemValue(0, "classname");
			if(classname.equals("hovgeneral")){
				String hovname=hovmodel.getItemValue(0,"hovname");
				super.on_hov(row, colname, hovmodel);
				setItemValue(row, "hovclass",classname+"_"+hovname);
				return 0;
			}
		}
		return super.on_hov(row, colname, hovmodel);
	}
	
	
}
