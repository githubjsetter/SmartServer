package com.inca.sysmgr.macreq;

import com.inca.np.gui.ste.*;
import java.awt.*;

/*����"������������"Frame����*/
public class Macreq_frame extends Steframe{
	public Macreq_frame() throws HeadlessException {
		super("������������");
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
