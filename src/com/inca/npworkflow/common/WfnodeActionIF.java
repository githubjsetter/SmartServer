package com.inca.npworkflow.common;

import java.sql.Connection;

/**
 * ���Ķ����ӿ�.
 * ֧������ӿڵ��඼���Թҵ����ִ��.
 * @author user
 *
 */
public interface WfnodeActionIF {
	
	/**
	 * ����ͨ��
	 */
	public static String ACTIONTYPE_PASS="pass";
	

	/**
	 * ������һ���
	 */
	public static String ACTIONTYPE_REFUSEPRIOR="refuseprior";
	
	/**
	 * ��������
	 */
	public static String ACTIONTYPE_REFUSEALL="refuseall";
	
	/**
	 * �˹�����
	 */
	public static String ACTIONTYPE_HUMAN="human";
	
	/**
	 * java���Զ�����
	 */
	public static String ACTIONTYPE_JAVA="java";

	/**
	 * ����update sql
	 */
	public static String ACTIONTYPE_UPDATESQL="updatesql";

	/**
	 * ���ܾ̾�ʱִ��update sql
	 */
	public static String ACTIONTYPE_REFUSE_UPDATESQL="rupdatesql";

	/**
	 * ���ܾ̾�ʱִ�е�java
	 */
	public static String ACTIONTYPE_REFUSE_JAVA="rjava";

	/**
	 * ��������
	 * @return
	 */
	String getActiontype();
	
	/**
	 * ��ɽ�㶯��.
	 * @param con
	 * @param wfinstance
	 * @param nodeinstance
	 * @throws Exception
	 */
	void process(Connection con,Wfinstance wfinstance ,Wfnodeinstance nodeinstance) throws Exception;
	
}
