package com.inca.npx.mde;

import java.util.Vector;

import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.npx.ste.ApIF;

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
