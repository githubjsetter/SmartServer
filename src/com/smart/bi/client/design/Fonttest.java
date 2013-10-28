package com.smart.bi.client.design;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Fonttest {
	public static void main(String[] args) {
		try {
			double scale=1.2;
			BufferedImage img=new BufferedImage(320,240,BufferedImage.TYPE_INT_RGB);
			Graphics2D g2=(Graphics2D)img.getGraphics();
			AffineTransform tran=g2.getTransform();
			tran.scale(scale,scale);
			g2.setTransform(tran);
			g2.setColor(Color.WHITE);
			g2.fillRect(0, 0, 320, 240);
			
			Font font=new Font("ÀŒÃÂ",Font.PLAIN,16);
			g2.setFont(font);
			g2.setColor(Color.black);
			String s="≤‚ ‘◊÷ÃÂ";
			
			FontMetrics fm=g2.getFontMetrics();
			int asc=fm.getAscent();
			int desc=fm.getDescent();
			int fh=asc+desc;
			int fwidth=fm.charsWidth(s.toCharArray(),0,s.length());
			g2.drawString(s,0,asc);
			g2.drawRect(0, 0, fwidth, fh);
			
			ImageIO.write(img,"png",new File("font.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
