package com.inca.np.print.drawable;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.PrintWriter;

import com.inca.np.gui.control.CCheckBox;
import com.inca.np.gui.control.CComboBox;
import com.inca.np.gui.control.CComboBoxModel;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-5-9 Time: 16:54:32
 * 一行数据项中的一列
 */
public class PColumnCell extends PDrawcellBase {
	String colname = "";
	String title = "";
	int colindex = -1;
	static Font titlefont = new Font("宋体", Font.BOLD, 10);

	/**
	 * 填充大小
	 */
	int pad = 1;

	public PColumnCell(String colname) {
		this.colname = colname;
	}

	public PColumnCell(String colname, int width) {
		this.colname = colname;
		this.width = width;
	}

	public PColumnCell(String colname, String title, int width) {
		this.colname = colname;
		this.title = title;
		this.width = width;
	}

	boolean freeze = false;

	/**
	 * 如果是一货一票的情况,一行记录打印成卡片. 在一行中的相对起始y位置.
	 */
	int suby = 0;

	/**
	 * 如果是一货一票的情况,一行记录打印成卡片. 在一行中的相对的高度.
	 */
	int subheight = 27;

	public String getColname() {
		return colname;
	}

	public void setColname(String colname) {
		this.colname = colname;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isFreeze() {
		return freeze;
	}

	public void setFreeze(boolean freeze) {
		this.freeze = freeze;
	}

	public int getPad() {
		return pad;
	}

	public void setPad(int pad) {
		this.pad = pad;
	}

	/*
	 * public int getSuby() { return suby; }
	 * 
	 * public void setSuby(int suby) { this.suby = suby; }
	 * 
	 * public int getSubheight() { return subheight; }
	 * 
	 * public void setSubheight(int subheight) { this.subheight = subheight; }
	 */

	public void paintTitle(Graphics2D g2, DBTableModel dbmodel, int row, int x,
			int y, int height) {
		// 画标题
		g2.setFont(titlefont);
		String value = title;
		if (value == null) {
			return;
		}
		drawCell(g2, value, x, y, width, height, ALIGN_CENTER);
	}

	/**
	 * 画交叉表的标题.下部的
	 * 
	 * @param g2
	 * @param dbmodel
	 * @param row
	 * @param x
	 * @param y
	 * @param height
	 */
	public void paintCrosstitleBottom(Graphics2D g2, DBTableModel dbmodel,
			int row, int x, int y, int height) {
		// 画标题
		g2.setFont(titlefont);
		int p = title.indexOf(":");
		String value = title;
		if (p >= 0) {
			value = title.substring(p + 1);
		}
		if (value == null) {
			return;
		}
		drawCell(g2, value, x, y + height / 2, width, height, ALIGN_CENTER);
	}

	public void paintCrosstitle(Graphics2D g2, String titlevalue, int x, int y,
			int width, int height) {
		// 画标题
		g2.setFont(titlefont);
		drawCell(g2, titlevalue, x, y, width, height, ALIGN_CENTER);
	}

	public void paint(Graphics2D g2, DBTableModel dbmodel, int row, int x,
			int y, int height) {
		// 画数据
		g2.setFont(font);
		String newvalue = dbmodel.getItemValue(row, colname);
		if (newvalue == null) {
			newvalue = "";
		}

		if (row == dbmodel.getRowCount() - 1) {
			if (this.colindex == 0) {
				newvalue = "合计";
				drawCell(g2, newvalue, x, y, width, height, align);
				return;
			} else if (colname.equals("行号")) {
				return;
			}
		} else {
			if (colname.equals("行号")) {
				newvalue = String.valueOf(row + 1);
				drawCell(g2, newvalue, x, y, width, height, align);
				return;
			}
		}

		DBColumnDisplayInfo colinfo = dbmodel.getColumninfo(colname);

		if (colinfo == null) {
			System.err.println("找不到列" + colname);
			return;
		}

		if (colinfo.getEditComponent() instanceof CComboBox) {
			DBTableModel cbdbmodel = colinfo.getCbdbmodel();
			if (cbdbmodel != null) {
				boolean bfind = false;
				for (int r = 0; r < cbdbmodel.getRowCount(); r++) {
					if (cbdbmodel.getItemValue(r, "key").equals(newvalue)) {
						newvalue = cbdbmodel.getItemValue(r, "value");
						bfind = true;
						break;
					}
				}
				if (!bfind) {
					CComboBox ccb = (CComboBox) colinfo.getEditComponent();
					CComboBoxModel ccbmodel = (CComboBoxModel) ccb.getModel();
					for (int i = 0; i < ccbmodel.getSize(); i++) {
						if (newvalue.equals(ccbmodel.getKeyvalue(i))) {
							newvalue = (String) ccbmodel.getElementAt(i);
						}
					}
				}
			}
		} else if (colinfo.getEditComponent() instanceof CCheckBox) {
			newvalue = newvalue != null && newvalue.equals("1") ? "是" : "否";
		} else if (colinfo.getColtype().equals("行号")) {
			newvalue = String.valueOf(row + 1);
		}

		if (colinfo.getColtype().equals("number")) {
			align = ALIGN_RIGHT;
		} else {
			align = ALIGN_LEFT;
		}
		drawCell(g2, newvalue, x, y, width, height, align);
	}

	public int calcBesttitleHeight(Graphics2D g2) {
		Font oldfont = g2.getFont();
		g2.setFont(titlefont);
		FontMetrics fm = g2.getFontMetrics();
		double tmpy = 0;
		String value = title;
		while (value.length() > 0) {
			Rectangle2D rect = fm.getStringBounds(value, g2);
			if (rect.getWidth() > width) {
				// 需要进行分解
				int charct = 1;
				for (charct = 1; charct < value.length(); charct++) {
					Rectangle2D bounds = fm.getStringBounds(value.substring(0,
							charct), g2);
					if (bounds.getWidth() > width) {
						charct--;
						break;
					}
				}
				if (charct == 0)
					charct = 1;

				// String subs = value.substring(0, charct);
				value = value.substring(charct);

				tmpy += rect.getHeight();

			} else {
				tmpy += rect.getHeight();
				break;
			}
		}

		g2.setFont(oldfont);
		return (int) tmpy;
	}

	protected void writeOther(PrintWriter out) {
		out.println("columnname=" + colname);
		out.println("title=" + title);
	}

	protected void readOther(BufferedReader in) throws Exception {
		String line = in.readLine();
		colname = line.substring("columnname=".length());
		line = in.readLine();
		title = line.substring("title=".length());
		return;
	}

	public int getColindex() {
		return colindex;
	}

	public void setColindex(int colindex) {
		this.colindex = colindex;
	}

}
