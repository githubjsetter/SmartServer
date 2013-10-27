package com.inca.np.gui.control;

import com.inca.np.gui.ste.Steform;
import com.inca.np.gui.ste.CSteModel;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;
import java.util.Enumeration;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-20
 * Time: 10:03:34
 * To change this template use File | Settings | File Templates.
 */
public class CFormFocusTraversalPolicy extends LayoutFocusTraversalPolicy {
    CSteModel stemodel = null;

    public CFormFocusTraversalPolicy(CSteModel stemodel) {
        this.stemodel = stemodel;
    }

    protected boolean accept(Component aComponent) {
        return super.accept(aComponent);
    }

    public Component getComponentAfter(Container aContainer, Component aComponent) {
        Vector<DBColumnDisplayInfo> displaycolumninfos = stemodel.getFormcolumndisplayinfos();
        int curindex = -1;
        for (int i = 0; i < displaycolumninfos.size(); i++) {
            DBColumnDisplayInfo dispinfo = displaycolumninfos.elementAt(i);
            if (dispinfo.getEditComponent() == aComponent) {
                curindex = i;
                break;
            }
        }
        if (curindex == -1) {
            return getFirstComponent(aContainer);
        }

        curindex++;
        if (curindex  >  displaycolumninfos.size() - 1) {
            curindex = 0;
        }

        for (int i = curindex; i < displaycolumninfos.size() ; i++) {
            DBColumnDisplayInfo dispinfo = displaycolumninfos.elementAt(i);
            if (dispinfo.getColtype().equals("行号")) {
                continue;
            }
            JComponent comp = dispinfo.getEditComponent();
            if (comp instanceof CFormatTextField) {
                if (((CFormatTextField) comp).isCanedit() && ((CFormatTextField) comp).isKeyfocusable()) {
                    return comp;
                }
            } else if (comp instanceof CTextField || comp instanceof CComboBox || comp instanceof CCheckBox) {
                if (comp.isEnabled()) {
                    return comp;
                }
            } else if (comp instanceof CTextArea){
            	CTextArea ctext=(CTextArea)comp;
            	JTextArea textarea=ctext.getTextarea();
            	if(ctext.isCanedit()){
            		return textarea;
            	}
            }
        }

        //如果只有一个编辑框可以focus，要触发一次focuslost
        Component comp = getFirstComponent(aContainer);
        if (comp instanceof CFormatTextField) {
            try {
                ((CFormatTextField) comp).commitEdit();
            } catch (ParseException e) {
            }
        }


        return comp;
    }

    public Component getComponentBefore(Container aContainer, Component aComponent) {
        Vector<DBColumnDisplayInfo> displaycolumninfos = stemodel.getDBtableModel().getDisplaycolumninfos();
        int curindex = -1;
        for (int i = displaycolumninfos.size() -1 ; i>=0 ;i--) {
            DBColumnDisplayInfo dispinfo = displaycolumninfos.elementAt(i);
            if (dispinfo.getEditComponent() == aComponent) {
                curindex = i;
                break;
            }
        }
        if (curindex == -1) {
            return getFirstComponent(aContainer);
        }

        curindex--;
        if (curindex <0 ) {
            curindex = displaycolumninfos.size() - 1;
        }

        for (int i = curindex; i>=0 ; i--) {
            DBColumnDisplayInfo dispinfo = displaycolumninfos.elementAt(i);
            if (dispinfo.getColtype().equals("行号")) {
                continue;
            }
            JComponent comp = dispinfo.getEditComponent();
            if (comp instanceof CFormatTextField) {
                if (((CFormatTextField) comp).isCanedit() && ((CFormatTextField) comp).isKeyfocusable()) {
                    return comp;
                }
            } else if (comp instanceof CTextField || comp instanceof CComboBox) {
                if (comp.isEnabled()) {
                    return comp;
                }
            }
        }
        return getLastComponent(aContainer);
    }

    public Component getFirstComponent(Container aContainer) {
        Vector<DBColumnDisplayInfo> displaycolumninfos = stemodel.getDBtableModel().getDisplaycolumninfos();
        Enumeration<DBColumnDisplayInfo> en = displaycolumninfos.elements();
        while (en.hasMoreElements()) {
            DBColumnDisplayInfo dispinfo = en.nextElement();
            if (dispinfo.getColtype().equals("行号")) {
                continue;
            }
            JComponent comp = dispinfo.getEditComponent();
            if (comp instanceof CFormatTextField) {
                if (((CFormatTextField) comp).isCanedit()) {
                    return comp;
                }
            } else if (comp instanceof CTextField || comp instanceof CComboBox) {
                if (comp.isEnabled()) {
                    return comp;
                }
            }
        }
        return super.getFirstComponent(aContainer);
    }

    public Component getLastComponent(Container aContainer) {
        Vector<DBColumnDisplayInfo> displaycolumninfos = stemodel.getDBtableModel().getDisplaycolumninfos();
        for (int i = displaycolumninfos.size() - 1; i >= 0; i--) {
            DBColumnDisplayInfo dispinfo = displaycolumninfos.elementAt(i);
            if (dispinfo.getColtype().equals("行号")) {
                continue;
            }
            JComponent comp = dispinfo.getEditComponent();
            if (comp instanceof CFormatTextField) {
                if (((CFormatTextField) comp).isCanedit()) {
                    return comp;
                }
            } else if (comp instanceof CTextField || comp instanceof CComboBox) {
                if (comp.isEnabled()) {
                    return comp;
                }
            }
        }
        return super.getLastComponent(aContainer);    //To change body of overridden methods use File | Settings | File Templates.
    }

}
