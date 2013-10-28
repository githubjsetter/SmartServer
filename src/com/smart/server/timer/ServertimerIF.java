package com.smart.server.timer;

/**
 * ��������ʱ������Ľӿ�.
 * @author user
 *
 */
public interface ServertimerIF {
	public static String TYPE_EVERYDAY="ÿ��ִ��һ��";
	public static String TYPE_LOOP="ѭ��ִ��";
	
	/**
	 * ��������
	 * @return
	 */
	String getName();
	
	/**
	 * ���õĶ�ʱ����
	 */
	void onTimer();
	
	/**
	 * ��������. TYPE_EVERYDAY | TYPE_LOOP
	 * @return
	 * �������TYPE_EVERYDAY, getSecond()������һ���е�����,��ʼִ��.��
	 * ÿ��20��ִ��,���� 20 x 3600
	 * 
	 * �������TYPE_LOOP, ѭ��ִ��,����֮����ʱgetSecond()��.
	 */
	String getType();
	
	/**
	 * ��������
	 * ���getType()����TYPE_EVERYDAY, getSecond()������һ���е�����,��ʼִ��.��
	 * ÿ��20��ִ��,���� 20 x 3600
	 * 
	 * ���getType()����TYPE_LOOP, ѭ��ִ��,����֮����ʱgetSecond()��.
	 * @return
	 */
	long getSecond();
	
}
