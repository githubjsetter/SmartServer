package com.inca.npbi.client.design;

import com.inca.np.gui.control.DBTableModel;

/**
 * ������ʽ�����õ�����ԭ
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
	 * ȡ����.
	 * @return
	 */
	String getParameter(String pname);
}
