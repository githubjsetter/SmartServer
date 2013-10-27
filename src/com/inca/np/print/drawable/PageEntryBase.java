package com.inca.np.print.drawable;

import com.inca.np.gui.control.DBTableModel;

import java.util.Vector;
import java.util.Enumeration;
import java.awt.*;
import java.io.PrintWriter;
import java.io.BufferedReader;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-5-9
 * Time: 18:13:56
 * pagehead pagebody pagetail的基类
 */
public class PageEntryBase {
    /**
     * 任意位置的字串
     */
    Vector<PLabelCell> labelcells = new Vector<PLabelCell>();

    /**
     * 数据行.
     */
    PDataline dataline = null;

    int width=800;
    int height=70;

    PReport report=null;

    public PageEntryBase(PReport report) {
        this.report = report;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public PDataline getDataline() {
        return dataline;
    }

    public void paint(Graphics2D g2, DBTableModel dbmodel, DBTableModel assistantdbmodel, int startrow, int endrow,
                      String[] pagecolumns) {
        if (labelcells != null) {
            Enumeration<PLabelCell> en = labelcells.elements();
            while (en.hasMoreElements()) {
                PLabelCell cell = en.nextElement();
                cell.paint(g2, dbmodel, assistantdbmodel, startrow, endrow);
            }
        }

        if(dataline!=null){
            dataline.paint(g2, dbmodel, assistantdbmodel, startrow, endrow,pagecolumns);
        }
    }

    public void setDataline(PDataline dataline) {
        this.dataline = dataline;
    }

    public void addLabelcell(PLabelCell cell) {
        labelcells.add(cell);
    }

    public void setDatalineColumns(Vector<PColumnCell> columns){
        dataline.setColumns(columns);
    }

    public Vector<PLabelCell> getLabelcells() {
        return labelcells;
    }

    /**
     *　输出到文件
     * @param out
     */
    public void writeReport(String headfootflag,PrintWriter out) {
        out.println("<"+headfootflag+">");

        out.println("height="+height);

        Enumeration<PLabelCell> en = labelcells.elements();
        while (en.hasMoreElements()) {
            PLabelCell labelCell = en.nextElement();
            labelCell.writeReport(out);
        }

        if(dataline!=null){
            dataline.writeReport(out);
        }

        out.println("</"+headfootflag+">");
    }

    public void readReport(String headfootflag,BufferedReader in)throws Exception{



        String line=null;

        while((line=in.readLine())!=null){
            int p=line.indexOf("height=");
            if(p>=0){
                height=Integer.parseInt(line.substring("height=".length()));
            }

            if(line.startsWith("</"+headfootflag)){
                break;
            }

            if(line.startsWith("<cell>")){
                PLabelCell cell=new PLabelCell(report,"");
                labelcells.add(cell);
                cell.read(in);
            }

            if(line.startsWith("<dataline>")){
                dataline=new PDataline();
                dataline.readReport(in);
            }
        }
    }
    
	public void removeCell(PLabelCell cell) {
		labelcells.remove(cell);
	}

}
