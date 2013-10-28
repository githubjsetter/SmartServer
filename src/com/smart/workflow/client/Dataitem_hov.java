package com.smart.workflow.client;

import java.awt.HeadlessException;
import java.util.Vector;

import javax.swing.table.TableModel;

import com.smart.platform.gui.control.CMultiHov;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.Querycond;
import com.smart.platform.gui.ste.Querycondline;

/**
 * ������HOV
 * 
 * @author user
 * 
 */
public class Dataitem_hov extends CMultiHov {
	String wfid="";
	public Dataitem_hov() throws HeadlessException {
		super();
	}

	public String getDefaultsql() {
		return "select dataitemid,wfid,dataitemname from np_wf_dataitem" +
				" where wfid="+wfid;
	}

	public Querycond getQuerycond() {
		Querycond querycond = new Querycond();

		DBColumnDisplayInfo colinfo = null;

/*		colinfo = new DBColumnDisplayInfo("opcode", "varchar", "������", false);
		colinfo.setUppercase(true);
		querycond.add(new Querycondline(querycond, colinfo));

*/
		return querycond;
	}

	protected TableModel createTablemodel() {
		Vector<DBColumnDisplayInfo> infos = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo colinfo = null;

		colinfo = new DBColumnDisplayInfo("dataitemname", "varchar", "������",
				false);
		infos.add(colinfo);

		//colinfo = new DBColumnDisplayInfo("dataitemid", "number", "������ID", false);
		//infos.add(colinfo);

		return new DBTableModel(infos);
	}

	public String getDesc() {
		return "ѡ��������(��ѡ)";
	}

	public String[] getColumns() {
		return new String[] { "dataitemname" };
	}

	public String getWfid() {
		return wfid;
	}

	public void setWfid(String wfid) {
		this.wfid = wfid;
	}

	@Override
	protected boolean autoReturn() {
		return false;
	}

	@Override
	protected boolean autoSelect() {
		return true;
	}

	
}
