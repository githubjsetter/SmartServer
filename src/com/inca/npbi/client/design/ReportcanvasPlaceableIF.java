package com.inca.npbi.client.design;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.inca.np.gui.control.DBTableModel;

/**
 * 在画布上可以放置的接口.包括自由单元格,表格,图形等.
 * @author user
 *
 */
public interface ReportcanvasPlaceableIF {
	/**
	 * 放置类型
	 * @return
	 */
	String getType();
	
	void setID(int id);
	int getID();
	/**
	 * 取大小
	 */
	Dimension getSize();
	
	void setSize(Dimension size);
	
	/**
	 * 准备数据. 数据变化,或数据显示方式变化,会调用.
	 * @return
	 */
	public boolean prepareData() ;

	/**
	 * 画
	 * @param g2
	 */
	void draw(Graphics2D g2,int pageno);
	
	void setDbtablemode(DBTableModel dm);
	
	void setCalcer(BICellCalcer calcer);

	int getPagecount();
	
	/**
	 * 用于设置报表的实际开始位置.
	 * @param layoutstarty
	 */
	void setLayoutstarty(int layoutstarty) ;

}
