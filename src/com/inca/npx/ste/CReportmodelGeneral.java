package com.inca.npx.ste;

import java.awt.HeadlessException;
import java.io.File;

import javax.swing.JPopupMenu;

import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.CStequeryToolbar;
import com.inca.np.gui.control.CStetoolbar;
import com.inca.np.gui.control.CTable;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.SteControlFactory;

/**
 * 专项报表cstemodel
 * @author Administrator
 *
 */
public class CReportmodelGeneral extends CSteModelGeneral{

	public CReportmodelGeneral(CFrame frame, String title, String opid,
			String viewname, File zxzipfile) throws HeadlessException {
		super(frame, title, opid, viewname, zxzipfile);
	}

	@Override
	protected CTable recreateTable(DBTableModel dbmodel) {
		CTable table=super.recreateTable(dbmodel);
		if(table!=null){
			table.setReadonly(true);
		}
		return table;
	}

	@Override
	public String getSaveCommandString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected JPopupMenu createPopmenu() {
        return SteControlFactory.createQueryPopupmenu(this);
    }


	@Override
    protected CStetoolbar createToolbar() {
        return new CStequeryToolbar(this);
    }


	@Override
    protected int on_beforeNew() {
        return -1;
    }


	@Override
    protected int on_beforemodify(int row) {
        return -1;
    }

	@Override
    protected int on_beforedel(int row) {
        return -1;
    }

	@Override
    public int on_beforesave() {
        return -1;
    }
}
