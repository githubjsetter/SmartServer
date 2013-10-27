package com.inca.np.gui.panedesign;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.Category;

import com.inca.np.gui.control.CTextArea;

/**
 * ������Сλ�õĿؼ�
 * 
 * @author user
 * 
 */
public class AdjsizeComp {
	/**
	 * ���м�,�ƶ�
	 */
	public static int MOUSE_ENTER = 0;

	/**
	 * ���ұ�
	 */
	public static int MOUSE_RIGHT = 3;

	/**
	 * ���±�
	 */
	public static int MOUSE_BOTTOM = 4;

	/**
	 * ͬʱ���ұߺ��±�
	 */
	public static int MOUSE_RIGHTBOTTOM = 5;

	Compinfo compinfo = null;
	Rectangle rect;
	Category logger=Category.getInstance(AdjsizeComp.class);
	/**
	 * �Ƿ�ѡ��?
	 */
	boolean selected = false;

	public AdjsizeComp(Compinfo compinfo) {
		super();
		this.compinfo = compinfo;
		this.rect = compinfo.rect;
	}

	/**
	 * �����ܱ�4���ߺ����½�һ��Ȧ
	 * 
	 * @param g2
	 */
	public void paint(Graphics2D g2) {
		if(compinfo.realcomp==null){
			return;
		}
		Graphics2D gtmp = (Graphics2D) g2.create(rect.x, rect.y, rect.width,
				rect.height);

		if(compinfo.realcomp instanceof JLabel || compinfo.realcomp instanceof JButton){
			gtmp.setColor(Color.black);
			if(compinfo.compname!=null){
				gtmp.drawString(compinfo.compname, 1, 13);
			}
			gtmp.dispose();
		}else if(compinfo.realcomp instanceof DPanel){
			//do nothing
		}else if(compinfo.realcomp instanceof JTextField || compinfo.realcomp instanceof CTextArea
				 || compinfo.realcomp instanceof JTextArea || compinfo.realcomp instanceof JComboBox
				 || compinfo.realcomp instanceof JCheckBox || compinfo.realcomp instanceof JRadioButton
				 || compinfo.realcomp instanceof JScrollPane){
			gtmp.setColor(Color.white);
			gtmp.fillRect(0,0,rect.width,
				rect.height);
			gtmp.setColor(Color.black);
			gtmp.drawString(compinfo.compname, 1, 13);
			gtmp.dispose();
		}else if(compinfo.realcomp instanceof Titleborderpane){
			//�����߿�ʾ��һ��.
			int x = compinfo.rect.x;
			int y = compinfo.rect.y;
			int w = compinfo.rect.width;
			int h = compinfo.rect.height;
			
			
			g2.translate(x, y);
			
			g2.setColor(compinfo.realcomp.getBackground());
			g2.drawRect(0, 5, w-2, h-2);
			
			g2.setColor(compinfo.realcomp.getBackground().brighter());
			g2.drawLine(1, h-3, 1, 5);
			g2.drawLine(1, 5, w-3, 5);
			
			g2.drawLine(0, h-1, w-1, h-1);
			g2.drawLine(w-1, h-1, w-1, 5);
			
			g2.translate(-x, -y);
			
			g2.setColor(Color.BLACK);
			String title=((Titleborderpane)compinfo.realcomp).getTitle();
			g2.drawString(title,x+10,y+10);

		}else{
			logger.error("error,not support comp class "+compinfo.realcomp.getClass().getName());
		}

		if (isSelected()) {
			g2.setColor(Color.blue);
			g2.drawRect(rect.x, rect.y, rect.width, rect.height);

			// �ұߵ�ԲȦ
			int ax = rect.x + rect.width;
			int ay = rect.y + rect.height / 2 - 2;
			g2.drawArc(ax, ay, 5, 5, 0, 360);

			// �±ߵ�
			ax = rect.x + rect.width / 2 - 2;
			ay = rect.y + rect.height;
			g2.drawArc(ax, ay, 5, 5, 0, 360);

			// ���±�
			ax = rect.x + rect.width;
			ay = rect.y + rect.height - 2;
			g2.drawArc(ax, ay, 5, 5, 0, 360);
		}else{
			g2.setColor(Color.lightGray);
			g2.drawRect(rect.x, rect.y, rect.width, rect.height);
		}

	}

	/**
	 * ����С��0��ʾû���н���. ����MOUSE_ ��ʾ��ͬ״̬
	 * 
	 * @param p
	 * @return
	 */
	public int onMousemove(Point p) {

		int offset = 5;
		int rightx = rect.x + rect.width;
		int bottomy = rect.y + rect.height;

		// System.out.println("x cond="+(p.x > (rightx - offset) && p.x <
		// (rightx + offset)));
		// System.out.println("y cond="+(p.y > (bottomy - offset) && (p.y <
		// bottomy + offset)));
		if (p.x > (rightx - offset) && p.x < (rightx + offset)
				&& p.y > (bottomy - offset) && (p.y < bottomy + offset)) {
			// ���½�?
			return MOUSE_RIGHTBOTTOM;
		} else if (p.x > (rightx - offset) && p.x < (rightx + offset)
				&& p.y > (rect.y - offset) && (p.y < bottomy + offset)) {
			// �ұ�
			return MOUSE_RIGHT;
		} else if (p.x > (rect.x - offset) && p.x < (rightx + offset)
				&& p.y > (bottomy - offset) && (p.y < bottomy + offset)) {
			// �±�
			return MOUSE_BOTTOM;
		} else if (rect.contains(p)) {
			// ����
			//���realcomp��titleborderpane, ������ϱ߲���
			if(compinfo.realcomp instanceof Titleborderpane){
				//ֻ�ϱ�
				if(p.x>rect.x && p.x<rightx && p.y>rect.y-offset && p.y<rect.y+offset){
					return MOUSE_ENTER;
				}
			}else{
				return MOUSE_ENTER;
			}
		}

		return -1;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isIntersects(Rectangle batchrect){
		return batchrect.intersects(rect);
	}
}
