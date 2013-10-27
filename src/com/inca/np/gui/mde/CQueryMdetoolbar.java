package com.inca.np.gui.mde;

import java.awt.event.ActionListener;

import com.inca.np.gui.control.CMdetoolbar;

public class CQueryMdetoolbar extends CMdetoolbar {

	public CQueryMdetoolbar(ActionListener l) {
		super(l);
	}

	static String nouse[] = { CMdeModel.ACTION_NEW, CMdeModel.ACTION_MODIFY,
			CMdeModel.ACTION_UNDO, CMdeModel.ACTION_DEL,
			CMdeModel.ACTION_NEWDTL, CMdeModel.ACTION_MODIFYDTL,
			CMdeModel.ACTION_UNDODTL, CMdeModel.ACTION_DELDTL,
			CMdeModel.ACTION_SAVE, };

	@Override
	protected boolean isUsebutton(String actionname) {
		for (int i = 0; i < nouse.length; i++) {
			if (actionname.equals(nouse[i])) {
				return false;
			}
		}
		return super.isUsebutton(actionname);
	}

}
