package com.smart.platform.anyprint;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import com.smart.platform.gui.control.CDialog;

public class FonttestDlg extends CDialog{
	public FonttestDlg(){
		super((Frame)null,"test font",true);
		init();
	}
	
	void init(){
		Container cp=getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(new Testpanel(),BorderLayout.CENTER);
	}
	
	class Testpanel extends JPanel{

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(200,200);
		}

		@Override
		public void paint(Graphics g) {
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, 200, 200);
			g.setColor(Color.black);
			//String value="0.45gx160’s";
			String value="中华人民共和国";
			
			Graphics2D g2=(Graphics2D)g;
			TextLayout layout = new TextLayout(value, g.getFont(), g2.getFontRenderContext());
			Rectangle2D bounds = layout.getBounds();
			System.out.println(bounds);

/*			layout.draw(g2, 30, 30);
			
			
			bounds.setRect(bounds.getX()+30,
	                  bounds.getY()+30,
	                  bounds.getWidth(),
	                  bounds.getHeight());
*/
			
			g2.draw(bounds);
		}
		
	}
	public static void main(String[] args) {
		FonttestDlg dlg=new FonttestDlg();
		dlg.pack();
		dlg.setVisible(true);
	}
}
