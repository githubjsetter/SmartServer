package com.inca.adminclient.modulemgr;

import java.awt.HeadlessException;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.Steframe;

public class ModulemgrFrame extends Steframe{
    public ModulemgrFrame() throws HeadlessException {
        super("��Ʒģ�����");  
    }

    protected CSteModel getStemodel() {
        return new ModulemgrSte(this);
    }

    public static void main(String[] args) {
		ModulemgrFrame frm=new ModulemgrFrame();
    	frm.pack();
    	frm.setVisible(true);
	}
}
