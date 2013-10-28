package com.smart.bi.client.design;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Vector;

import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.gui.control.CTable;
import com.smart.platform.gui.control.DBTableModel;

public class Tabletransferable implements Transferable{
	public static DataFlavor DATAFLAVOR_BIDATACOLUMN=new DataFlavor(ColumndragTable.class,"数据源列");
	DataFlavor[] flavors=null;
	
	CTable table=null;
	
	public Tabletransferable(DataFlavor[] flavors,CTable table) {
		super();
		this.flavors = flavors;
		this.table=table;
	}

	/**
	 * 返回选中行的RecordTrunk向量
	 */
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		//返回选中的行
		DBTableModel dm=(DBTableModel)table.getModel();
		Vector<RecordTrunk> newdm=new Vector<RecordTrunk>();
		int rows[]=table.getSelectedRows();
		for(int i=0;i<rows.length;i++){
			int row=rows[i];
			newdm.add(dm.getRecordThunk(row));
		}
		return newdm;
	}

	public DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		System.out.println("isDataFlavorSupported?");
		return false;
	}

}
