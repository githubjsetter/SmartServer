package com.smart.adminclient.fullscan;

import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.mde.MdeFrame;
import com.smart.platform.util.DefaultNPParam;

import java.awt.*;

/*����"��ѯfullscan"�ܵ�ϸĿFrame����*/
public class Fullscan_frame extends MdeFrame{
	public Fullscan_frame() throws HeadlessException {
		super("��ѯfullscan");
	}

	protected CMdeModel getMdeModel() {
		return new Fullscan_mde(this,"��ѯfullscan");
	}
	
	

	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		Fullscan_frame w=new Fullscan_frame();
		w.pack();
		w.setVisible(true);
	}
}
