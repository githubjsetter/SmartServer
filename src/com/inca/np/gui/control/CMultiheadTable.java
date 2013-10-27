package com.inca.np.gui.control;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import com.inca.np.gui.ste.CTableMultiHeader;

public class CMultiheadTable extends CTable{

	public CMultiheadTable() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CMultiheadTable(TableModel dm, TableColumnModel cm) {
		super(dm, cm);
		// TODO Auto-generated constructor stub
	}

	public CMultiheadTable(TableModel dm) {
		super(dm);
		// TODO Auto-generated constructor stub
	}

	

	@Override
	protected JTableHeader createDefaultTableHeader() {
        return new CTableMultiHeader(columnModel);
	}

}
