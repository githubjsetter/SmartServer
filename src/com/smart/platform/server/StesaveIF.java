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
     * ����༭����ǰ�����ܵ�ϸ������ǰ
     * @param con
     * @param dbmodel ����Դ
     * @param row     �к�
     * @throws Exception
     */
    void on_beforesave(Connection con,Userruninfo userrininfo,DBTableModel dbmodel,int row)throws Exception;

    /**
     * ����༭����󣬻��ܵ�ϸĿ�����
     * @param con
     * @param saveddbmodel ����ļ�¼dbmodel
     * @param row          �кţ�Ӧ��Ϊ0
     * @throws Exception
     */
    void on_aftersave(Connection con,Userruninfo userrininfo,DBTableModel saveddbmodel,int row)throws Exception;
}
