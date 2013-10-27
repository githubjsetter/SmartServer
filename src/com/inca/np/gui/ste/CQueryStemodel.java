package com.inca.np.gui.ste;

import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.CStetoolbar;
import com.inca.np.gui.control.CStequeryToolbar;
import com.inca.np.gui.control.CTable;
import com.inca.np.gui.control.DBTableModel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-8-9
 * Time: 11:35:29
 * ½ö²éÑ¯µÄstemodel
 */
public abstract class CQueryStemodel extends CSteModel{

    public CQueryStemodel(CFrame frame, String title) throws HeadlessException {
        super(frame, title);
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
