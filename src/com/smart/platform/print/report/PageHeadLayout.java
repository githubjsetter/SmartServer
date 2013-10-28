package com.smart.platform.print.report;

import com.smart.platform.print.drawable.PLabelCell;
import com.smart.platform.print.drawable.PageEntryBase;
import com.smart.platform.print.drawable.PageHeadFoot;

import javax.swing.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-5-24
 * Time: 14:21:13
 * To change this template use File | Settings | File Templates.
 */
public class PageHeadLayout implements LayoutManager2{
    HashMap<JComponent,PLabelCell> compmap=null;
    public PageHeadLayout(HashMap<JComponent,PLabelCell> compmap) {
        this.compmap = compmap;
    }

    public void addLayoutComponent(Component comp, Object constraints) {
    }

    public Dimension maximumLayoutSize(Container target) {
        return preferredLayoutSize(target);
    }

    public float getLayoutAlignmentX(Container target) {
        return 0;
    }

    public float getLayoutAlignmentY(Container target) {
        return 0;
    }

    public void invalidateLayout(Container target) {
    }

    public void addLayoutComponent(String name, Component comp) {
    }

    public void removeLayoutComponent(Component comp) {
    }

    public Dimension preferredLayoutSize(Container parent) {
        //放置元件
        int maxw=0;
        int maxh=0;
        Iterator<JComponent> it = compmap.keySet().iterator();
        while (it.hasNext()) {
            JComponent comp = it.next();
            PLabelCell cell = compmap.get(comp);
            int x = cell.getX() +cell.getWidth();
            if(x>maxw){
                maxw=x;
            }
            int y=cell.getY() +cell.getHeight();
            if(y>maxh){
                maxh=y;
            }
        }

        return new Dimension(maxw,maxh);
    }

    public Dimension minimumLayoutSize(Container parent) {
        return preferredLayoutSize(parent);
    }

    public void layoutContainer(Container parent) {
        //放置元件
        Iterator<JComponent> it = compmap.keySet().iterator();
        while (it.hasNext()) {
            JComponent comp = it.next();
            PLabelCell cell = compmap.get(comp);
            comp.setBounds(cell.getX(),cell.getY(),cell.getWidth(),cell.getHeight());
        }
    }
}
