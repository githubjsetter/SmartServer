package com.inca.npworkflow.common;

import java.sql.Connection;

/**
 * 结点的动作接口.
 * 支持这个接口的类都可以挂到结点执行.
 * @author user
 *
 */
public interface WfnodeActionIF {
	
	/**
	 * 审批通过
	 */
	public static String ACTIONTYPE_PASS="pass";
	

	/**
	 * 销审上一结点
	 */
	public static String ACTIONTYPE_REFUSEPRIOR="refuseprior";
	
	/**
	 * 销审所有
	 */
	public static String ACTIONTYPE_REFUSEALL="refuseall";
	
	/**
	 * 人工操作
	 */
	public static String ACTIONTYPE_HUMAN="human";
	
	/**
	 * java类自动操作
	 */
	public static String ACTIONTYPE_JAVA="java";

	/**
	 * 运行update sql
	 */
	public static String ACTIONTYPE_UPDATESQL="updatesql";

	/**
	 * 流程拒绝时执行update sql
	 */
	public static String ACTIONTYPE_REFUSE_UPDATESQL="rupdatesql";

	/**
	 * 流程拒绝时执行的java
	 */
	public static String ACTIONTYPE_REFUSE_JAVA="rjava";

	/**
	 * 返回类型
	 * @return
	 */
	String getActiontype();
	
	/**
	 * 完成结点动作.
	 * @param con
	 * @param wfinstance
	 * @param nodeinstance
	 * @throws Exception
	 */
	void process(Connection con,Wfinstance wfinstance ,Wfnodeinstance nodeinstance) throws Exception;
	
}
