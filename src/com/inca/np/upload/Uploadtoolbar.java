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
		addButton("上传更新","上传更新远程的服务器",Upload_ste.ACTION_UPLOAD);
	}

}
