package com.inca.np.gui.event;

import java.awt.event.ActionEvent;

/**
 * @deprecated

 * User: Administrator
 * Date: 2007-3-31
 * Time: 13:28:47
 * Frame事件接口
 */
public interface FrameEventIF {

    //////////////////GET/////////////////////////
    int getdbStatus(int row);
    int getRowcount();

    //////////////////SET/////////////////////////
    void setFormfieldvalue(int row,String colname,String value);

    //////////////////do action/////////////////////////
    void actionPerformed(ActionEvent e);

/*
    void doNew();
    void doNextRow();
    void doPriorRow();
    void doFirstRow();
    void doLastRow();
    void doSave();
*/
    void doSelectTabbedpane(int index);

    //////////////////fire event/////////////////////////
    void on_Tablerowchanged(int currow,int lastrow);


    void on_doubleclick(int currow);
}
