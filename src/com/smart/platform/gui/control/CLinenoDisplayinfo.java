package com.smart.platform.gui.control;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-3
 * Time: 14:31:29
 * To change this template use File | Settings | File Templates.
 */
public class CLinenoDisplayinfo extends DBColumnDisplayInfo{
    public CLinenoDisplayinfo() {
        super("ÐÐºÅ","ÐÐºÅ","ÐÐºÅ");
        queryable=false;
        dbcolumn=false;
    }

    public void placeOnForm(JPanel parent, CFormlayout layout) {
        return;
    }

	@Override
	public boolean isDbcolumn() {
		return false;
	}

	@Override
	public boolean isQueryable() {
		return false;
	}

	@Override
	public boolean isUpdateable() {
		return false;
	}
    
    
}
