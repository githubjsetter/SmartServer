package com.smart.adminclient.prodmanager;

import java.awt.HeadlessException;

import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.Steframe;

public class ProdmanagerFrame extends Steframe{
    public ProdmanagerFrame() throws HeadlessException {
        super("产品管理");  
    }

    protected CSteModel getStemodel() {
        return new ProdmanagerSte(this);
    }

    public static void main(String[] args) {
    	ProdmanagerFrame frm=new ProdmanagerFrame();
    	frm.pack();
    	frm.setVisible(true);
	}
}
