package com.smart.bi.client.design;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.event.InputEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.event.TableModelEvent;

import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.gui.control.CFormatTextField;
import com.smart.platform.gui.control.CTable;
import com.smart.platform.gui.control.DBTableModel;

public class Tablev_transferhandle extends TransferHandler {
	Tablevdesignpane frm = null;

	public Tablev_transferhandle(Tablevdesignpane frm) {
		super();
		this.frm = frm;
	}

	@Override
	public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
		DataFlavor dataflavor = transferFlavors[0];
		Class clazz = dataflavor.getRepresentationClass();
		System.out.println("can import=" + clazz);
		if (clazz.equals(ColumndragTable.class)) {
			return true;
		} else if (clazz.equals(String.class)) {
			// 字符串,移动单元格
			return true;
		}
		return true;
	}

	@Override
	public boolean importData(JComponent comp, Transferable t) {
		DataFlavor dataflavor = t.getTransferDataFlavors()[0];
		Object tranobj = null;
		try {
			tranobj = t.getTransferData(dataflavor);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		if (dataflavor.getMimeType().equals(
				"application/x-java-serialized-object; class=java.lang.String")) {
			// String,说明是单元格移动.
			String transfervalue = (String) tranobj;

			Tabledesign_table table = (Tabledesign_table) frm.getTable();
			int newr = table.getRow();
			int newc = table.getCurcol(); //
			DBTableModel dm = (DBTableModel) table.getModel();
			// System.out.println("newr=" + newr + ",newc=" + newc);
			if (table.getCellEditor() != null) {
				int mindex = table.convertColumnIndexToModel(newc);
				JComponent editcomp = dm.getDisplaycolumninfos().elementAt(
						mindex).getEditComponent();
				if (editcomp instanceof CFormatTextField) {
					((CFormatTextField) editcomp).setText(transfervalue);
				}
			} else {
				dm.setItemValue(newr, newc, transfervalue);
				table.tableChanged(new TableModelEvent(dm, newr));
			}
			dragstartrow=dragstartcol=-1;
			return true;
		}

		if (tranobj instanceof Vector) {

			Vector<RecordTrunk> dm = (Vector<RecordTrunk>) tranobj;
			frm.newColumn(dm);
			dragstartrow=dragstartcol=-1;
			return true;
		} else {
			return super.importData(comp, t);
		}
	}

	@Override
	protected Transferable createTransferable(JComponent c) {
		if (c instanceof CFormatTextField) {
			CFormatTextField tf = (CFormatTextField) c;
			Tabledesign_table table = (Tabledesign_table) frm.getTable();
			dragstartrow = table.getRow();
			dragstartcol = table.getCurcol();
			System.out.println("dragstartrow=" + dragstartrow
					+ ",dragstartcol=" + dragstartcol);
			String text = tf.getText();
			table.stopEdit();
			return new StringSelection(text);
		} else {
			Tabledesign_table table = (Tabledesign_table) frm.getTable();
			dragstartrow = table.getRow();
			dragstartcol = table.getCurcol();
			System.out.println("dragstartrow=" + dragstartrow
					+ ",dragstartcol=" + dragstartcol);
			DBTableModel dm = (DBTableModel) table.getModel();
			String text = dm.getItemValue(dragstartrow, dragstartcol);
			return new StringSelection(text);
		}

	}

	int dragstartrow;
	int dragstartcol;

	@Override
	public int getSourceActions(JComponent c) {
		if (c instanceof CFormatTextField) {
			return DnDConstants.ACTION_COPY;
		}
		return DnDConstants.ACTION_NONE;
	}

}
