package com.inca.np.logger;

import com.inca.np.gui.ste.*;
import java.awt.*;

/*����"������־��ѯ"Frame����*/
public class Visitlogger_frame extends Steframe{
	public Visitlogger_frame() throws HeadlessException {
		super("������־��ѯ");
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
