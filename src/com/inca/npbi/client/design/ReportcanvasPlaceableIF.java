package com.inca.npbi.client.design;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.inca.np.gui.control.DBTableModel;

/**
 * �ڻ����Ͽ��Է��õĽӿ�.�������ɵ�Ԫ��,���,ͼ�ε�.
 * @author user
 *
 */
public interface ReportcanvasPlaceableIF {
	/**
	 * ��������
	 * @return
	 */
	String getType();
	
	void setID(int id);
	int getID();
	/**
	 * ȡ��С
	 */
	Dimension getSize();
	
	void setSize(Dimension size);
	
	/**
	 * ׼������. ���ݱ仯,��������ʾ��ʽ�仯,�����.
	 * @return
	 */
	public boolean prepareData() ;

	/**
	 * ��
	 * @param g2
	 */
	void draw(Graphics2D g2,int pageno);
	
	void setDbtablemode(DBTableModel dm);
	
	void setCalcer(BICellCalcer calcer);

	int getPagecount();
	
	/**
	 * �������ñ����ʵ�ʿ�ʼλ��.
	 * @param layoutstarty
	 */
	void setLayoutstarty(int layoutstarty) ;

}
