package com.smart.platform.server;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.gui.control.DBTableModel;

import java.sql.Connection;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-7-5
 * Time: 16:17:05
 * To change this template use File | Settings | File Templates.
 */
public interface StesaveIF {
    /**
     * 单表编辑保存前，或总单细单表保存前
     * @param con
     * @param dbmodel 数据源
     * @param row     行号
     * @throws Exception
     */
    void on_beforesave(Connection con,Userruninfo userrininfo,DBTableModel dbmodel,int row)throws Exception;

    /**
     * 单表编辑保存后，或总单细目保存后
     * @param con
     * @param saveddbmodel 保存的记录dbmodel
     * @param row          行号，应该为0
     * @throws Exception
     */
    void on_aftersave(Connection con,Userruninfo userrininfo,DBTableModel saveddbmodel,int row)throws Exception;
}
