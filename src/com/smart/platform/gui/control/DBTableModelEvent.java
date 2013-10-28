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
     * ��ѯ���һ����
     * @param dbmodel
     * @return ���ط�0,��ֹ��ѯ
     */
    int retrievePart(DBTableModel dbmodel,int startrow,int endrow,int retrivedsize,int inflatesize);

    /**
     * ��ѯȫ�����
     * @param dbmodel
     */
    void retrieveFinish(DBTableModel dbmodel);


    /**
     * ������
     * @param dbmodel
     * @param errormessage
     */
    void retrieveError(DBTableModel dbmodel,String errormessage);
}
