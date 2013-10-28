package com.smart.platform.gui.control;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

/**
 * 卡片上的字段分组
 * @author Administrator
 *
 */
public class CFieldGroup extends JComponent{

	String title="";
	Font font=new Font("宋体",Font.BOLD|Font.ITALIC,16);
	int leftlinewidth=20; 
	int rightlinewidth=500;
	
	public CFieldGroup(String title) {
		super();
		this.title = title;
	}
	@Override
	public void paint(Graphics g) {
		UIDefaults table = UIManager.getLookAndFeelDefaults();
		/*
           table.getColor("TextField.shadow"),
           table.getColor("TextField.darkShadow"),
           table.getColor("TextField.light"),
           table.getColor("TextField.highlight"));

		 */

		Graphics2D g2=(Graphics2D)g;
		g2.setFont(font);
		FontMetrics fm=g2.getFontMetrics();
		char[] car=title.toCharArray();
		int width = fm.charsWidth(car,0,car.length);
		int height=fm.getHeight();
		
		
		int liney=height>>1;
		g.setColor(table.getColor("TextField.light"));
		g.drawLine(0,liney,leftlinewidth,liney);
		g.setColor(table.getColor("TextField.darkShadow"));
		g.drawLine(0,liney+1,leftlinewidth,liney+1);
		
		g.setColor(Color.BLACK);
		g.drawString(title, leftlinewidth, height);

		int x=leftlinewidth + width + 2;
		g.setColor(table.getColor("TextField.light"));
		g.drawLine(x,liney,x+rightlinewidth,liney);
		g.setColor(table.getColor("TextField.darkShadow"));
		g.drawLine(x,liney+1,x+rightlinewidth,liney+1);
	}
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(500,20);
	}
	/*
	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 20;
	}

	@Override
	public Dimension getSize(Dimension rv) {
		return new Dimension(100,20);
	}
	
	

*/	
}
