package com.smart.platform.gui.ste;

import com.smart.platform.gui.control.DBTableModel;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-12
 * Time: 9:53:34
 * To change this template use File | Settings | File Templates.
 */
public class CSteModelListenerAdaptor implements CSteModelListener{
	public int on_actionPerformed(String command) {
		//缺省-1表示没有处理
		return -1;
	}

	public int on_checkrow(int row, DBTableModel model) {
        return 0;  
    }

    public void on_tablerowchanged(int newrow, int newcol, int oldrow, int oldcol) {
        
    }


    public void on_itemvaluechange(int row, String colname, String value) {
    }

    public void on_retrieved() {
    }

    public int on_retrievepart() {
    	return 0;
    }

    public void on_doubleclick(int row, int col) {
        
    }

    public void on_click(int row, int col) {
        
    }

    public void on_rclick(int row, int col) {
        
    }

    public int on_beforequery() {
        return 0;  
    }

    public int on_beforenew() {
        return 0;  
    }

    public void on_new(int row) {
        
    }

    public int on_beforedel(int row) {
        return 0;  
    }

    public void on_del(int row) {
        
    }

    public int on_beforesave() {
        return 0;  
    }

    public void on_save() {
        return ;
    }

    public int on_beforeundo() {
        return 0;  
    }

    public void on_undo() {
        
    }

    public int on_beforemodify(int row) {
        return 0;  
    }

    public void on_modify(int row) {
        
    }

	public void on_retrievestart() {
		
		
	}

	public void on_saved(int errorct) {
		
		
	}

	public int on_beforeclose() {
		
		return 0;
	}

	public void on_close(){
		return;
	}
}
