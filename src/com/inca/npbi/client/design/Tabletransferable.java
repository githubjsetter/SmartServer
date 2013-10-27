package com.inca.npbi.client.design;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Vector;

import com.inca.np.communicate.RecordTrunk;
import com.inca.np.gui.control.CTable;
import com.inca.np.gui.control.DBTableModel;

public class Tabletransferable implements Transferable{
	public static DataFlavor DATAFLAVOR_BIDATACOLUMN=new DataFlavor(ColumndragTable.class,"����Դ��");
	DataFlavor[] flavors=null;
	
	CTable table=null;
	
	public Tabletransferable(DataFlavor[] flavors,CTable table) {
		super();
		this.flavors = flavors;
		this.table=table;
	}

	/**
	 * ����ѡ���е�RecordTrunk����
	 */
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		//����ѡ�е���
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
