package com.smart.platform.upload;

import com.smart.platform.filesync.UploaderFrame;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CStetoolbar;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.ste.CQueryStemodel;
import com.smart.platform.gui.ste.CSteModel;

import java.awt.*;

/*功能"上传日志管理"单表编辑Model*/
public class Upload_ste extends CQueryStemodel{
	public static final String ACTION_UPLOAD="upload";
	public Upload_ste(CFrame frame) throws HeadlessException {
		super(frame, "上传日志");
	}


	public String getTablename() {
		return "np_upload_log";
	}

	public String getSaveCommandString() {
		return "保存上传更新日志";
	}

	@Override
	protected int on_actionPerformed(String command) {
		if(command.equals(ACTION_UPLOAD)){
			UploaderFrame frm = new UploaderFrame();
			frm.pack();
			frm.setVisible(true);
			return 0;
		}
		return super.on_actionPerformed(command);
	}
	
	
	@Override
	protected CStetoolbar createToolbar() {
		return new Uploadtoolbar(this);
	}
	
	
}
