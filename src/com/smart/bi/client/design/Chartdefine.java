package com.smart.bi.client.design;

import java.awt.Font;

public class Chartdefine {
	public static String[] charttypes={
		"��ͼ","��ͼ"/*"��","����","��","�Ǳ�","��","����","Բ��","Բ׶"*/
	};
	public static String[] dimensions={"2D����","2Dƽ��"};
	public static String[] columnops={"��ֵ","��ֵ���","��ֵƽ��"};
	public static String[] colortypes={"��ϵ����ɫ","��ά����ɫ"};
	public static String[] showdatas={"����ʾͼ��������","��ʾ��ͼ����","��ʾ��ͼ����"};
	
	static Font defaultfont=new Font("����",Font.PLAIN,9);
	private BIReportdsDefine dsdefine=null;
	public int dsdefineindex=0;
	
	public int charttype=0;
	public int dimension=0;
	
	public String title="";
	
	/**
	 * ��෵������
	 */
	public int maxrowcount=0;
	/**
	 * ά������
	 */
	public String x1column="";

	/**
	 * X�����
	 */
	public String xtitle="";

	/**
	 * ά��������ת
	 */
	public int x1fontrotation=0;
	public String ytitle="\"���(��λ:Ԫ)\"";
	public int yfontrotation=90;
	
	/**
	 * Y�����������
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
	 * X������
	 */
	public Font xfont=defaultfont;
	/**
	 * ά������
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
	 * ����title��ֵ
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
