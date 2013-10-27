package com.inca.adminclient.serverinfo;

import com.inca.np.gui.ste.*;
import com.inca.np.util.DefaultNPParam;

import java.awt.*;

import javax.swing.SwingUtilities;

/*����"ϵͳ��Ϣ��ѯ"Frame����*/
public class Sessionlock_frame extends Steframe{
	public Sessionlock_frame() throws HeadlessException {
		super("ϵͳ����");
	}

	protected CSteModel getStemodel() {
		return new Sessionlock_ste(this);
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
		Sessionlock_frame w=new Sessionlock_frame();
		w.pack();
		w.setVisible(true);
	}
}
