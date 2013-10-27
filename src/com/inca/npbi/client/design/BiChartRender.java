package com.inca.npbi.client.design;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.apache.log4j.Category;

import com.inca.np.gui.control.DBTableModel;
import com.inca.npbi.client.support.ChartFactory;

/**
 * 图形的基类
 * 
 * @author user
 * 
 */
public class BiChartRender implements ReportcanvasPlaceableIF {
	Dimension size = new Dimension(500, 300);
	Chartdefine chartdefine;
	ChartFactory chartfactory;
	DBTableModel orgdm;
	Category logger=Category.getInstance(BiChartRender.class);
	
	public BiChartRender() {
		chartdefine = createDefaultchart();
		chartfactory = new ChartFactory();
	}

	public BiChartRender(Chartdefine chartdefine) {
		this.chartdefine = chartdefine;
		chartfactory = new ChartFactory();
	}

	private Chartdefine createDefaultchart() {
		Chartdefine chartdef = new Chartdefine();
		chartdef.charttype = 0;
		chartdef.dimension = 0;
		chartdef.title = "客户发货额";
		chartdef.x1column = "CUSTOMNAME";
		chartdef.xtitle = "客户";
		chartdef.y1column = "TOTAL";
		chartdef.y1title = "发货额";

		return chartdef;
	}

	public Chartdefine getChartdefine() {
		return chartdefine;
	}

	public void setChartdefine(Chartdefine chartdefine) {
		this.chartdefine = chartdefine;
	}

	public void draw(Graphics2D g2, int pageno) {
		chartfactory.drawChart(g2, chartdefine, size.width, size.height);
	}

	public int getPagecount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Dimension getSize() {
		return size;
	}

	public String getType() {
		return id + ":图表";
	}

	public void setCalcer(BICellCalcer calcer) {
		chartdefine.setCalcer(calcer);
	}

	public void setDbtablemode(DBTableModel dm) {
		this.orgdm = dm;
	}

	public void setSize(Dimension size) {
		this.size = size;
	}

	public void setLayoutstarty(int layoutstarty) {
		// TODO Auto-generated method stub

	}

	int id;

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public boolean prepareData() {
		logger.debug("bichartrender preparedata");
		chartfactory.prepareData(chartdefine, orgdm);
		logger.debug("bichartrender preparedata ok");
		return true;
	}

}
