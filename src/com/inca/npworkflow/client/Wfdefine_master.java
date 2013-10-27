package com.inca.npworkflow.client;

import com.inca.np.demo.communicate.RemotesqlHelper;
import com.inca.np.gui.mde.CMasterModel;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.ste.Hovdefine;
import com.inca.np.gui.tbar.TBar;
import com.inca.np.gui.tbar.TButton;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;

import java.awt.*;

import javax.swing.JButton;
import javax.swing.JPanel;

/*����"���̶���"�ܵ�Model*/
public class Wfdefine_master extends CMasterModel {
	public Wfdefine_master(CFrame frame, CMdeModel mdemodel)
			throws HeadlessException {
		super(frame, "���̶���", mdemodel);
		
		DBColumnDisplayInfo col=this.getDBColumnDisplayInfo("tablename");
		Hovdefine hovdef=new Hovdefine("com.inca.npworkflow.client.SelecttableHov","tablename");
		hovdef.putColpair("tname","tablename");
		col.setHovdefine(hovdef);
		
		col=this.getDBColumnDisplayInfo("statuscolname");
		hovdef=new Hovdefine("com.inca.npworkflow.client.SelectaTablecolumnHov","statuscolname");
		hovdef.putColpair("cname","statuscolname");
		col.setHovdefine(hovdef);
		
		col=this.getDBColumnDisplayInfo("messagecolname");
		hovdef=new Hovdefine("com.inca.npworkflow.client.SelectaTablecolumnHov","messagecolname");
		hovdef.putColpair("cname","messagecolname");
		col.setHovdefine(hovdef);

		col=this.getDBColumnDisplayInfo("condexpr");
		hovdef=new Hovdefine("com.inca.npworkflow.client.Dataitem_hov","condexpr");
		hovdef.putColpair("dataitemname","condexpr");
		col.setHovdefine(hovdef);

		col=this.getDBColumnDisplayInfo("summaryexpr");
		hovdef=new Hovdefine("com.inca.npworkflow.client.Dataitem_hov","summaryexpr");
		hovdef.putColpair("dataitemname","summaryexpr");
		col.setHovdefine(hovdef);

		col=this.getDBColumnDisplayInfo("callopid");
		hovdef=new Hovdefine("com.inca.npworkflow.client.CallopHov","callopid");
		hovdef.putColpair("opid","callopid");
		col.setHovdefine(hovdef);

	}

	public String getTablename() {
		return "np_wf_define";
	}

	public String getSaveCommandString() {
		return null;
	}

	@Override
	protected void on_itemvaluechange(int row, String colname, String value) {
		if (colname.equalsIgnoreCase("tablename")) {
			// ��ѯ����
			try {
				fetchPkcolname(row, value);
			} catch (Exception e) {
				errorMessage("����", e.getMessage());
				return;
			}
		}
		super.on_itemvaluechange(row, colname, value);
	}

	@Override
	public int on_hov(int row, String colname, DBTableModel hovmodel) {
		if (colname.equalsIgnoreCase("tablename")) {
			String tablename = hovmodel.getItemValue(0, "tname");
			try {
				setItemValue(row, "viewname", tablename + "_V");	
				fetchPkcolname(row, tablename);
			} catch (Exception e) {
				errorMessage("����", e.getMessage());
				return 0;
			}
		}
		return super.on_hov(row, colname, hovmodel);
	}

	/**
	 * ��ѯ��������
	 * 
	 * @param tablename
	 */
	void fetchPkcolname(int row, String tablename) throws Exception {
		String pkcolname = getTablepkcol(tablename);
		dbmodel.setItemValue(row, "pkcolname", pkcolname);
	}

	String getTablepkcol(String tname) throws Exception {
		String sql = "select column_name from USER_CONS_COLUMNS where constraint_name in ( "
				+ " select constraint_name from user_constraints where table_name='"
				+ tname.toUpperCase() + "' and constraint_type='P')";

		RemotesqlHelper sqlh = new RemotesqlHelper();
		DBTableModel result = sqlh.doSelect(sql, 0, 1);
		if (result.getRowCount() == 0)
			return "";
		return result.getItemValue(0, "column_name");
	}

	@Override
	public String getHovOtherWheres(int row, String colname) {
		if(colname.equalsIgnoreCase("statuscolname") || colname.equalsIgnoreCase("messagecolname")){
			String tablename=getItemValue(row, "tablename");
			return "tname='"+tablename.toUpperCase()+"'";
		}
		return super.getHovOtherWheres(row, colname);
	}

	@Override
	protected JPanel createSecondtoolbar() {
		TBar tb=new TBar();
		TButton btn;
		btn=new TButton("����������");
		btn.addActionListener(mdemodel);
		btn.setActionCommand("����������");
		btn.setFocusable(false);
		tb.add(btn);
		
		btn=new TButton("����ɫ��Ȩ");
		btn.addActionListener(mdemodel);
		btn.setActionCommand("����ɫ��Ȩ");
		btn.setFocusable(false);
		tb.add(btn);

		btn=new TButton("�����Ա��Ȩ");
		btn.addActionListener(mdemodel);
		btn.setActionCommand("�����Ա��Ȩ");
		btn.setFocusable(false);
		tb.add(btn);

		btn=new TButton("�������������");
		btn.addActionListener(mdemodel);
		btn.setActionCommand("����������");
		btn.setFocusable(false);
		tb.add(btn);

		btn=new TButton("�������");
		btn.addActionListener(mdemodel);
		btn.setActionCommand("�������");
		btn.setFocusable(false);
		tb.add(btn);

		btn=new TButton("����������Ȩ");
		btn.addActionListener(mdemodel);
		btn.setActionCommand("����������Ȩ");
		btn.setFocusable(false);
		tb.add(btn);

		return tb;
	}

	@Override
	protected void invokeMultimdehov(int row, String colname, String value) {
		if(colname.equalsIgnoreCase("condexpr")){
			String wfid=getItemValue(row, "wfid");
			Dataitem_hov hov=new Dataitem_hov();
			hov.setWfid(wfid);
			DBTableModel result=hov.showDialog(mdemodel.getParentFrame(),"ѡ��������");
			if(result==null)return;
			int rows[]=hov.getDlgtable().getSelectedRows();
			DBTableModel dm=hov.getTablemodel();
			for(int i=0;i<rows.length;i++){
				int r=rows[i];
				String dataitemname=dm.getItemValue(r, "dataitemname");
				String s=getItemValue(row, "condexpr")+"{"+dataitemname+"}";
				setItemValue(row, "condexpr",s);
			}
			
		}else if(colname.equalsIgnoreCase("summaryexpr")){
			String wfid=getItemValue(row, "wfid");
			Dataitem_hov hov=new Dataitem_hov();
			hov.setWfid(wfid);
			DBTableModel result=hov.showDialog(mdemodel.getParentFrame(),"ѡ��������");
			if(result==null)return;
			int rows[]=hov.getDlgtable().getSelectedRows();
			DBTableModel dm=hov.getTablemodel();
			for(int i=0;i<rows.length;i++){
				int r=rows[i];
				String dataitemname=dm.getItemValue(r, "dataitemname");
				String s=getItemValue(row, "summaryexpr")+"{"+dataitemname+"}";
				setItemValue(row, "summaryexpr",s);
			}
			
		} else{
			super.invokeMultimdehov(row, colname, value);
		}
	}

	
}
