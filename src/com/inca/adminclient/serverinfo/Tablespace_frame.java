package com.inca.adminclient.serverinfo;

import com.inca.np.gui.ste.*;
import com.inca.np.util.DefaultNPParam;

import java.awt.*;

import javax.swing.SwingUtilities;

/*����"ϵͳ��Ϣ��ѯ"Frame����*/
public class Tablespace_frame extends Steframe{
	public Tablespace_frame() throws HeadlessException {
		super("��ѯ��ռ�");
	}

	protected CSteModel getStemodel() {
		return new Tablespace_ste(this);
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
		Tablespace_frame w=new Tablespace_frame();
		w.pack();
		w.setVisible(true);
	}
}
