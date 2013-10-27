package com.inca.np.print.drawable;

import com.inca.np.gui.control.DBTableModel;
import com.inca.np.print.expr.ExprCalcer;
import com.inca.np.print.report.AccessableReport;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.io.BufferedInputStream;
import java.io.BufferedReader;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-5-9
 * Time: 19:38:32
 * 任意位置放置表达式
 */
public class PLabelCell extends PDrawcellBase{
    AccessableReport report=null;
    String expr="";


    public PLabelCell(AccessableReport report,String expr) {
        this.report=report;
        this.expr = expr;
    }

/*
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
*/

    public String getExpr() {
        return expr;
    }

    public void setExpr(String expr) {
        this.expr = expr;
    }

/*
    public void setFont(Font font) {
        this.font = font;
    }

    public Font getFont() {
        return font;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
*/
    public String calcExpr(){
        ExprCalcer exprcalc=new ExprCalcer(report);
        try {
            return exprcalc.calc(0,expr);
        } catch (Exception e) {
            return "表达式错误:"+e.getMessage();
        }
    }

    public void paint(Graphics2D g2,DBTableModel dbmodel,DBTableModel assistantdbmodel,int startrow,int endrow){
        //画数据
        g2.setFont(font);
        this.drawCell(g2,calcExpr(),x,y,width,height,align);
    }

    protected void writeOther(PrintWriter out) {
        out.println("expr="+expr);
    }

    protected void readOther(BufferedReader in) throws Exception{
        String line=in.readLine();
        expr=line.substring("expr=".length());
        return;
    }

}
