package com.inca.npworkflow.client;

import java.awt.HeadlessException;
import java.util.Vector;

import javax.swing.table.TableModel;

import com.inca.np.gui.control.CMultiHov;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.Querycond;
import com.inca.np.gui.ste.Querycondline;

/**
 * 数据项HOV
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

/*		colinfo = new DBColumnDisplayInfo("opcode", "varchar", "操作码", false);
		colinfo.setUppercase(true);
		querycond.add(new Querycondline(querycond, colinfo));

*/
		return querycond;
	}

	protected TableModel createTablemodel() {
		Vector<DBColumnDisplayInfo> infos = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo colinfo = null;

		colinfo = new DBColumnDisplayInfo("dataitemname", "varchar", "数据项",
				false);
		infos.add(colinfo);

		//colinfo = new DBColumnDisplayInfo("dataitemid", "number", "数据项ID", false);
		//infos.add(colinfo);

		return new DBTableModel(infos);
	}

	public String getDesc() {
		return "选择数据项(多选)";
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
