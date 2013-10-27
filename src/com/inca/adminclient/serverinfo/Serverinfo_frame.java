package com.inca.adminclient.serverinfo;

import com.inca.np.gui.ste.*;
import com.inca.np.util.DefaultNPParam;

import java.awt.*;

import javax.swing.SwingUtilities;

/*����"ϵͳ��Ϣ��ѯ"Frame����*/
public class Serverinfo_frame extends Steframe{
	public Serverinfo_frame() throws HeadlessException {
		super("ϵͳ��Ϣ��ѯ");
	}

	protected CSteModel getStemodel() {
		return new Serverinfo_ste(this);
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		Runnable r=new Runnable(){
			public void run(){
				stemodel.doQuery();
			}
		};
		SwingUtilities.invokeLater(r);
	}



	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		Serverinfo_frame w=new Serverinfo_frame();
		w.pack();
		w.setVisible(true);
	}
}
