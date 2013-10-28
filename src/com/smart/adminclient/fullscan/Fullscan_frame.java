package com.smart.adminclient.fullscan;

import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.mde.MdeFrame;
import com.smart.platform.util.DefaultNPParam;

import java.awt.*;

/*功能"查询fullscan"总单细目Frame窗口*/
public class Fullscan_frame extends MdeFrame{
	public Fullscan_frame() throws HeadlessException {
		super("查询fullscan");
	}

	protected CMdeModel getMdeModel() {
		return new Fullscan_mde(this,"查询fullscan");
	}
	
	

	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		Fullscan_frame w=new Fullscan_frame();
		w.pack();
		w.setVisible(true);
	}
}
