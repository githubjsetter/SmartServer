package com.inca.np.upload;

import com.inca.np.gui.ste.*;
import com.inca.np.util.DefaultNPParam;

import java.awt.*;

/*����"�ϴ���־����"Frame����*/
public class Upload_frame extends Steframe{
	public Upload_frame() throws HeadlessException {
		super("�ϴ���־����");
	}

	protected CSteModel getStemodel() {
		return new Upload_ste(this);
	}

	public static void main(String[] argv){
		new DefaultNPParam();
		Upload_frame w=new Upload_frame();
		w.pack();
		w.setVisible(true);
	}
}
