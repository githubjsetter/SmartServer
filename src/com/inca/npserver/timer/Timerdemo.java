package com.inca.npserver.timer;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Category;

import com.inca.np.gui.control.DBTableModel;
import com.inca.np.util.SelectHelper;

public class Timerdemo extends TimerAdapter{
	Category logger=Category.getInstance(Timerdemo.class);
	public void onTimer(){
		Connection con = null;
		try {
			con = getConnection();
			String sql="select to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') strdate from dual";
			SelectHelper sh=new SelectHelper(sql);
			DBTableModel dm=sh.executeSelect(con, 0, 1);
			String sysdate=dm.getItemValue(0, "strdate");
			logger.info("定时执行1,数据库服务器时间是:"+sysdate);

		} catch (Exception e) {
			logger.error("Error", e);
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	/**
	 * 每60秒执行一次
	 */
	public long getSecond() {
		return 60L * 1;
	}

	/**
	 * 循环执行
	 */
	public String getType() {
		return ServertimerIF.TYPE_LOOP;
	}

	public String getName() {
		return "服务器定时任务演示";
	}
}
