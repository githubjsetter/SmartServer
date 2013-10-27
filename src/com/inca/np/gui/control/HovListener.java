package com.inca.np.gui.control;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-22
 * Time: 11:08:08
 * To change this template use File | Settings | File Templates.
 */
public interface HovListener {
    void on_hov(DBColumnDisplayInfo dispinfo,DBTableModel result);
    void gainFocus(DBColumnDisplayInfo dispinfo);
    void lostFocus(DBColumnDisplayInfo dispinfo);
}
