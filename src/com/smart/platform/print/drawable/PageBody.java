package com.smart.platform.print.drawable;

import com.smart.platform.gui.control.DBTableModel;

import java.util.Vector;
import java.util.Enumeration;
import java.util.ArrayList;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-5-9
 * Time: 18:26:58
 * 页身
 */
public class PageBody extends PageEntryBase{
    public PageBody(PReport report) {
        super(report);
        height=600;
    }


    public int getLineheight() {
        return dataline.getHeight();
    }

    public int getLineTitleheight(){
        return dataline.getTitleheight();
    }

    public void setLineTitleheight(int h){
        dataline.setTitleheight(h);
    }

    public boolean isWithborder(){
        return dataline.isWithborder();
    }

    public int getBorderwidth(){
        return dataline.getBorderwidth();
    }



    /**
     * 横向进行分页
     * @param pagewidth  每页允许最大宽
     * @return
     */
    public Vector<String[]> splitPagecolumns(int pagewidth){
        int maxwidth = pagewidth - (dataline.getX() << 1);
        Vector<String[]> pagecolumns=new Vector<String[]>();

        int colindex=0;
        while(true){
            ArrayList<String> ar=new ArrayList<String>();
            int curwidth=getFreezeWidth(ar);
            Enumeration<PColumnCell> en = dataline.getColumns().elements();
            int p;
            int addedcolct=0;
            for(p=colindex;p<dataline.getColumns().size();p++) {
                PColumnCell columnCell = dataline.getColumns().get(p);
                if(!columnCell.isVisible()) {
                    colindex=p+1;
                	continue;
                }
                if(columnCell.isFreeze()){
                    colindex=p+1;
                    continue;
                }
                int colwidth=columnCell.getWidth();
                if(dataline.isWithborder()){
                   colwidth+=dataline.getBorderwidth();
                }

                if(colwidth + curwidth <=maxwidth || addedcolct==0){
                    ar.add(columnCell.getColname());
                    curwidth += colwidth;
                    addedcolct++;
                    colindex=p+1;
                }else{
                    break;
                }
            }

            //分好一个横向页
            String names[]=new String[ar.size()];
            ar.toArray(names);
            pagecolumns.add(names);

            if(colindex>=dataline.getColumns().size()){
                break;
            }

        }
        return pagecolumns;
    }

    private int getFreezeWidth(ArrayList ar){
        int curwidth=0;
        Enumeration<PColumnCell> en = dataline.getColumns().elements();
        while (en.hasMoreElements()) {
            PColumnCell columnCell = en.nextElement();
            if(!columnCell.isVisible()) continue;
            if(columnCell.isFreeze()){
                ar.add(columnCell.getColname());
                curwidth+=columnCell.getWidth();
                if(dataline.isWithborder()){
                   curwidth+=dataline.getBorderwidth();
                }
            }
        }
        return curwidth;
    }


    public int calcBesttitleHeight(Graphics2D g2) {
       return dataline.calcBesttitleHeight(g2);
    }
    
    public String getSortexpr(){
        return dataline.getSortexpr();
    }
    public void setSortexpr(String s){
    	dataline.setSortexpr(s);
    }
}
