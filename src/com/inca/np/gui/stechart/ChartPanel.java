package com.inca.np.gui.stechart;

import org.jfree.chart.JFreeChart;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-7-31
 * Time: 15:33:21
 * 专门画报表图形的JPanel
 */
public class ChartPanel extends JPanel {
    JFreeChart chart=null;


    public JFreeChart getChart() {
        return chart;
    }

    public void setChart(JFreeChart chart) {
        this.chart = chart;
    }


    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2=(Graphics2D)g;

        if(chart!=null){
            Dimension size = getPreferredSize();
            Rectangle rect=new Rectangle(0,0,(int)size.getWidth(),(int)size.getHeight());
            chart.draw(g2,rect);
        }
    }
}
