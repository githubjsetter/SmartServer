package com.smart.platform.print;

import java.awt.print.Printable;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.Pageable;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-24
 * Time: 10:51:59
 * To change this template use File | Settings | File Templates.
 */
public class TestPrintDoc implements Printable,Pageable{

    /**
     * Õº∆¨200x200,ªªÀ„≥…1/72”¢¥Á‘Ÿ≥˝“‘∂˛
     * @param graphics
     * @param pageFormat
     * @param pageIndex
     * @return
     * @throws PrinterException
     */

    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if(pageIndex>0){
            return  Printable.NO_SUCH_PAGE;
        }
        Graphics2D g2=(Graphics2D)graphics;
        g2.translate(0,0);
        
        g2.drawLine(0,0,200,200);
        g2.drawString("≤‚ ‘¥Ú”°",100,100);
        return PAGE_EXISTS;
    }

    public int getNumberOfPages() {
        return 1;
    }

    public PageFormat getPageFormat(int pageIndex) throws IndexOutOfBoundsException {
        System.out.println("TestPrintDoc getPageFormat");
        return new TestPage();
    }

    public Printable getPrintable(int pageIndex) throws IndexOutOfBoundsException {
        return this;
    }
}
