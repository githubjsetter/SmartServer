package com.inca.npworkflow.common;

import java.util.Enumeration;
import java.util.Vector;

/**
 * 结点级的实例
 * @author user
 *
 */
public class Wfstageinstance {
	Vector<Wfnodeinstance> nodeinstances=new Vector<Wfnodeinstance>();
	
	/**
	 * 取结点实例
	 * @return
	 */
	public Enumeration<Wfnodeinstance> getNodeinstance(){
		return nodeinstances.elements();
	}
}
