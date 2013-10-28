package com.smart.server.pushplat.common;

/**
 * 显示push信息的接口
 * @author user
 *
 */
public interface PushshowIF {
	/**
	 * 增加显示pushinfo
	 * @param pushinfo
	 */
	void appendPushinfo(Pushinfo pushinfo);
	
	/**
	 * 清除显示
	 */
	void clear();
	
	/**
	 * 已显示的push数量
	 * @return
	 */
	int getPushcount();
	
	void updateStatus(String msg);
	
	/**
	 * 取间隔时间
	 * @return
	 */
	int getMinute();
	
	boolean isNotifystartimmediate();

	void resetNotifystartimmediate();
}
