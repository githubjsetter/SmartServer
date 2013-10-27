package com.inca.np.gui.ste;

import com.inca.np.gui.control.DBTableModel;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-12
 * Time: 9:38:13
 * ¼àÌýCSteModelµÄÏûÏ¢
 */
public interface CSteModelListener {
	int on_actionPerformed(String command);
    int on_checkrow(int row, DBTableModel model);
    void on_tablerowchanged(int newrow,int newcol,int oldrow,int oldcol);
    void on_itemvaluechange(int row, String colname, String value);
    void on_retrievestart();
    int on_retrievepart();
    void on_retrieved();
    void on_doubleclick(int row, int col);
    void on_click(int row, int col);
    void on_rclick(int row, int col);


    int on_beforequery();
    int on_beforenew();
    void on_new(int row);
    int on_beforedel(int row);
    void on_del(int row);
    int on_beforesave();
    void on_save();
    int on_beforeundo();
    void on_undo();

    int on_beforemodify(int row);
    void on_modify(int row);
	void on_saved(int errorct);
	int on_beforeclose() ;
	void on_close() ;

}
