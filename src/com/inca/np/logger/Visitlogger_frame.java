package com.inca.np.logger;

import com.inca.np.gui.ste.*;
import java.awt.*;

/*功能"访问日志查询"Frame窗口*/
public class Visitlogger_frame extends Steframe{
	public Visitlogger_frame() throws HeadlessException {
		super("访问日志查询");
	}

	protected CSteModel getStemodel() {
		return new Visitlogger_ste(this);
	}

	public static void main(String[] argv){
		Visitlogger_frame w=new Visitlogger_frame();
		w.pack();
		w.setVisible(true);
	}
}
