package com.smart.bi.client.design;

import java.awt.Font;

public class Chartdefine {
	public static String[] charttypes={
		"柱图","饼图"/*"线","区域","饼","仪表","点","曲线","圆柱","圆锥"*/
	};
	public static String[] dimensions={"2D立体","2D平面"};
	public static String[] columnops={"列值","列值求和","列值平均"};
	public static String[] colortypes={"按系列着色","按维度着色"};
	public static String[] showdatas={"不显示图形上数字","显示在图形内","显示在图形外"};
	
	static Font defaultfont=new Font("宋体",Font.PLAIN,9);
	private BIReportdsDefine dsdefine=null;
	public int dsdefineindex=0;
	
	public int charttype=0;
	public int dimension=0;
	
	public String title="";
	
	/**
	 * 最多返回行数
	 */
	public int maxrowcount=0;
	/**
	 * 维度列名
	 */
	public String x1column="";

	/**
	 * X轴标题
	 */
	public String xtitle="";

	/**
	 * 维度字体旅转
	 */
	public int x1fontrotation=0;
	public String ytitle="\"金额(单位:元)\"";
	public int yfontrotation=90;
	
	/**
	 * Y坐标轴的字体
	 */
	public Font yLabelfont=defaultfont;

	public String y1column="";
	public String y1title="";
	public int 	y1op=0;
	public String y2column="";
	public String y2title="";
	public String y3column="";
	public String y3title="";
	public boolean showlegend=true;
	
	public Font titlefont=defaultfont;
	/**
	 * X轴字体
	 */
	public Font xfont=defaultfont;
	/**
	 * 维度字体
	 */
	public Font x1font=defaultfont;
	public Font yfont=defaultfont;
	public Font y1font=defaultfont;
	public Font y2xfont=defaultfont;
	public Font y3font=defaultfont;
	public Font legendfont=defaultfont;
	
	public String sortcolumn="";
	public boolean sortdesc=false;
	public int colortype=0;
	public int showdata=0;
	
	private BICellCalcer calcer=null;
	
	public BICellCalcer getCalcer() {
		return calcer;
	}
	public void setCalcer(BICellCalcer calcer) {
		this.calcer = calcer;
	}
	public BIReportdsDefine getDsdefine() {
		return dsdefine;
	}
	public void setDsdefine(BIReportdsDefine dsdefine) {
		this.dsdefine = dsdefine;
	}
	
	/**
	 * 返回title现值
	 * @return
	 */
	public String getTitlevalue(){
		return calcExpr(title);
	}
	public String getXTitlevalue(){
		return calcExpr(xtitle);
	}
	public String getYTitlevalue(){
		return calcExpr(ytitle);
	}
	public String getY1Titlevalue(){
		return calcExpr(y1title);
	}
	public String getY2Titlevalue(){
		return calcExpr(y2title);
	}
	public String getY3Titlevalue(){
		return calcExpr(y3title);
	}
	
	
	String calcExpr(String expr){
		String s="";
		try {
			s=calcer.calc(0, expr);
		} catch (Exception e) {
			s=e.getMessage();
		}
		return s;
	}
}
