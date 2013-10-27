package com.inca.npworkflow.client;

import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;

import com.inca.np.gui.control.CMultiHov;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.control.Sumdbmodel;
import com.inca.np.gui.ste.Querycond;
import com.inca.np.gui.ste.Querycondline;

/**
 * 结点决策数据HOV
 * 
 * @author user
 * 
 */
public class NodeinstData_hov extends CMultiHov {

	public NodeinstData_hov() {
		super();
		editable = true;

	}

	DBTableModel nodedatadm = null;

	public DBTableModel getNodedatadm() {
		return nodedatadm;
	}

	public void setNodedatadm(DBTableModel nodedatadm) {
		this.nodedatadm = nodedatadm;
	}

	@Override
	protected TableModel createTablemodel() {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col = new DBColumnDisplayInfo("dataitemname", "varchar", "数据项");
		col.setReadonly(true);
		cols.add(col);

		col = new DBColumnDisplayInfo("sortno", "number", "排序编号");
		col.setReadonly(false);
		cols.add(col);

		col = new DBColumnDisplayInfo("dataitemid", "number", "数据项ID");
		col.setReadonly(true);
		cols.add(col);

		DBTableModel dm = new DBTableModel(cols);
		return dm;
	}

	@Override
	public String getDefaultsql() {
		return "select dataitemid,dataitemname from np_wf_dataitem";
	}

	@Override
	public Querycond getQuerycond() {
		Querycond cond = new Querycond();
		// DBColumnDisplayInfo col=new
		// DBColumnDisplayInfo("dataitemid","number","数据项ID");
		// Querycondline ql=new Querycondline(cond,col);
		// cond.add(ql);
		return cond;
	}

	public String[] getColumns() {
		return new String[] { "dataitemid", "dataitemname" };
	}

	public String getDesc() {
		return "选择数据项";
	}

	@Override
	protected boolean autoSelect() {
		return true;
	}

	@Override
	protected void on_retrieved() {
		super.on_retrieved();
		dlgtable.getSelectionModel().clearSelection();
		DBTableModel dm = (DBTableModel) dlgtable.getModel();

		// 根据nodedatadm中的dataitemid进行设置
		for (int i = 0; nodedatadm != null && i < nodedatadm.getRowCount(); i++) {
			String dataitemid = nodedatadm.getItemValue(i, "dataitemid");
			for (int j = 0; j < dlgdbmodel.getRowCount(); j++) {
				if (dlgdbmodel.getItemValue(j, "dataitemid").equals(dataitemid)) {
					String sortno = nodedatadm.getItemValue(i, "sortno");
					// System.out.println("set j="+j+",sortno="+sortno);
					dm.setItemValue(j, "sortno", sortno);
				}
			}
		}
		try {
			tablemodel.sort("sortno:asc");
		} catch (Exception e) {
			e.printStackTrace();
		}
		dlgtable.tableChanged(new TableModelEvent(dm));

		for (int i = 0; nodedatadm != null && i < nodedatadm.getRowCount(); i++) {
			String dataitemid = nodedatadm.getItemValue(i, "dataitemid");
			for (int j = 0; j < dlgdbmodel.getRowCount(); j++) {
				if (dlgdbmodel.getItemValue(j, "dataitemid").equals(dataitemid)) {
					String sortno = nodedatadm.getItemValue(i, "sortno");
					// System.out.println("set j="+j+",sortno="+sortno);
					dlgtable.getSelectionModel().addSelectionInterval(j, j);
				}
			}
		}
	}

	@Override
	protected void onOk() {
		dlgtable.stopEdit();
		DBTableModel dm = (DBTableModel) dlgtable.getModel();
		for (int i = 0; i < dm.getRowCount(); i++) {
			if (dm.getItemValue(i, "sortno").length() > 0) {
				dlgtable.addRowSelectionInterval(i, i);
			}
		}
		super.onOk();
	}

}
