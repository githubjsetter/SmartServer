package com.inca.np.print.drawable;

import com.inca.np.gui.control.DBTableModel;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-5-11
 * Time: 15:17:07
 * 只设置一个标题
 */
public class DefaultPageHead extends PageHeadFoot{
    String title;
    Font font=new Font("宋体",Font.BOLD,20);

    public DefaultPageHead(PReport report, String title) {
        super(report);
        this.title = title;
        this.height = 40;

        PLabelCell labelcell = new PLabelCell(report,"\""+title+"\"");
        labelcell.setX(40);
        labelcell.setY(3);
        labelcell.setWidth(300);
        labelcell.setHeight(30);
        labelcell.setFont(font);

        addLabelcell(labelcell);

    }


/*
    public void paint(Graphics2D g2, DBTableModel dbmodel, DBTableModel masterdbmodel, int startrow,
                      int endrow, String[] pagecolumns) {
        g2.setFont(font);
        g2.setColor(Color.BLACK);
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D rect = fm.getStringBounds(title,g2);

        int x= (int)(( (double)width - rect.getWidth() ) /2.0 + 0.5);
        g2.drawString(title,x,(int)(height -10 +0.5));
    }
*/
}
