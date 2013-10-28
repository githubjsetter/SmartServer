package com.smart.platform.print.drawable;

import com.smart.platform.gui.control.DBTableModel;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-5-11
 * Time: 15:44:57
 * To change this template use File | Settings | File Templates.
 */
public class DefaultPageFoot extends PageHeadFoot{
    public DefaultPageFoot(PReport report) {
        super(report);
        height=30;
    }

/*

    public void paint(Graphics2D g2, DBTableModel dbmodel, DBTableModel masterdbmodel, int startrow,
                      int endrow, String[] pagecolumns) {
        FontMetrics fm = g2.getFontMetrics();
        g2.setFont(font);
        g2.setColor(Color.BLACK);

        //显示inca
        String s="Power by INCA";
        Rectangle2D rect = fm.getStringBounds(s,g2);
        int x=(int)(width - rect.getWidth() - 10);
        g2.drawString(s,x,height - 4);


        //制表时间
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        s = "制表时间:" + df.format(new Date());
        rect = fm.getStringBounds(s,g2);
        x= (int)(x - rect.getWidth() - 20);
        g2.drawString(s,x,height - 4);


        //显示页码
        StringBuffer sb=new StringBuffer();
        sb.append("第"+(report.getPrintingpageno()+1)+"/"+report.getNumberOfPages()+"页");

        s=sb.toString();
        rect = fm.getStringBounds(s,g2);

        x= (int)(x - rect.getWidth() - 20);
        g2.drawString(s,x,height - 4);

    }
*/
}
