package com.smart.workflow.common;

import java.util.Enumeration;
import java.util.Vector;

/**
 * ���ļ�.һ�������ж�����.
 * @author user
 *
 */
public class WfnodeStage {
	/**
	 * ���
	 */
	Vector<Wfnodedefine> nodes=new Vector<Wfnodedefine>();
	
	/**
	 * ����״̬
	 * @return 1 ͨ�� 0��ͨ��
	 */
	public int calcStatus(){
		return 0;
	}
	
	/**
	 * ���ؽ��
	 * @return
	 */
	public Enumeration<Wfnodedefine> getNodes(){
		return nodes.elements();
	}
}
