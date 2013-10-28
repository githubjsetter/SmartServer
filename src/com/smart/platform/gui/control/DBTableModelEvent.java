package com.smart.platform.gui.control;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-16
 * Time: 13:36:42
 * To change this template use File | Settings | File Templates.
 */
public interface DBTableModelEvent {
    void retrieveStart(DBTableModel dbmodel);
    /**
     * 查询完成一部分
     * @param dbmodel
     * @return 返回非0,中止查询
     */
    int retrievePart(DBTableModel dbmodel,int startrow,int endrow,int retrivedsize,int inflatesize);

    /**
     * 查询全部完成
     * @param dbmodel
     */
    void retrieveFinish(DBTableModel dbmodel);


    /**
     * 出错了
     * @param dbmodel
     * @param errormessage
     */
    void retrieveError(DBTableModel dbmodel,String errormessage);
}
