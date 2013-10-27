package com.inca.npbi.client.design;

import java.awt.dnd.DnDConstants;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;

import com.inca.np.gui.control.CTable;

public class ColumndragTable extends CTable {
	DscolumnTabletransferHandler tth = null;

	public ColumndragTable(TableModel dm) {
		super(dm);
		setDragEnabled(true);
		tth = new DscolumnTabletransferHandler(this);
		setTransferHandler(tth);
		addMouseMotionListener(new ML());
		getSelectionModel().setSelectionMode(
				ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setReadonly(true);
	}

	class ML implements MouseMotionListener {

		public void mouseDragged(MouseEvent e) {
			ColumndragTable.this.getTransferHandler().exportAsDrag(
					ColumndragTable.this, e, DnDConstants.ACTION_MOVE);
			e.consume();

		}

		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}
}
