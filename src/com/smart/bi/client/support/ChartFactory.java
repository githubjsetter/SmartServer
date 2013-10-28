package com.smart.bi.client.support;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import javax.imageio.ImageIO;

import oracle.gss.util.CharConvBuilder.CharDataParser;

import org.apache.log4j.Category;
import org.eclipse.birt.chart.api.ChartEngine;
import org.eclipse.birt.chart.device.IDeviceRenderer;
import org.eclipse.birt.chart.exception.ChartException;
import org.eclipse.birt.chart.factory.GeneratedChartState;
import org.eclipse.birt.chart.factory.Generator;
import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.ChartWithAxes;
import org.eclipse.birt.chart.model.ChartWithoutAxes;
import org.eclipse.birt.chart.model.attribute.Angle3D;
import org.eclipse.birt.chart.model.attribute.AxisType;
import org.eclipse.birt.chart.model.attribute.Bounds;
import org.eclipse.birt.chart.model.attribute.ChartDimension;
import org.eclipse.birt.chart.model.attribute.FontDefinition;
import org.eclipse.birt.chart.model.attribute.IntersectionType;
import org.eclipse.birt.chart.model.attribute.LegendItemType;
import org.eclipse.birt.chart.model.attribute.Marker;
import org.eclipse.birt.chart.model.attribute.MarkerType;
import org.eclipse.birt.chart.model.attribute.Position;
import org.eclipse.birt.chart.model.attribute.TickStyle;
import org.eclipse.birt.chart.model.attribute.impl.Angle3DImpl;
import org.eclipse.birt.chart.model.attribute.impl.BoundsImpl;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.Rotation3DImpl;
import org.eclipse.birt.chart.model.component.Axis;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.component.impl.SeriesImpl;
import org.eclipse.birt.chart.model.data.NumberDataSet;
import org.eclipse.birt.chart.model.data.SeriesDefinition;
import org.eclipse.birt.chart.model.data.TextDataSet;
import org.eclipse.birt.chart.model.data.impl.NumberDataSetImpl;
import org.eclipse.birt.chart.model.data.impl.SeriesDefinitionImpl;
import org.eclipse.birt.chart.model.data.impl.TextDataSetImpl;
import org.eclipse.birt.chart.model.impl.ChartWithAxesImpl;
import org.eclipse.birt.chart.model.impl.ChartWithoutAxesImpl;
import org.eclipse.birt.chart.model.layout.Legend;
import org.eclipse.birt.chart.model.layout.Plot;
import org.eclipse.birt.chart.model.type.BarSeries;
import org.eclipse.birt.chart.model.type.BubbleSeries;
import org.eclipse.birt.chart.model.type.LineSeries;
import org.eclipse.birt.chart.model.type.PieSeries;
import org.eclipse.birt.chart.model.type.impl.BarSeriesImpl;
import org.eclipse.birt.chart.model.type.impl.BubbleSeriesImpl;
import org.eclipse.birt.chart.model.type.impl.LineSeriesImpl;
import org.eclipse.birt.chart.model.type.impl.PieSeriesImpl;
import org.eclipse.birt.core.framework.PlatformConfig;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;

import com.ibm.icu.math.BigDecimal;
import com.smart.bi.client.design.Chartdefine;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.util.DecimalHelper;
import com.sun.org.apache.xerces.internal.impl.dv.xs.DecimalDV;

/**
 * 图表的生成
 * 
 * @author user
 * 
 */
public class ChartFactory {

	 PlatformConfig config;
	 Generator gr;
	 IDeviceRenderer render = null;
	 boolean initok = false;
	 Chartvalues currentvalues=null;
	 Category logger=Category.getInstance(ChartFactory.class);
	public ChartFactory() {
		gr = Generator.instance();
		config = new PlatformConfig();
		config.setProperty("STANDALONE", "true");  //$NON-NLS-2$
		try {
			render = ChartEngine.instance(config).getRenderer("dv.SWING");
			initok = true;
		} catch (ChartException e) {
			initok = false;
			logger.error("error",e);
		}
	}
	
	public void prepareData(Chartdefine chartdefine,DBTableModel dm){
		currentvalues=calcValues(chartdefine, dm);
	}

	/**
	 * 生成图表
	 * 
	 * @param chartdefine
	 *            定义
	 * @param dm
	 *            数据
	 * @param width
	 *            宽
	 * @param height
	 *            高
	 * @return BufferedImage中是图
	 */
	public  void drawChart(Graphics graphics,Chartdefine chartdefine,
			int width, int height) {

		if (!initok) {
			logger.error("chart init failure,cann't draw chart");
			return ;
		}
		Chart cm = null;
		
		if(chartdefine.charttype==0){
			//柱
			cm=createBarChart(chartdefine);
		}else if(chartdefine.charttype==1){
			cm=createPieChart(chartdefine);
		}
		

		try {
			Rectangle d = new Rectangle(0, 0, width, height);

			// Image imgChart = new Image(null, d);
			// GC gcImage = new GC(imgChart);
			render.setProperty(IDeviceRenderer.GRAPHICS_CONTEXT, graphics);

			Bounds bo = BoundsImpl.create(0, 0, d.width, d.height);
			bo.scale(72d / render.getDisplayServer().getDpiResolution());

			GeneratedChartState gcs = gr.build(render.getDisplayServer(), cm,
					bo, null, null, null);

			gr.render(render, gcs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 按参数生成chart
	 * 
	 * @param chartdefine
	 * @param dm
	 * @return
	 */
	private  final Chart createBarChart(Chartdefine chartdefine) {
		ChartWithAxes cwaBar = ChartWithAxesImpl.create();

		if (chartdefine.dimension == 1) {
			// 2D
			cwaBar.setDimension(ChartDimension.TWO_DIMENSIONAL_LITERAL);
		} else if (chartdefine.dimension == 0) {
			// 2D有阴影
			cwaBar
					.setDimension(ChartDimension.TWO_DIMENSIONAL_WITH_DEPTH_LITERAL);
		} else if (chartdefine.dimension == 2) {
			// 3D
			cwaBar.setDimension(ChartDimension.THREE_DIMENSIONAL_LITERAL);
		} else {
			cwaBar.setDimension(ChartDimension.TWO_DIMENSIONAL_LITERAL);
		}

		// Plot
		cwaBar.getBlock().setBackground(ColorDefinitionImpl.WHITE());

		cwaBar.getBlock().getOutline().setVisible(true);

		Plot p = cwaBar.getPlot();
		p.getClientArea().setBackground(
				ColorDefinitionImpl.create(255, 255, 255));
		p.getOutline().setVisible(false);

		// Title
		cwaBar.getTitle().getLabel().getCaption().setValue(chartdefine.getTitlevalue());
		toswtFont(chartdefine.titlefont, cwaBar.getTitle().getLabel()
				.getCaption().getFont());

		// Legend
		Legend lg = cwaBar.getLegend();
		if (chartdefine.showlegend) {
			lg.setVisible(true);
			toswtFont(chartdefine.legendfont, lg.getText().getFont());
			if (chartdefine.colortype == 0) {
				// 按系列分颜色
				lg.setItemType(LegendItemType.SERIES_LITERAL);
			} else {
				// 按数据分
				lg.setItemType(LegendItemType.CATEGORIES_LITERAL);
			}
		}else{
			lg.setVisible(false);
		}

		// X-Axis
		Axis xAxisPrimary = cwaBar.getPrimaryBaseAxes()[0];

		xAxisPrimary.setType(AxisType.TEXT_LITERAL);
		xAxisPrimary.getMajorGrid().setTickStyle(TickStyle.BELOW_LITERAL);
		xAxisPrimary.getOrigin().setType(IntersectionType.VALUE_LITERAL);
		xAxisPrimary.getTitle().setVisible(true);
		xAxisPrimary.getTitle().getCaption().setValue(chartdefine.getXTitlevalue());
		toswtFont(chartdefine.xfont, xAxisPrimary.getTitle().getCaption()
				.getFont());

		// ////////////维度列
		xAxisPrimary.getLabel().setVisible(true);
		toswtFont(chartdefine.x1font, xAxisPrimary.getLabel().getCaption()
				.getFont());
		xAxisPrimary.getLabel().getCaption().getFont().setRotation(
				chartdefine.x1fontrotation);

		// Y-Axis
		Axis yAxisPrimary = cwaBar.getPrimaryOrthogonalAxis(xAxisPrimary);
		// cwaBar.get

		yAxisPrimary.getMajorGrid().setTickStyle(TickStyle.LEFT_LITERAL);
		yAxisPrimary.setType(AxisType.LINEAR_LITERAL);
		
		//Y坐标轴的标注
		toswtFont(chartdefine.yLabelfont,yAxisPrimary.getLabel().getCaption().getFont());
		
		yAxisPrimary.getTitle().setVisible(true);
		yAxisPrimary.getTitle().getCaption().setValue(chartdefine.getYTitlevalue());
		yAxisPrimary.getTitle().getCaption().getFont().setRotation(0);
		toswtFont(chartdefine.yfont, yAxisPrimary.getTitle().getCaption()
				.getFont());
		yAxisPrimary.setTitlePosition(Position.LEFT_LITERAL);

		yAxisPrimary.getTitle().getCaption().getFont().setRotation(
				chartdefine.yfontrotation);

		//
		String xs[]=currentvalues.xs;
		double y1s[]=currentvalues.y1s;
		double y2s[]=currentvalues.y2s;
		double y3s[]=currentvalues.y3s;

		
		
		// 先定义seriesdefine
		SeriesDefinition sdX = SeriesDefinitionImpl.create();
		sdX.getSeriesPalette().shift(0);
		xAxisPrimary.getSeriesDefinitions().add(sdX);

		// 加入x
		Series seCategory = SeriesImpl.create();
		TextDataSet xValues = TextDataSetImpl.create(xs);
		seCategory.setDataSet(xValues);
		sdX.getSeries().add(seCategory);

		// Y-Series

		SeriesDefinition sdY = SeriesDefinitionImpl.create();
		sdY.getSeriesPalette().shift(-1);
		yAxisPrimary.getSeriesDefinitions().add(sdY);

		NumberDataSet y1Values = NumberDataSetImpl.create(y1s);
		BarSeries bs1 = createBarserial(chartdefine, y1Values,
				chartdefine.getY1Titlevalue());
		sdY.getSeries().add(bs1);

		if (y2s != null) {
			NumberDataSet y2Values = NumberDataSetImpl.create(y2s);
			BarSeries bs2 = createBarserial(chartdefine, y2Values,
					chartdefine.getY2Titlevalue());
			sdY.getSeries().add(bs2);
		}
		if (y3s != null) {
			NumberDataSet y3Values = NumberDataSetImpl.create(y3s);
			BarSeries bs3 = createBarserial(chartdefine, y3Values,
					chartdefine.getY3Titlevalue());
			sdY.getSeries().add(bs3);
		}

		return cwaBar;
	}
	
	 Chartvalues calcValues(Chartdefine chartdefine,DBTableModel dm){
		DBTableModel calcdm = dm;
		String cop;
		if (chartdefine.y1op == 1) {
			cop = "sum";
		} else if (chartdefine.y1op == 2) {
			cop = "avg";
		} else {
			cop = "";
		}
		if (dm!=null && dm.getRowCount()>0 && chartdefine.y1op > 0 ) {
			calcdm = calcSumgroupDM(dm, chartdefine.x1column,
					chartdefine.y1column, cop, chartdefine.y2column,
					chartdefine.y3column);


		}
		
		if (calcdm!=null && chartdefine.sortcolumn.length() > 0) {
			String sortexpr = chartdefine.sortcolumn + ":";
			sortexpr += chartdefine.sortdesc ? "desc" : "asc";
			// 排序?
			try {
				calcdm.sort(sortexpr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		
		//维度值能有多少个?
		int xcount=0;
		if(calcdm!=null){
			xcount=calcdm.getRowCount();
		}
		if(chartdefine.maxrowcount>0){
			if(xcount>chartdefine.maxrowcount){
				xcount=chartdefine.maxrowcount;
			}
		}

		String xs[] = new String[xcount];
		double y1s[] = new double[xcount];
		double y2s[] = null;
		double y3s[] = null;

		boolean createexample=false;
		if(xs.length==0){
			createexample=true;
			xs=new String[]{"维度值1","维度值2","维度值3"};
			y1s=new double[]{10,20,35};
		}

		if (chartdefine.y2column != null && chartdefine.y2column.length() > 0) {
			y2s = new double[xcount];
			if(createexample){
				y2s=new double[]{5,10,15};
			}
		}
		if (chartdefine.y3column != null && chartdefine.y3column.length() > 0) {
			y3s = new double[xcount];
			if(createexample){
				y3s=new double[]{2,4,6};
			}
		}

		for (int row = 0; calcdm!=null && row < xcount; row++) {
			xs[row] = calcdm.getItemValue(row, chartdefine.x1column);
			try {
				y1s[row] = Double.parseDouble(calcdm.getItemValue(row,
						chartdefine.y1column));
			} catch (Exception e) {

			}

			try {
				if (chartdefine.y2column != null
						&& chartdefine.y2column.length() > 0) {

					y2s[row] = Double.parseDouble(calcdm.getItemValue(row,
							chartdefine.y2column));
				}
			} catch (Exception e) {

			}
			try {
				if (chartdefine.y3column != null
						&& chartdefine.y3column.length() > 0) {

					y3s[row] = Double.parseDouble(calcdm.getItemValue(row,
							chartdefine.y3column));
				}
			} catch (Exception e) {

			}
		}

		Chartvalues result=new Chartvalues();
		result.xs=xs;
		result.y1s=y1s;
		result.y2s=y2s;
		result.y3s=y3s;
		return result;
	}

	 BarSeries createBarserial(Chartdefine chartdefine,
			NumberDataSet values, String serialname) {
		BarSeries bs = (BarSeries) BarSeriesImpl.create();
		bs.setDataSet(values);
		if (chartdefine.showdata == 0) {
			bs.getLabel().setVisible(false);
		} else {
			bs.getLabel().setVisible(true);
			if (chartdefine.showdata == 1) {
				bs.setLabelPosition(Position.INSIDE_LITERAL);
			} else {
				bs.setLabelPosition(Position.OUTSIDE_LITERAL);
			}
		}
		bs.setSeriesIdentifier(serialname);
		return bs;
	}

	/**
	 * 生成一个合计dm
	 * 
	 * @param dm
	 * @param xcolname
	 * @param y1colname
	 * @param op1
	 * @return
	 */
	 DBTableModel calcSumgroupDM(DBTableModel dm, String xcolname,
			String y1colname, String op1, String y2colname, String y3colname) {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		cols.add(dm.getColumninfo(xcolname));
		cols.add(dm.getColumninfo(y1colname));
		boolean by2 = y2colname != null && y2colname.length() > 0;
		boolean by3 = y3colname != null && y3colname.length() > 0;
		if (by2) {
			cols.add(dm.getColumninfo(y2colname));

		}
		if (by3) {
			cols.add(dm.getColumninfo(y3colname));

		}
		DBColumnDisplayInfo ctcol = new DBColumnDisplayInfo("_bi_count1",
				"number");
		cols.add(ctcol);
		ctcol = new DBColumnDisplayInfo("_bi_count2", "number");
		cols.add(ctcol);
		ctcol = new DBColumnDisplayInfo("_bi_count3", "number");
		cols.add(ctcol);

		DBTableModel groupdm = new DBTableModel(cols);

		/**
		 * 维度值和行号的关系.
		 */
		HashMap<String,Integer> xvaluerowmap=new HashMap<String, Integer>();
		
		int xcolumnindex=dm.getColumnindex(xcolname);
		int y1columnindex=dm.getColumnindex(y1colname);
		int y2columnindex=0,y3columnindex=0;
		if(by2){
			y2columnindex=dm.getColumnindex(y2colname);
		}
		if(by3){
			y3columnindex=dm.getColumnindex(y3colname);
		}

		int gxcolumnindex=groupdm.getColumnindex(xcolname);
		int gy1columnindex=groupdm.getColumnindex(y1colname);
		int gy2columnindex=0,gy3columnindex=0;
		if(by2){
			gy2columnindex=groupdm.getColumnindex(y2colname);
		}
		if(by3){
			gy3columnindex=groupdm.getColumnindex(y3colname);
		}

		for (int r = 0; r < dm.getRowCount() ; r++) {
			String x = dm.getItemValue(r, xcolumnindex);
			String y1 = dm.getItemValue(r, y1columnindex);
			String y2;
			String y3;

			// 找列.
			int targetr ;
			Integer irow=xvaluerowmap.get(x);
			if(irow==null){
				targetr=-1;
			}else{
				targetr=irow.intValue();
			}
			
			if (targetr < 0) {
				targetr = groupdm.getRowCount();
				groupdm.appendRow();
				xvaluerowmap.put(x, new Integer(targetr));
			}
			String sum = groupdm.getItemValue(targetr, gy1columnindex);
			sum = DecimalHelper.add(sum, y1, 10);
			groupdm.setItemValue(targetr, gxcolumnindex, x);
			groupdm.setItemValue(targetr, gy1columnindex, sum);

			// 计数器加1
			String ct = groupdm.getItemValue(targetr, "_bi_count1");
			ct = DecimalHelper.add(ct, "1", 0);
			groupdm.setItemValue(targetr, "_bi_count1", ct);

			if (by2) {
				sum = groupdm.getItemValue(targetr, gy2columnindex);
				y2 = dm.getItemValue(r, y2columnindex);
				sum = DecimalHelper.add(sum, y2, 10);
				groupdm.setItemValue(targetr, gy2columnindex, sum);

				// 计数器加1
				ct = groupdm.getItemValue(targetr, "_bi_count2");
				ct = DecimalHelper.add(ct, "1", 0);
				groupdm.setItemValue(targetr, "_bi_count2", ct);
			}

			if (by3) {
				sum = groupdm.getItemValue(targetr, gy3columnindex);
				y3 = dm.getItemValue(r, y3columnindex);
				sum = DecimalHelper.add(sum, y3, 10);
				groupdm.setItemValue(targetr, gy3columnindex, sum);

				// 计数器加1
				ct = groupdm.getItemValue(targetr, "_bi_count3");
				ct = DecimalHelper.add(ct, "1", 0);
				groupdm.setItemValue(targetr, "_bi_count3", ct);
			}

		}

		// 平均?
		if (op1.equals("avg") ) {
			for (int r = 0; r < groupdm.getRowCount(); r++) {
				String sum = groupdm.getItemValue(r, y1colname);
				String ct = groupdm.getItemValue(r, "_bi_count1");
				String avg = DecimalHelper.divide(sum, ct, 10);
				groupdm.setItemValue(r, y1colname, avg);
			}
		}
		if (op1.equals("avg") && by2) {
			for (int r = 0; r < groupdm.getRowCount(); r++) {
				String sum = groupdm.getItemValue(r, y2colname);
				String ct = groupdm.getItemValue(r, "_bi_count2");
				String avg = DecimalHelper.divide(sum, ct, 10);
				groupdm.setItemValue(r, y1colname, avg);
			}
		}
		if (op1.equals("avg") && by3) {
			for (int r = 0; r < groupdm.getRowCount(); r++) {
				String sum = groupdm.getItemValue(r, y3colname);
				String ct = groupdm.getItemValue(r, "_bi_count3");
				String avg = DecimalHelper.divide(sum, ct, 10);
				groupdm.setItemValue(r, y1colname, avg);
			}
		}

		return groupdm;

	}

	 void toswtFont(java.awt.Font font, FontDefinition swtfont) {
		swtfont.setName(font.getName());
		swtfont.setSize(font.getSize());
		if ((font.getStyle() & Font.BOLD) != 0) {
			swtfont.setBold(true);
		} else {
			swtfont.setBold(false);
		}
		if ((font.getStyle() & Font.ITALIC) != 0) {
			swtfont.setItalic(true);
		} else {
			swtfont.setItalic(false);
		}
	}

	
	/**
	 * 生成饼
	 * @param chartdefine
	 * @param dm
	 * @return
	 */
	private  Chart createPieChart(Chartdefine chartdefine) {
		ChartWithoutAxes cwoaPie = ChartWithoutAxesImpl.create( );

		if (chartdefine.dimension == 1) {
			// 2D
			cwoaPie.setDimension(ChartDimension.TWO_DIMENSIONAL_LITERAL);
		} else if (chartdefine.dimension == 0) {
			// 2D有阴影
			cwoaPie
					.setDimension(ChartDimension.TWO_DIMENSIONAL_WITH_DEPTH_LITERAL);
		} else if (chartdefine.dimension == 2) {
			// 3D
			cwoaPie.setDimension(ChartDimension.THREE_DIMENSIONAL_LITERAL);
		} else {
			cwoaPie.setDimension(ChartDimension.TWO_DIMENSIONAL_LITERAL);
		}

		// Plot
		cwoaPie.getBlock().setBackground(ColorDefinitionImpl.WHITE());

		cwoaPie.getBlock().getOutline().setVisible(true);

		Plot p = cwoaPie.getPlot();
		p.getClientArea().setBackground(
				ColorDefinitionImpl.create(255, 255, 255));
		p.getOutline().setVisible(false);

		// Title
		cwoaPie.getTitle().getLabel().getCaption().setValue(chartdefine.getTitlevalue());
		toswtFont(chartdefine.titlefont, cwoaPie.getTitle().getLabel()
				.getCaption().getFont());

		// Legend
		Legend lg = cwoaPie.getLegend();
		if (chartdefine.showlegend) {
			lg.setVisible(true);
			toswtFont(chartdefine.legendfont, lg.getText().getFont());
/*			if (chartdefine.colortype == 0) {
				// 按系列分颜色
				lg.setItemType(LegendItemType.SERIES_LITERAL);
			} else {
				// 按维度分
				lg.setItemType(LegendItemType.CATEGORIES_LITERAL);
			}
*/
			//肯定按维度分
			lg.setItemType(LegendItemType.CATEGORIES_LITERAL);

		
		}else{
			lg.setVisible(false);
		}

		String xs[]=currentvalues.xs;
		double y1s[]=currentvalues.y1s;
		double y2s[]=currentvalues.y2s;
		double y3s[]=currentvalues.y3s;

		// 加入x
		SeriesDefinition sdX = SeriesDefinitionImpl.create();
		sdX.getSeriesPalette().shift(0);
		

		Series seCategory = SeriesImpl.create();
		TextDataSet xValues = TextDataSetImpl.create(xs);
		seCategory.setDataSet(xValues);
		
		sdX.getSeries().add(seCategory);
		// Y-Series
		NumberDataSet y1Values = NumberDataSetImpl.create(y1s);
		PieSeries pieseries1 =(PieSeries)PieSeriesImpl.create();
		pieseries1.setDataSet(y1Values);
		toswtFont(chartdefine.y1font, pieseries1.getLabel().getCaption().getFont());
		toswtFont(chartdefine.x1font, pieseries1.getTitle().getCaption().getFont());
		pieseries1.setSeriesIdentifier(chartdefine.getY1Titlevalue());

		SeriesDefinition sdy = SeriesDefinitionImpl.create( );
		sdy.getSeries().add(pieseries1);
		sdX.getSeriesDefinitions().add(sdy);

		cwoaPie.getSeriesDefinitions().add(sdX);
		return cwoaPie;
	}

}
