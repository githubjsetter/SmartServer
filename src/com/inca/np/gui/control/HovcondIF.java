package com.inca.np.gui.control;

/**
 * hov��ѯ����if
 * @author Administrator
 *
 */
public interface HovcondIF {
	/**
	 * ����HOV��ѯwhere����
	 * @param colname
	 * @return
	 */
	String getHovOtherWheres(String colname);
	
	/**
	 * �ɳ���������HOV
	 * @param colname
	 */
	void invokeMultimdehov(String colname);
	/**
	 * �Ƿ��ܴ���HOV
	 * @return
	 */
	boolean canInvokehov(String colname);
}
