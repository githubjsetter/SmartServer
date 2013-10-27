package com.inca.npbi.client.design;

import com.inca.np.gui.control.DBTableModel;

/**
 * 报表表达式计算用的数据原
 * @author user
 *
 */
public interface ReportcalcerDatasourceIF {
	DBTableModel getDbmodel();
	int getCurrow();
	int getPrintingpageno();
	int getPagecount();
	Splitpageinfo getPageinfo(int pageno);
	/**
	 * 取参数.
	 * @return
	 */
	String getParameter(String pname);
}
