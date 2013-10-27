package com.inca.npx.ste;

import java.util.Vector;

import com.inca.np.gui.control.DBColumnDisplayInfo;

/**
 * ����Ap�Ľӿ�
 * 
 * @author Administrator
 * 
 */
public interface ApIF {

	/**
	 * ������Ȩ����,�ܷ�����
	 * 
	 * @return true ����
	 */
	public boolean isDevelopCannew();

	/**
	 * ������Ȩ����,�ܷ�ɾ��
	 * 
	 * @return true ����
	 */
	public boolean isDevelopCandelete();

	/**
	 * ������Ȩ����,�ܷ��ѯ
	 * 
	 * @return true ����
	 */
	public boolean isDevelopCanquery();

	/**
	 * ������Ȩ����,�ܷ��޸�
	 * 
	 * @return true ����
	 */
	public boolean isDevelopCanmodify();

	/**
	 * ������Ȩ����,�ܷ񱣴�
	 * 
	 * @return true ����
	 */
	public boolean isDevelopCansave();

	/**
	 * ȡ��Ȩ���Զ���
	 * 
	 * @param apname
	 * @return Apinfo
	 */
	public Apinfo getApinfo(String apname);

	/**
	 * ȡ��Ȩ����ֵ
	 * 
	 * @param apname
	 * @return
	 */
	public String getApvalue(String apname);

	/**
	 * ���ؿ����õĲ������͵���Ȩ���ԵĶ���
	 * 
	 * @return
	 */
	public Vector<Apinfo> getParamapinfos();

	/**
	 * ��������֧�ֵĴ�ӡ����
	 * 
	 * @return
	 */
	public Vector<String> getPrintplans();

	/**
	 * ��������֧�ֵĴ�ӡ����
	 * 
	 * @param plans
	 */
	public void setPrintplans(Vector<String> plans);

	/**
	 * �����Զ���ӡ������
	 * 
	 * @return
	 */
	public String getAutoprintplan();

	/**
	 * �����Զ���ӡ����
	 * 
	 * @param planname
	 * @return
	 */
	public void setAutoprintplan(String planname);
	
	
	public Vector<DBColumnDisplayInfo> getDBColumnDisplayInfos();

	public Vector<DBColumnDisplayInfo> loadOrgDBmodeldefine();

}
