package com.inca.sysmgr.macreq;

import com.inca.np.gui.ste.*;
import java.awt.*;

/*功能"入网请求审批"Frame窗口*/
public class Macreq_frame extends Steframe{
	public Macreq_frame() throws HeadlessException {
		super("入网请求审批");
	}

	protected CSteModel getStemodel() {
		return new Macreq_ste(this);
	}

	public static void main(String[] argv){
		Macreq_frame w=new Macreq_frame();
		w.pack();
		w.setVisible(true);
	}
}
