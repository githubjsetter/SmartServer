package com.smart.platform.gui.ui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.table.TableColumnModel;

import com.smart.platform.gui.control.CTable;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;

public class CTableMultiheadUI extends CTableheadUI {

	public CTableMultiheadUI() {
		super();
	}

	@Override
	public void paint(Graphics g, JComponent comp) {
		// TODO Auto-generated method stub
		super.paint(g, comp);
		Color oldc = g.getColor();
		Color gridcolor=UIManager.getColor("Table.gridColor");
		Color textcolor=Color.BLACK;
		g.setColor(gridcolor);
		
		// 画线和cross的标题
		CTable table = (CTable) header.getTable();
		DBTableModel dbmodel = (DBTableModel) table.getModel();
		Vector<DBColumnDisplayInfo> colinfos = dbmodel.getDisplaycolumninfos();
		TableColumnModel cm = header.getColumnModel();
		double x = 0;

		String crosstitle="";
		double crosstitlestartx=-1;
		int ix=0,iy=0;
		Rectangle r=null;
		for (int c = 0; c < cm.getColumnCount(); c++) {
			int modelindex = cm.getColumn(c).getModelIndex();
			DBColumnDisplayInfo colinfo = colinfos.elementAt(modelindex);

			r = header.getHeaderRect(c);
			if (!colinfo.isCrossdata()) {
				// 在左边画竖线
				ix=(int)x-1;
				g.drawLine(ix, 0, ix, (int) r.getHeight());
			}else{
				//画中间的横线
				ix=(int)x;
				int ix1 = (int)(x + r.getWidth());
				iy=(int)(r.getHeight() / 2.0);
				g.drawLine(ix,iy,ix1,iy);
				
				//需要画左竖线吗？
				String title=colinfo.getTitle();
				int p=title.indexOf(":");
				title=title.substring(0,p);
				if(!crosstitle.equals(title) ){
					if(crosstitlestartx>=0){
						//需要写cross列的值
						FontMetrics fm = g.getFontMetrics();
						Rectangle2D crosstitlerect = fm.getStringBounds(crosstitle, g);
						double crossw = x - crosstitlestartx ;
						int tx=(int)(crosstitlestartx + (crossw - crosstitlerect.getWidth())/2.0);
						int ty = (int)((r.getHeight() - crosstitlerect.getHeight())/2.0);
						g.setColor(textcolor);
						g.drawString(crosstitle, tx, ty);
						g.setColor(gridcolor);
					}
					
					crosstitlestartx=x;
					crosstitle=title;
					// 在左边画竖线
					ix=(int)x-1;
					g.drawLine(ix, 0, ix, (int) r.getHeight());
				}else{
					//画半根吧
					ix=(int)x-1;
					g.drawLine(ix, iy, ix, (int) r.getHeight());
				}
			}
			x += r.getWidth();

		}
		//最后的标题
		if(crosstitlestartx>=0){
			//需要写cross列的值
			FontMetrics fm = g.getFontMetrics();
			Rectangle2D crosstitlerect = fm.getStringBounds(crosstitle, g);
			double crossw = x - crosstitlestartx ;
			int tx=(int)(crosstitlestartx + (crossw - crosstitlerect.getWidth())/2.0);
			int ty = (int)((r.getHeight() - crosstitlerect.getHeight())/2.0);
			g.setColor(textcolor);
			g.drawString(crosstitle, tx, ty);
			g.setColor(gridcolor);
		}
		
		//最右竖线
		ix=(int)x-1;
		g.drawLine(ix, 0, ix, (int) r.getHeight());
		
		g.setColor(oldc);
	}

}
