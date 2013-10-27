package com.inca.np.anyprint;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.util.Enumeration;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.inca.np.anyprint.impl.Bodypart;
import com.inca.np.anyprint.impl.Cellbase;
import com.inca.np.anyprint.impl.Footpart;
import com.inca.np.anyprint.impl.Headpart;
import com.inca.np.anyprint.impl.Partbase;
import com.inca.np.anyprint.impl.Parts;

/**
 * 画出表头表身表尾区域，设计界面。
 * 
 * @author Administrator
 * 
 */
public class CanvasPane extends JPanel implements ActionListener {

	AnyprintFrame frm = null;
	Printplan plan = null;
	int width = 2000;
	int height = 2000;
	/**
	 * 显示比例
	 */
	double scalerate=1.0;

	public CanvasPane(AnyprintFrame frm, Printplan plan) {
		this.frm = frm;
		this.plan = plan;
		this.addMouseListener(new CanvasMouseListener());
		this.addMouseMotionListener(new CanvasMouseMotionListener());
		setHotkey(this);

	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		draw(g2);
	}

	public void draw(Graphics2D g2) {
		Color oldc = g2.getColor();
		AffineTransform oldtran=g2.getTransform();
		AffineTransform tran=new AffineTransform(oldtran);
		//放大scalerate
		tran.scale(scalerate,scalerate);
		g2.setTransform(tran);
		
		g2.setColor(Color.LIGHT_GRAY);
		g2.fillRect(0, 0, width, getHeight());
		g2.setColor(oldc);

		Parts parts = plan.getParts();
		int y = 0;
		for (int i = 0; i < parts.getPartcount(); i++) {
			Partbase part = parts.getPart(i);
			if (part.getHeight() <= 0)
				continue;
			Graphics2D partg2 = (Graphics2D) g2.create(0, y, width, part
					.getHeight());
			partg2.setColor(Color.white);
			partg2.fillRect(0, 0, width, part.getHeight());
			partg2.setColor(Color.LIGHT_GRAY);
			String partname="";
			if(i==0){
				partname="页头";
			}else if(i==1){
				partname="表头";
			}else if(i==2){
				partname="表行";
			}else if(i==3){
				partname="表尾";
			}else if(i==4){
				partname="页脚";
			}
			partg2.drawString(partname,1, 12);
			partg2.setColor(oldc);
			part.draw(partg2);
			y += part.getHeight();
			g2.setColor(Color.blue);
			g2.drawLine(0, y, parts.getPaperwidthPixel(), y);
			y++;
		}
		
		//右边的竖线
		int pagex=parts.getPaperwidthPixel();
		if(parts.isLandscape()){
			pagex=parts.getPaperheightPixel();
		}
		g2.drawLine(pagex, 0, pagex, plan.getParts().getHeight());
		g2.setColor(oldc);
		
		g2.setTransform(oldtran);
	}

	/**
	 * @param p
	 */
	void drag(Point p) {
		//	 * 这里不需要再次除以scalerate了.

		plan.getParts().onDrag(p);
		// if(plan.getParts().needRepaint())
		repaint();
	}

	void onMousemove(Point p) {
		// 当鼠标移动，知道在哪个元件
		int newx=(int)((double)p.x / scalerate+0.5);
		int newy=(int)((double)p.y / scalerate+0.5);
		Point p1=new Point(newx,newy);
		
		plan.getParts().onMousemove(p1);
		if (plan.getParts().needRepaint())
			repaint();
	}

	class CanvasMouseListener implements MouseListener {

		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() > 1) {
				frm.setWaitcursor();
				CellpropDlg dlg = new CellpropDlg(frm, plan);
				frm.setDefaultcursor();
				Partbase activepart = plan.getParts().getActivepart();
				Cellbase activecell = plan.getParts().getActivecell();
				if (activepart != null && activecell != null) {
					dlg.editCell(activepart, activecell);
					dlg.pack();
					dlg.setVisible(true);
				}
			}
		}

		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		public void mouseExited(MouseEvent e) {
			frm.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

		public void mousePressed(MouseEvent e) {
			int newx=(int)((double)e.getPoint().x / scalerate+0.5);
			int newy=(int)((double)e.getPoint().y / scalerate+0.5);
			Point p1=new Point(newx,newy);
			plan.getParts().onMousePressed(p1);
		}

		public void mouseReleased(MouseEvent e) {
			int newx=(int)((double)e.getPoint().x / scalerate+0.5);
			int newy=(int)((double)e.getPoint().y / scalerate+0.5);
			Point p1=new Point(newx,newy);
			plan.getParts().onMouseReleased(p1);
			repaint();
		}

	}

	class CanvasMouseMotionListener implements MouseMotionListener {

		public void mouseDragged(MouseEvent e) {
			int newx=(int)((double)e.getPoint().x / scalerate+0.5);
			int newy=(int)((double)e.getPoint().y / scalerate+0.5);
			Point p1=new Point(newx,newy);
			drag(p1);
		}

		public void mouseMoved(MouseEvent e) {
			Point p = e.getPoint();
			onMousemove(p);
		}

	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension((int)((double)width*scalerate), (int)((double)height*scalerate));
	}
	
	void setHotkey(JComponent jcp){
		KeyStroke vk_del=KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0,false);
		InputMap im=jcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap am=jcp.getActionMap();

		im.put(vk_del, "delcell");
		am.put("delcell", new PaneAction("delcell"));

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,0,false), "moveleft");
		am.put("moveleft", new PaneAction("moveleft"));
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,0,false), "moveright");
		am.put("moveright", new PaneAction("moveright"));
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,0,false), "moveup");
		am.put("moveup", new PaneAction("moveup"));
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,0,false), "movedown");
		am.put("movedown", new PaneAction("movedown"));
}
	
	class PaneAction extends AbstractAction{
		PaneAction(String name){
			super();
			super.putValue(AbstractAction.ACTION_COMMAND_KEY,name);
		}

		public void actionPerformed(ActionEvent e) {
			//System.out.println(e.getActionCommand());
			if(e.getActionCommand().equals("delcell")){
				//System.out.println("del cell");
				plan.getParts().delActivecell();
				repaint();
			}else if(e.getActionCommand().equals("moveleft")){
				plan.getParts().moveLeft();
				repaint();
			}else if(e.getActionCommand().equals("moveright")){
				plan.getParts().moveRight();
				repaint();
			}else if(e.getActionCommand().equals("moveup")){
				plan.getParts().moveUp();
				repaint();
			}else if(e.getActionCommand().equals("movedown")){
				plan.getParts().moveDown();
				repaint();
			}
		}
	}

	@Override
	public boolean isFocusable() {
		return true;
	}

	public double getScalerate() {
		return scalerate;
	}

	public void setScalerate(double scalerate) {
		this.scalerate = scalerate;
	}

	
}
