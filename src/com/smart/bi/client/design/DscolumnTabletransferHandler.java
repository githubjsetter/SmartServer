package com.smart.bi.client.design;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import com.smart.platform.gui.control.DBTableModel;

/**
 * BI 呈现数据源列的ctable的transferhandle
 * 
 * @author user
 * 
 */
public class DscolumnTabletransferHandler extends TransferHandler {
	ColumndragTable table = null;

	public DscolumnTabletransferHandler(ColumndragTable table) {
		super();
		this.table = table;
	}

	@Override
	public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
		System.out.println("can import ,comp=" + comp.getClass().getName());
		if (comp instanceof Tabledesign_table) {
			return true;
		}
		return false;
	}

	@Override
	protected Transferable createTransferable(JComponent c) {
		DataFlavor[] flavors = new DataFlavor[] {
				Tabletransferable.DATAFLAVOR_BIDATACOLUMN,
				DataFlavor.stringFlavor};
		return new Tabletransferable(flavors, table);
	}

	/**
	 * @Override protected void exportDone(JComponent source, Transferable data,
	 *           int action) { System.out.println("export done");
	 *           super.exportDone(source, data, action); }
	 */
	@Override
	public int getSourceActions(JComponent c) {
		return TransferHandler.COPY_OR_MOVE;
	}

	/**
	 * @Override public boolean importData(JComponent comp, Transferable t) {
	 *           System.out.println("importData"); //super.importData(comp, t)
	 *           return true; }
	 */
}
