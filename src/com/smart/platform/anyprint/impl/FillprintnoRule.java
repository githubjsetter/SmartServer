package com.smart.platform.anyprint.impl;

public class FillprintnoRule extends DataprocRule{

	/**
	 * 每页一个打印票据号，由外部序列号产生。
	 * 表达式包括
	 * 外部序列号ID
	 * 表名，反填字段名，主键列名, 主数据源列名
	 * 
	 */
	public FillprintnoRule() {
		super(RULETYPE_FILLPRINTNO);
	}


}
