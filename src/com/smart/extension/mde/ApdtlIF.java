package com.smart.extension.mde;

import java.util.Vector;

import com.smart.extension.ste.ApIF;
import com.smart.platform.gui.control.DBColumnDisplayInfo;

public interface ApdtlIF extends ApIF{
	
	/**
	 * 开发授权属性,能否新增细单
	 * @return true 可以
	 */
	public boolean isDevelopCannewdtl();
	/**
	 * 开发授权属性,能否删除细单
	 * @return true 可以
	 */
	public boolean isDevelopCandeletedtl();
	/**
	 * 开发授权属性,能否修改细单
	 * @return true 可以
	 */
	public boolean isDevelopCanmodifydtl();
	
	public Vector<DBColumnDisplayInfo> getDtlDBColumnDisplayInfos();
	public Vector<DBColumnDisplayInfo> loadDtlOrgDBColumnDisplayInfos();
	

}
