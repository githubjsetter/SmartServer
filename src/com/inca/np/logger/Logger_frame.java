package com.inca.np.logger;

import com.inca.np.gui.mde.CQueryMdeFrame;
import com.inca.np.gui.mde.MdeFrame;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.util.DefaultNPParam;

import java.awt.*;

/*����"��ѯ������������־"�ܵ�ϸĿFrame����*/
public class Logger_frame extends CQueryMdeFrame{
	public Logger_frame() throws HeadlessException {
		super("��ѯ������������־");
	}

	protected CMdeModel getMdeModel() {
		return new Logger_mde(this,"��ѯ������������־");
	}

	public static void main(String[] argv){
		new DefaultNPParam();
		Logger_frame w=new Logger_frame();
		w.pack();
		w.setVisible(true);
	}
}
