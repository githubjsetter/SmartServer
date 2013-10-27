package com.inca.npbi.client.ds;

import java.awt.HeadlessException;
import java.util.Vector;

import com.inca.np.communicate.RecordTrunk;
import com.inca.np.gui.control.CStehovEx;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.Querycond;
import com.inca.np.gui.ste.Querycondline;

public class DS_stehov extends CStehovEx{

	public DS_stehov() throws HeadlessException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected CSteModel createStemodel() {
		Ds_frame frm=new Ds_frame();
		frm.pack();
		Ds_ste ste=(Ds_ste) frm.getCreatedStemodel();
		return ste;
	}

	@Override
	protected boolean autoReturn() {
		return false;
	}

	@Override
	protected boolean autoSelect() {
		return true;
	}

	@Override
	public Querycond getQuerycond() {
		Querycond cond=new Querycond();
		Querycondline ql=null;
		DBColumnDisplayInfo col;
		col=new DBColumnDisplayInfo("dsid","nubmer","数据源ID");
		ql=new Querycondline(cond,col);
		cond.add(ql);
		
		col=new DBColumnDisplayInfo("dsname","varchar","数据源名称");
		ql=new Querycondline(cond,col);
		cond.add(ql);
		
		return cond;
	}

	@Override
	protected void on_retrieved() {
		super.on_retrieved();
/*		//增加缺省的
		int ct=stemodel.getDBtableModel().getColumnCount();
		RecordTrunk rec=new RecordTrunk(ct);
		
		stemodel.getDBtableModel().getDataVector().insertElementAt(rec, 0);
		stemodel.setItemValue(0, "dsid", "0");
		stemodel.setItemValue(0, "dsname", "缺省数据源");
*/
	}
	

}
