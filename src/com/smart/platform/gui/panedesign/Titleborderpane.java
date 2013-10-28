package com.smart.platform.gui.panedesign;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;


/**
 * titleborderµÄÖ÷¿Ø¼þ.
 * @author user
 *
 */
public class Titleborderpane extends JPanel{
	String title;

	public Titleborderpane(String title) {
		super();
		this.title = title;
		setPreferredSize(new Dimension(320,240));
		Border bdetch=BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border titleborder = BorderFactory.createTitledBorder(bdetch, title);
		setBorder(titleborder);
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}
