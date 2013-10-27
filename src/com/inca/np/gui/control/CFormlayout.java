package com.inca.np.gui.control;

import java.awt.*;
import java.util.HashMap;

import javax.swing.JLabel;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-3-30
 * Time: 9:43:45
 * 按顺序从左到右排列元件，遇到约束FormlineBreak换行。
 * 元件大小取preferredsize。
 */
public class CFormlayout implements LayoutManager2{
    HashMap linebreakmap=new HashMap();

    int colspace=0;
    int linespace=0;

    public CFormlayout(int colspace, int linespace) {
        this.colspace = colspace;
        this.linespace = linespace;
    }

    public int getColspace() {
        return colspace;
    }

    public void setColspace(int colspace) {
        this.colspace = colspace;
    }

    public int getLinespace() {
        return linespace;
    }

    public void setLinespace(int linespace) {
        this.linespace = linespace;
    }

    /////////////////////      LayoutManager2 ///////////////////////////
    public void addLayoutComponent(Component comp, Object constraints) {
        if(constraints == null || !(constraints instanceof CFormlineBreak)){
            return;
        }
        linebreakmap.put(comp,constraints);
    }

    public Dimension maximumLayoutSize(Container target) {
        return preferredLayoutSize(target);
    }

    public float getLayoutAlignmentX(Container target) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public float getLayoutAlignmentY(Container target) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void invalidateLayout(Container target) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /////////////////////      LayoutManager ///////////////////////////
    public void addLayoutComponent(String name, Component comp) {
        addLayoutComponent(comp,null);
    }

    public void removeLayoutComponent(Component comp) {
        linebreakmap.remove(comp);
    }

    /**
     * 计算大小。每个元件都取preferred大小
     * @param parent
     * @return
     */
    public Dimension preferredLayoutSize(Container parent) {
        checkCompent(parent);

        double maxwidth=0;
        double maxheight=0;

        int compct = parent.getComponentCount();
        double linewidth=0;
        double lineheight=0;
        for(int i=0;i<compct;i++){

            Component comp = parent.getComponent(i);
            if(!comp.isVisible())continue;
            Dimension preferredSize = comp.getPreferredSize();
            linewidth+=colspace;
            linewidth+= preferredSize.getWidth();
            lineheight=Math.max(preferredSize.getHeight(),lineheight);

            Object constraint = linebreakmap.get(comp);
            if(constraint instanceof CFormlineBreak){
                //该换行了。
                maxwidth = Math.max(maxwidth,linewidth);
                linewidth=0;
                maxheight += linespace + lineheight;
                linewidth=0;
                lineheight=0;
            }
        }

        maxwidth += colspace;
        maxheight += linespace;
        
        //加滚动条的高
        maxheight += 10;

        return new Dimension((int)(maxwidth+0.5),(int)(maxheight+0.5));
    }

    public Dimension minimumLayoutSize(Container parent) {
        return preferredLayoutSize(parent);
    }


    /**
     * 排列元件
     * @param parent
     */
    public void layoutContainer(Container parent) {
        checkCompent(parent);



        int rowct=linebreakmap.size();
        double lineheights[]=new double[rowct];


        int compct = parent.getComponentCount();
        int row=0;
        double lineheight=0;
        for(int i=0;i<compct;i++){
            Component comp = parent.getComponent(i);
            Dimension preferredSize = comp.getPreferredSize();
            lineheight = Math.max(preferredSize.getHeight(),lineheight);

            Object constraint = linebreakmap.get(comp);
            if(constraint instanceof CFormlineBreak){
                //该换行了。
                lineheights[row++] = lineheight;
                lineheight=0;
            }
        }


        double x=0;
        double y=0;
        row=0;
        lineheight=0;
        for(int i=0;i<compct;i++){
            Component comp = parent.getComponent(i);
            if(!comp.isVisible())continue;
            Dimension preferredSize = comp.getPreferredSize();
            
            Dimension rsize=comp.getSize();
            //System.out.println(comp.getClass().getName()+","+rsize.width);
            
            if(comp instanceof JLabel){
            	JLabel lbtext=(JLabel)comp;
            	if(lbtext.getText().length()==0){
            		continue;
            	}
            }
            
            x+=(double)colspace;
            lineheight = Math.max(preferredSize.getHeight(),lineheight);
            double thisy = (lineheights[row] -  preferredSize.getHeight())/2.0 + y;
            //System.out.println(comp.getClass()+" .x="+x);
            comp.setBounds((int)(x+0.5),(int)(thisy+0.5),(int)preferredSize.getWidth(),(int)preferredSize.getHeight());
            
            rsize=comp.getSize();
            //System.out.println(comp.getClass().getName()+","+rsize.width);
            x += rsize.getWidth();

            Object constraint = linebreakmap.get(comp);
            if(constraint instanceof CFormlineBreak){
                //该换行了。
                y+=linespace + lineheight;
                x=0;
                lineheight=0;
                row ++;
            }
        }
    }

    protected void checkCompent(Container parent) {
        int ct=parent.getComponentCount();
        if(ct==0)return;

        Component lastcomp = parent.getComponent(ct-1);
        if(linebreakmap.get(lastcomp) == null){
            linebreakmap.put(lastcomp,new CFormlineBreak());
        }

    }

}
