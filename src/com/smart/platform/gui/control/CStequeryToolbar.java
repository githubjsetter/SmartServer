package com.smart.platform.gui.control;

import com.smart.platform.gui.ste.CSteModel;

import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-8-9
 * Time: 11:15:11
 ²éÑ¯×¨ÓÃ
 */
public class CStequeryToolbar extends CStetoolbar{
    public CStequeryToolbar(ActionListener l) {
        super(l);
    }

    static String[] notneeds={
            CSteModel.ACTION_DEL,
            CSteModel.ACTION_EXPORT,
            CSteModel.ACTION_MODIFY,
            CSteModel.ACTION_NEW,
            CSteModel.ACTION_SAVE,
            CSteModel.ACTION_UNDO
    };
    protected boolean isUsebutton(String actionname) {
        for(int i=0;i<notneeds.length;i++){
            if(actionname.equals(notneeds[i])){
                return false;
            }
        }
        return super.isUsebutton(actionname);
    }
}
