package com.smart.bi.client.design;

public interface ReportApIF {

	String FORBID_EXPORT="forbidexport";
	String FORBID_PRINT="forbidprint";
	String REPORT_SQL="reportsql";
	//是否禁止导出
	boolean isForbidExport();
	//是否禁止打印
	boolean isForbidPrint();
	//查询sql设置
	String getSelectSql();

}
