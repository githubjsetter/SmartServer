package com.smart.workflow.client;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.ste.Hovdefine;

import java.awt.*;

/*功能"流程定义"细单Model*/
public class Wfdefine_detail extends CDetailModel {
	public Wfdefine_detail(CFrame frame, CMdeModel mdemodel)
			throws HeadlessException {
		super(frame, "结点定义", mdemodel);
		DBColumnDisplayInfo col = getDBColumnDisplayInfo("passstatus");
		Hovdefine hovdef = new Hovdefine(
				"com.inca.npworkflow.client.Approvestatus_hov", "passstatus");
		hovdef.putColpair("statusid", "passstatus");
		col.setHovdefine(hovdef);

		col = getDBColumnDisplayInfo("refusestatus");
		hovdef = new Hovdefine("com.inca.npworkflow.client.Approvestatus_hov",
				"refusestatus");
		hovdef.putColpair("statusid", "refusestatus");
		col.setHovdefine(hovdef);

		col = this.getDBColumnDisplayInfo("entercond");
		hovdef = new Hovdefine("com.inca.npworkflow.client.Dataitem_hov",
				"entercond");
		hovdef.putColpair("dataitemname", "entercond");
		col.setHovdefine(hovdef);

		col = this.getDBColumnDisplayInfo("updatesql");
		hovdef = new Hovdefine("com.inca.npworkflow.client.Dataitem_hov",
				"updatesql");
		hovdef.putColpair("dataitemname", "updatesql");
		col.setHovdefine(hovdef);

	}

	public String getTablename() {
		return "np_wf_node";
	}

	public String getSaveCommandString() {
		return null;
	}

	@Override
	protected void invokeMultimdehov(int row, String colname, String value) {
		if (colname.equalsIgnoreCase("passstatus")
				|| colname.equalsIgnoreCase("refusestatus")) {
			Approvestatus_hov hov = new Approvestatus_hov();
			String wfid = getItemValue(row, "wfid");
			if (wfid == null || wfid.length() == 0) {
				warnMessage("提示", "请先保存流程定义再设置状态");
				return;
			}
			hov.setWfid(wfid);
			String curv = getItemValue(row, colname);
			hov.setCurrentv(curv);
			DBTableModel result = hov.showDialog(getParentFrame(), "选择审批状态");
			if (result == null)
				return;
			String statusid = result.getItemValue(0, "statusid");
			setItemValue(row, colname, statusid);

			// 一定要触发变化．
			on_itemvaluechange(row, colname, value);
		} else if (colname.equalsIgnoreCase("entercond")) {
			String wfid = getItemValue(row, "wfid");
			Dataitem_hov hov = new Dataitem_hov();
			hov.setWfid(wfid);
			DBTableModel result = hov.showDialog(mdemodel.getParentFrame(),
					"选择数据项");
			if (result == null)
				return;
			int rows[] = hov.getDlgtable().getSelectedRows();
			DBTableModel dm = hov.getTablemodel();
			for (int i = 0; i < rows.length; i++) {
				int r = rows[i];
				String dataitemname = dm.getItemValue(r, "dataitemname");
				String s = getItemValue(row, "entercond") + "{" + dataitemname
						+ "}";
				setItemValue(row, "entercond", s);
			}
		} else if (colname.equalsIgnoreCase("updatesql")) {
			String wfid = getItemValue(row, "wfid");
			Dataitem_hov hov = new Dataitem_hov();
			hov.setWfid(wfid);
			DBTableModel result = hov.showDialog(mdemodel.getParentFrame(),
					"选择数据项");
			if (result == null)
				return;
			int rows[] = hov.getDlgtable().getSelectedRows();
			DBTableModel dm = hov.getTablemodel();
			for (int i = 0; i < rows.length; i++) {
				int r = rows[i];
				String dataitemname = dm.getItemValue(r, "dataitemname");
				String s = getItemValue(row, "updatesql") + "{" + dataitemname
						+ "}";
				setItemValue(row, "updatesql", s);
			}

		}
	}

	@Override
	protected String getEditablecolumns(int row) {
		String actiontype=getItemValue(row, "actiontype");
		String basecol="nodename,stage,passstatus,refusestatus,actiontype,entercond";
		String basecolr="nodename,stage,passstatus,refusestatus,actiontype";
		if(actiontype.equals("human")){
			return basecol;
		}else if(actiontype.equals("updatesql") ){
			return basecol+",updatesql";
		}else if(actiontype.equals("refuseprior")){
			return basecol;
		}else if(actiontype.equals("refuseall")){
			return basecol;
		}else if(actiontype.equals("pass")){
			return basecol;
		}else if(actiontype.equals("java") ){
			return basecol+",classname";
		}else if(actiontype.equals("rupdatesql")){
			return basecolr+",updatesql";
		}else if(actiontype.equals("rjava")){
			return basecolr+",classname";
		}
		return super.getEditablecolumns(row);
	}

	@Override
	protected void on_itemvaluechange(int row, String colname, String value) {
		super.on_itemvaluechange(row, colname, value);
		if(colname.equalsIgnoreCase("actiontype")){
			super.bindDataSetEnable(row);
		}
	}
	
	

}
