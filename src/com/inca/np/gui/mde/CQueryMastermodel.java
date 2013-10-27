package com.inca.np.gui.mde;

import java.awt.HeadlessException;

import javax.swing.JPopupMenu;

import com.inca.np.gui.control.CFormFocusTraversalPolicy;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.CSteFormWindow;
import com.inca.np.gui.control.CStetoolbar;

/**
 * 只查询的总单
 * @author Administrator
 *
 */
public abstract class CQueryMastermodel extends CMasterModel{
	public CQueryMastermodel(CFrame frame, String title, CMdeModel mdemodel)
			throws HeadlessException {
		super(frame, title, mdemodel);
	}

	
	
/*	@Override
	protected CSteFormWindow createFormwindow() {
        CQueryMasterFormWindow formwindow = new CQueryMasterFormWindow(this.getParentFrame(),form,mdemodel,title);
        formwindow.setFocusTraversalPolicy(new CFormFocusTraversalPolicy(mdemodel.getMasterModel()));
        return formwindow;
	}
*/


	@Override
	protected JPopupMenu createPopmenu() {
        return MdeControlFactory.createQueryPopupmenu(mdemodel);
	}

	@Override
	protected CStetoolbar createToolbar() {
		return new CQueryMdetoolbar(this);
	}

	@Override
	public String getTablename() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSaveCommandString() {
		// TODO Auto-generated method stub
		return null;
	}

	

}
