package com.inca.np.logger;

import com.inca.np.gui.mde.CQueryMdeFrame;
import com.inca.np.gui.mde.MdeFrame;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.util.DefaultNPParam;

import java.awt.*;

/*功能"查询服务器错误日志"总单细目Frame窗口*/
public class Logger_frame extends CQueryMdeFrame{
	public Logger_frame() throws HeadlessException {
		super("查询服务器错误日志");
	}

	protected CMdeModel getMdeModel() {
		return new Logger_mde(this,"查询服务器错误日志");
	}

	public static void main(String[] argv){
		new DefaultNPParam();
		Logger_frame w=new Logger_frame();
		w.pack();
		w.setVisible(true);
	}
}
