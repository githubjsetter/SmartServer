package com.inca.np.print.drawable;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-5-10
 * Time: 10:20:47
 * To change this template use File | Settings | File Templates.
 */
public class PagelineInfo {
    private int startrow;
    private int endrow;
    private String[] columns=null;

    public PagelineInfo(int startrow, int endrow,String[] columns) {
        this.startrow = startrow;
        this.endrow = endrow;
        this.columns=columns;
    }

    public int getStartrow() {
        return startrow;
    }

    public int getEndrow() {
        return endrow;
    }

    public String[] getColumns() {
        return columns;
    }

    public void setColumns(String[] columns) {
        this.columns = columns;
    }
}
