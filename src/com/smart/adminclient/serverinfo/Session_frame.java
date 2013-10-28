package com.smart.adminclient.serverinfo;

import com.smart.platform.gui.ste.*;
import com.smart.platform.util.DefaultNPParam;

import java.awt.*;

import javax.swing.SwingUtilities;

/*����"ϵͳ��Ϣ��ѯ"Frame����*/
public class Session_frame extends Steframe{
	public Session_frame() throws HeadlessException {
		super("����������");
	}

	protected CSteModel getStemodel() {
		return new Session_ste(this);
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
		Session_frame w=new Session_frame();
		w.pack();
		w.setVisible(true);
	}
}
