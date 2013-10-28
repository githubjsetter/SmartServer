package com.smart.workflow.common;

import java.util.Enumeration;
import java.util.Vector;

/**
 * 结点的级.一个级下有多个结点.
 * @author user
 *
 */
public class WfnodeStage {
	/**
	 * 结点
	 */
	Vector<Wfnodedefine> nodes=new Vector<Wfnodedefine>();
	
	/**
	 * 返回状态
	 * @return 1 通过 0不通过
	 */
	public int calcStatus(){
		return 0;
	}
	
	/**
	 * 返回结点
	 * @return
	 */
	public Enumeration<Wfnodedefine> getNodes(){
		return nodes.elements();
	}
}
