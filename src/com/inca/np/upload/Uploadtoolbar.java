package com.inca.np.upload;

import java.awt.event.ActionListener;

import com.inca.np.gui.control.CStequeryToolbar;
import com.inca.np.gui.control.CStetoolbar;

public class Uploadtoolbar extends CStequeryToolbar{

	public Uploadtoolbar(ActionListener l) {
		super(l);
	}

	@Override
	protected void createOtherButton(ActionListener listener) {
		super.createOtherButton(listener);
		addButton("�ϴ�����","�ϴ�����Զ�̵ķ�����",Upload_ste.ACTION_UPLOAD);
	}

}
