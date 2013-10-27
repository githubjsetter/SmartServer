package com.inca.np.server;

import com.inca.np.auth.Userruninfo;
import com.inca.np.gui.control.DBTableModel;

import java.sql.Connection;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-7-5 Time: 16:29:29
 * �ܵ�ϸĿ����ӿ�
 */
public interface MdesaveIF extends StesaveIF{
    /**
	 * �ܵ�һ����¼����ǰ
	 * 
	 * @param con
	 * @param dbmodel
	 *            �ܵ�������
	 * @param row
	 *            �к�
	 * @throws Exception
	 */
    void on_beforesavemaster(Connection con,Userruninfo userruninfo,DBTableModel dbmodel,int row)throws Exception;

    /**
	 * �ܵ�һ����¼�����
	 * 
	 * @param con
	 * @param savedmasterdbmodel
	 *            ������ܵ�����Դ
	 * @param masterrow
	 *            �ܵ����к�
	 * @param saveddetaildbmodel
	 *            �����ܵ���Ӧ�ı������ϸ������Դ��������¼
	 * @throws Exception
	 */
    void on_aftersavemaster(Connection con,Userruninfo userruninfo,DBTableModel savedmasterdbmodel,int masterrow,
                            DBTableModel saveddetaildbmodel)throws Exception;
}
