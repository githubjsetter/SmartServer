package com.smart.bi.client.design;

public interface ReportApIF {

	String FORBID_EXPORT="forbidexport";
	String FORBID_PRINT="forbidprint";
	String REPORT_SQL="reportsql";
	//�Ƿ��ֹ����
	boolean isForbidExport();
	//�Ƿ��ֹ��ӡ
	boolean isForbidPrint();
	//��ѯsql����
	String getSelectSql();

}
