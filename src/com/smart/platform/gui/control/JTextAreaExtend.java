package com.smart.platform.gui.control;

import java.awt.Color;

import javax.swing.JTextArea;

public class JTextAreaExtend extends JTextArea{
	boolean canedit=false;

	public boolean isCanedit() {
		return canedit;
	}

	public void setCanedit(boolean canedit) {
		this.canedit = canedit;
	}

    Color darkbackgroud=new Color(234,234,234);

	@Override
	public Color getBackground() {
		if(!canedit || !isEnabled()){
			return darkbackgroud;
		}else{
			return super.getBackground();
		}
	}


}
