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

/*功能"流程定义"总单Model*/
public class Wfdefine_master extends CMasterModel {
	public Wfdefine_master(CFrame frame, CMdeModel mdemodel)
			throws HeadlessException {
		super(frame, "流程定义", mdemodel);
		
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
			// 查询主键
			try {
				fetchPkcolname(row, value);
			} catch (Exception e) {
				errorMessage("错误", e.getMessage());
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
				errorMessage("错误", e.getMessage());
				return 0;
			}
		}
		return super.on_hov(row, colname, hovmodel);
	}

	/**
	 * 查询主键列名
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
		btn=new TButton("定义数据项");
		btn.addActionListener(mdemodel);
		btn.setActionCommand("定义数据项");
		btn.setFocusable(false);
		tb.add(btn);
		
		btn=new TButton("结点角色授权");
		btn.addActionListener(mdemodel);
		btn.setActionCommand("结点角色授权");
		btn.setFocusable(false);
		tb.add(btn);

		btn=new TButton("结点人员授权");
		btn.addActionListener(mdemodel);
		btn.setActionCommand("结点人员授权");
		btn.setFocusable(false);
		tb.add(btn);

		btn=new TButton("定义结点决策数据");
		btn.addActionListener(mdemodel);
		btn.setActionCommand("结点决策数据");
		btn.setFocusable(false);
		tb.add(btn);

		btn=new TButton("检查流程");
		btn.addActionListener(mdemodel);
		btn.setActionCommand("检查流程");
		btn.setFocusable(false);
		tb.add(btn);

		btn=new TButton("设置数据授权");
		btn.addActionListener(mdemodel);
		btn.setActionCommand("设置数据授权");
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
			DBTableModel result=hov.showDialog(mdemodel.getParentFrame(),"选择数据项");
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
			DBTableModel result=hov.showDialog(mdemodel.getParentFrame(),"选择数据项");
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
