package com.smart.server.pushplat.common;

/**
 * ��ʾpush��Ϣ�Ľӿ�
 * @author user
 *
 */
public interface PushshowIF {
	/**
	 * ������ʾpushinfo
	 * @param pushinfo
	 */
	void appendPushinfo(Pushinfo pushinfo);
	
	/**
	 * �����ʾ
	 */
	void clear();
	
	/**
	 * ����ʾ��push����
	 * @return
	 */
	int getPushcount();
	
	void updateStatus(String msg);
	
	/**
	 * ȡ���ʱ��
	 * @return
	 */
	int getMinute();
	
	boolean isNotifystartimmediate();

	void resetNotifystartimmediate();
}
