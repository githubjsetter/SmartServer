package com.inca.np.upload;

import com.inca.np.filesync.UploaderFrame;
import com.inca.np.gui.ste.CQueryStemodel;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.CStetoolbar;
import com.inca.np.gui.control.DBColumnDisplayInfo;
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
