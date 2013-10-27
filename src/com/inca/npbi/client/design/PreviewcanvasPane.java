package com.inca.npbi.client.design;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class PreviewcanvasPane extends JPanel {
	BufferedImage img = null;
	Tablevdesignpane frm=null;
	

	public PreviewcanvasPane(Tablevdesignpane frm) {
		super();
		this.frm = frm;
	}


	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (img == null) {
			return;
		}
		
		g.drawImage(img, 0,0,img.getWidth(),img.getHeight(),null);
	}



	public BufferedImage getImg() {
		return img;
	}


	public void setImg(BufferedImage img) {
		this.img = img;
	}


	@Override
	public Dimension getPreferredSize() {
		if(img==null)
		return super.getPreferredSize();
		
		return new Dimension(img.getWidth(),img.getHeight());
	}
	
	

}
