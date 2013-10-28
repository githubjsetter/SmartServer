package com.smart.platform.gui.ui;

import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import com.smart.platform.gui.control.CTable;

import java.awt.*;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-7-10
 * Time: 15:45:11
 * To change this template use File | Settings | File Templates.
 */
public class CTableheadUI extends BasicTableHeaderUI{
    public void installUI(JComponent c) {
        super.installUI(c);
    }

    public void paint(Graphics g, JComponent c) {

        super.paint(g, c);
        JTableHeader th=(JTableHeader)c;
        TableColumnModel cm = th.getColumnModel();
        int w=0;
        for(int i=0;i<cm.getColumnCount();i++){
            w+=cm.getColumn(i).getWidth();
        }
/*        if(c instanceof JTableHeader){
        	JTable table=th.getTable();
        	if(table.isFocusOwner()){
            	paint3Deffect((Graphics2D)g,c,w,true,true) ;
        	}
        }else{
        	paint3Deffect((Graphics2D)g,c,w,true,true) ;
        }
*/
    	paint3Deffect((Graphics2D)g,c,w,true,true) ;
    }

    /** Top Top Color - white 128       the higher the ligher   */
    static public final Color COL_1TOP = new Color(255, 255, 255,128);
    /** End Top Color - white 0         */
    static public final Color COL_1END = new Color(255, 255, 255,0);
    /** Top End Color - black 0         */
    static public final Color COL_2TOP = new Color(0, 0, 0, 0);
    /** End End Color - black 64        the higher the darker   */
    static public final Color COL_2END = new Color(0, 0, 0, 64);

    public static void paint3Deffect (Graphics2D g2D, JComponent c, int width,boolean round, boolean out)
    {
        // paint upper gradient
        GradientPaint topPaint = null;
        if (out)
            topPaint = new GradientPaint(0,0, COL_1TOP, 0,c.getHeight()/2, COL_1END);
        else
            topPaint = new GradientPaint(0,0, COL_2END, 0,c.getHeight()/2, COL_2TOP);
        g2D.setPaint(topPaint);
        //
        RectangularShape topRec = null;
        if (round)
            topRec = new RoundRectangle2D.Float(0, 0, /*c.getWidth()*/ width,c.getHeight()/2, 15,15);
        else
            topRec = new Rectangle(0,0, width,c.getHeight()/2);
        g2D.fill(topRec);

        // paint lower gradient
        GradientPaint endPaint = null;
        if (out)
            endPaint = new GradientPaint(0, c.getHeight()/2, COL_2TOP, 0,c.getHeight(), COL_2END);
        else
            endPaint = new GradientPaint(0, c.getHeight()/2, COL_1END, 0,c.getHeight(), COL_1TOP);
        g2D.setPaint(endPaint);
        //
        RectangularShape endRec = null;
        if (round)
            endRec = new RoundRectangle2D.Float(0, c.getHeight()/2, width,c.getHeight()/2, 15,15);
        else
            endRec = new Rectangle(0, c.getHeight()/2, width, c.getHeight()/2);
        g2D.fill(endRec);
    }   //  paint3Deffect

}

