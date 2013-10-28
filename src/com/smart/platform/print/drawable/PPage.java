package com.smart.platform.print.drawable;

import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;

import javax.print.attribute.standard.MediaSize;

import java.awt.*;
import java.util.Vector;
import java.util.Enumeration;
import java.io.PrintWriter;
import java.io.BufferedReader;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-5-10
 * Time: 9:01:35
 * ҳ
 */
public class PPage {
    /**
     * ֽ��
     */
    int width=200;

    /**
     * ֽ��
     */
    int height=300;

    PReport report=null;

    /**
     * ҳü
     */
    PageHeadFoot phead=null;

    /**
     * ����
     */
    PageBody pbody=null;

    /**
     * ҳ��
     */
    PageHeadFoot pfoot=null;


    public PPage(PReport report) {
        this.report = report;
        this.phead=new DefaultPageHead(report,"");
        this.pfoot=new DefaultPageFoot(report);

        //ȱʡA4ֽ
        MediaSize A4 = MediaSize.ISO.A4;
        float[] size = A4.getSize(MediaSize.INCH);
        width = (int)(size[0]*72);
        height = (int)(size[1]*72);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setDefaultTitle(String title){
       phead=new DefaultPageHead(report,title);
    }

    public PageHeadFoot getPhead() {
        return phead;
    }

    public PageHeadFoot getPfoot() {
        return pfoot;
    }

    public PageBody getPbody() {
        return pbody;
    }

    /**
     * ��ӡһҳ
     * @param g2
     * @param dbmodel
     * @param assistantdbmodel
     * @param startrow
     * @param endrow
     */
    public void paint(Graphics2D g2,DBTableModel dbmodel,DBTableModel assistantdbmodel,int startrow,int endrow,
                      String[] pagecolumns){
        //ҳü
        int y=0;
        if(phead!=null){
            Graphics2D headg2 = (Graphics2D) g2.create(0,y,width,phead.getHeight());
            phead.setWidth(width);
            phead.paint(headg2,dbmodel,assistantdbmodel,startrow,endrow,pagecolumns);
            y+=phead.getHeight();
        }


        //����
        if(pbody!=null){
            Graphics2D bodyg2 = (Graphics2D) g2.create(0,y,width,pbody.getHeight());
            pbody.setWidth(width);
            pbody.paint(bodyg2,dbmodel,assistantdbmodel,startrow,endrow,pagecolumns);
            y+=pbody.getHeight();
        }

        //ҳ��
        if(pfoot!=null){
            Graphics2D footg2 = (Graphics2D) g2.create(0,y,width,pfoot.getHeight());

            //footg2.setColor(Color.BLACK);
            //footg2.drawLine(0,0,width,pfoot.getHeight());

            pfoot.setWidth(width);
            pfoot.paint(footg2,dbmodel,assistantdbmodel,startrow,endrow,pagecolumns);
            y+=pfoot.getHeight();
        }
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
        //����body��.
        int bodyheight=height;
        if(phead!=null){
            bodyheight -= phead.getHeight();
        }
        if(pfoot!=null){
            bodyheight -= pfoot.getHeight();
        }
        pbody.setHeight(bodyheight);
    }

    public void setPhead(PageHeadFoot phead) {
        this.phead = phead;
    }

    public void setPbody(PageBody pbody) {
        this.pbody = pbody;
    }

    public void setPfoot(PageHeadFoot pfoot) {
        this.pfoot = pfoot;
    }

    public Vector<PagelineInfo> splitpage(Graphics2D g2,DBTableModel dbmodel) {
        Vector<PagelineInfo> pagelines=new Vector<PagelineInfo>();
        int lineheight=pbody.getLineheight();
        if(pbody.isWithborder()){
            lineheight += pbody.getBorderwidth();
        }

        //�����ҳ
        Vector<String[]> pagecolumns = pbody.splitPagecolumns(width);

        //�����и�
        if(pbody.getDataline().getTitleheight()<10){
            int besttitleheight = calcBesttitleHeight(g2);
            pbody.setLineTitleheight(besttitleheight);
        }
        
        

        //ÿҳ������?
        int rowsperpage = (pbody.getHeight()-pbody.getLineTitleheight()) / lineheight;
        if(rowsperpage<=0)rowsperpage=1;


        //��ҳ
        int startrow=0;
        int endrow=0;
        while(true){
            endrow = startrow + rowsperpage - 1;
            int lastrow = dbmodel.getRowCount() - 1;
            endrow = endrow > lastrow ? lastrow : endrow;

            //���ǵ�һ�г��ȿ��ܳ���ֽ��,�����Ҫ������з�ҳ.
            Enumeration<String[]> en = pagecolumns.elements();
            while (en.hasMoreElements()) {
                String[] columns = en.nextElement();
                PagelineInfo pagelineInfo = new PagelineInfo(startrow,endrow,columns);
                pagelines.add(pagelineInfo);

            }


            startrow = endrow + 1;
            if(startrow>lastrow) {
                break;
            }
        }

        return pagelines;
    }

    /**
     * ��������и߶�
     * @param g2
     */
    int calcBesttitleHeight(Graphics2D g2) {
        return pbody.calcBesttitleHeight(g2);
    }

    /**
     * �����ļ�
     * @param out
     */
    public void writeReport(PrintWriter out) {
        if(phead!=null){
            phead.writeReport("pagehead",out);
        }

        if(pbody!=null){
            pbody.writeReport("pagebody",out);
        }

        if(pfoot!=null){
            pfoot.writeReport("pagefoot",out);
        }
    }


    public void readReport(BufferedReader in) throws Exception{
        String line;
        while((line=in.readLine())!=null){
            if(line.trim().startsWith("<pagehead>")){
                phead=new PageHeadFoot(report);
                phead.readReport("pagehead",in);
            }else if(line.startsWith("<pagebody")){
                pbody=new PageBody(report);
                pbody.readReport("pagebody",in);
            }else if(line.startsWith("<pagefoot")){
                pfoot=new PageHeadFoot(report);
                pfoot.readReport("pagefoot",in);
            }
        }
    }
    
    public String getSortexpr(){
        return pbody.getSortexpr();
    }
    public void setSortexpr(String s){
    	pbody.setSortexpr(s);
    }
    
}
