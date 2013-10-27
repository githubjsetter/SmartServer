package com.inca.np.server;

import com.inca.np.auth.Userruninfo;
import com.inca.np.gui.control.DBTableModel;

import java.sql.Connection;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-7-5 Time: 16:29:29
 * 总单细目保存接口
 */
public interface MdesaveIF extends StesaveIF{
    /**
	 * 总单一条记录保存前
	 * 
	 * @param con
	 * @param dbmodel
	 *            总单数据行
	 * @param row
	 *            行号
	 * @throws Exception
	 */
    void on_beforesavemaster(Connection con,Userruninfo userruninfo,DBTableModel dbmodel,int row)throws Exception;

    /**
	 * 总单一条记录保存后
	 * 
	 * @param con
	 * @param savedmasterdbmodel
	 *            保存的总单数据源
	 * @param masterrow
	 *            总单的行号
	 * @param saveddetaildbmodel
	 *            这条总单对应的保存过的细单数据源，多条记录
	 * @throws Exception
	 */
    void on_aftersavemaster(Connection con,Userruninfo userruninfo,DBTableModel savedmasterdbmodel,int masterrow,
                            DBTableModel saveddetaildbmodel)throws Exception;
}
