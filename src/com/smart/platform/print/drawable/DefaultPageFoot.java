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

        //��ʾinca
        String s="Power by INCA";
        Rectangle2D rect = fm.getStringBounds(s,g2);
        int x=(int)(width - rect.getWidth() - 10);
        g2.drawString(s,x,height - 4);


        //�Ʊ�ʱ��
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        s = "�Ʊ�ʱ��:" + df.format(new Date());
        rect = fm.getStringBounds(s,g2);
        x= (int)(x - rect.getWidth() - 20);
        g2.drawString(s,x,height - 4);


        //��ʾҳ��
        StringBuffer sb=new StringBuffer();
        sb.append("��"+(report.getPrintingpageno()+1)+"/"+report.getNumberOfPages()+"ҳ");

        s=sb.toString();
        rect = fm.getStringBounds(s,g2);

        x= (int)(x - rect.getWidth() - 20);
        g2.drawString(s,x,height - 4);

    }
*/
}
