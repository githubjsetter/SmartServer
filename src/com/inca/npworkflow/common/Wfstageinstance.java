package com.inca.npworkflow.common;

import java.util.Enumeration;
import java.util.Vector;

/**
 * ��㼶��ʵ��
 * @author user
 *
 */
public class Wfstageinstance {
	Vector<Wfnodeinstance> nodeinstances=new Vector<Wfnodeinstance>();
	
	/**
	 * ȡ���ʵ��
	 * @return
	 */
	public Enumeration<Wfnodeinstance> getNodeinstance(){
		return nodeinstances.elements();
	}
}
