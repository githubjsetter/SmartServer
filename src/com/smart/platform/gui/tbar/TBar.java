package com.smart.platform.gui.tbar;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.apache.log4j.Category;

/**
 * 工具条.底色为图片,上面放置透明的按钮TButton.
 * @author user
 *
 */
public class TBar extends JPanel{
	Category logger=Category.getInstance(TBar.class);
	public TBar(){
		imgbd=loadImage("tbar.gif");
		setLayout(new TbarLayout(FlowLayout.LEFT));
	}
	
	BufferedImage imgbd=null;
	
	
	@Override
	public void paint(Graphics g) {
		//填充背景
		paintBg(g);
		paintChildren(g);
	}



	private void paintBg(Graphics g) {
		int ct=getWidth() / imgbd.getWidth();
		ct++;
		
		int x=0;
		for(int i=0;i<ct;i++,x+=imgbd.getWidth()){
			g.drawImage(imgbd,x,0,imgbd.getWidth(),imgbd.getHeight(),null);
		}
	}

	


	@Override
	public Dimension getPreferredSize() {
		return new Dimension((int)super.getPreferredSize().getWidth(),imgbd.getHeight());
	}



	@Override
	public Dimension getMaximumSize() {
		return new Dimension((int)super.getMaximumSize().getWidth(),imgbd.getHeight());
	}



	@Override
	public Dimension getMinimumSize() {
		return new Dimension((int)super.getMinimumSize().getWidth(),imgbd.getHeight());
	}



	BufferedImage loadImage(String filename) {
		InputStream in = TBar.class.getResourceAsStream(filename);
		if (in == null) {
			return null;
		}
		try {
			BufferedImage image = ImageIO.read(in);
			return image;
		} catch (IOException e) {
			logger.error("error", e);
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	class TbarLayout extends FlowLayout{

		public TbarLayout() {
			super();
			setHgap(0);
		}

		public TbarLayout(int align, int hgap, int vgap) {
			super(align, hgap, vgap);
			setHgap(0);
		}

		public TbarLayout(int align) {
			super(align);
			setHgap(0);
		}

		@Override
		public void layoutContainer(Container target) {
			super.layoutContainer(target);
			int ct=target.getComponentCount();
			for(int i=0;i<ct;i++){
				Component m = target.getComponent(i);
				java.awt.Point p=m.getLocation();
				p.y=1;
				m.setLocation(p);
				
			}
		}
		
	}
	
	protected void addSeparator(){
		addSeparator(null);
	}

	protected void addSeparator(Dimension size){
        JToolBar.Separator s = new JToolBar.Separator( size );
        add(s);
	}
	

    public void removeDefaultKey(){
/*        InputMap inputmap = this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputmap.getParent().clear();
        this.setFocusable(false);

        for(int i=0;i<this.getComponentCount();i++){
            JComponent comp = (JComponent) getComponent(i);
            comp.setFocusable(false);
        }
*/
    	}

}
