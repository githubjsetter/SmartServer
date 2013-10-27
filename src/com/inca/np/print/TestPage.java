package com.inca.np.print;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.print.PageFormat;
import java.awt.print.Paper;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-24
 * Time: 11:03:27
 * Í¼´óÐ¡200x200
 */
public class TestPage extends PageFormat {

    double w=200;
    double h=200;

    public TestPage() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * Ö½¿í
     * @return
     */
    public double getWidth() {
        return  w / 72.0 / 1.0;
    }

    public double getHeight() {
        return h / 72.0 / 1.0;
    }

    public double getImageableX() {
        return 0;
    }

    public double getImageableY() {
        return 0;
    }

    public double getImageableWidth() {
        return w/1;
    }

    public double getImageableHeight() {
        return h/1;
    }

    public Paper getPaper() {
        return super.getPaper();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void setPaper(Paper paper) {
        super.setPaper(paper);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void setOrientation(int orientation) throws IllegalArgumentException {
        super.setOrientation(orientation);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public int getOrientation() {
        return super.getOrientation();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public double[] getMatrix() {
        return super.getMatrix();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
