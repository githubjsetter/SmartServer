package com.smart.extension.mde;

import java.util.Vector;

import com.smart.extension.ste.ApIF;
import com.smart.platform.gui.control.DBColumnDisplayInfo;

public interface ApdtlIF extends ApIF{
	
	/**
	 * ������Ȩ����,�ܷ�����ϸ��
	 * @return true ����
	 */
	public boolean isDevelopCannewdtl();
	/**
	 * ������Ȩ����,�ܷ�ɾ��ϸ��
	 * @return true ����
	 */
	public boolean isDevelopCandeletedtl();
	/**
	 * ������Ȩ����,�ܷ��޸�ϸ��
	 * @return true ����
	 */
	public boolean isDevelopCanmodifydtl();
	
	public Vector<DBColumnDisplayInfo> getDtlDBColumnDisplayInfos();
	public Vector<DBColumnDisplayInfo> loadDtlOrgDBColumnDisplayInfos();
	

}
