package com.inca.np.gui.control;

/**
 * hov查询条件if
 * @author Administrator
 *
 */
public interface HovcondIF {
	/**
	 * 返回HOV查询where条件
	 * @param colname
	 * @return
	 */
	String getHovOtherWheres(String colname);
	
	/**
	 * 由程序来调用HOV
	 * @param colname
	 */
	void invokeMultimdehov(String colname);
	/**
	 * 是否能触发HOV
	 * @return
	 */
	boolean canInvokehov(String colname);
}
