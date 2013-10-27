package com.inca.np.gui.event;

import com.inca.np.gui.ste.*;


/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-3-31
 * Time: 13:27:54
 * 表格换行了
 */
public class TablerowchangedEvent extends EditorEvent{
    /**
     * 当前行
     */
    int currow=-1;

    /**
     * 原来的行
     */
    int lastrow=-1;

    public TablerowchangedEvent(int currow,int lastrow) {
        super("tablerowchanged");
        this.currow=currow;
        this.lastrow=lastrow;
    }

    public int getCurrow() {
        return currow;
    }

    public int getLastrow() {
        return lastrow;
    }
}
