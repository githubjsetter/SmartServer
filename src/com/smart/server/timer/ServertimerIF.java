package com.smart.server.timer;

/**
 * 服务器定时调用类的接口.
 * @author user
 *
 */
public interface ServertimerIF {
	public static String TYPE_EVERYDAY="每天执行一次";
	public static String TYPE_LOOP="循环执行";
	
	/**
	 * 返回名称
	 * @return
	 */
	String getName();
	
	/**
	 * 调用的定时函数
	 */
	void onTimer();
	
	/**
	 * 返回类型. TYPE_EVERYDAY | TYPE_LOOP
	 * @return
	 * 如果返回TYPE_EVERYDAY, getSecond()返回在一天中的秒数,开始执行.如
	 * 每天20点执行,返回 20 x 3600
	 * 
	 * 如果返回TYPE_LOOP, 循环执行,两次之间延时getSecond()秒.
	 */
	String getType();
	
	/**
	 * 返回秒数
	 * 如果getType()返回TYPE_EVERYDAY, getSecond()返回在一天中的秒数,开始执行.如
	 * 每天20点执行,返回 20 x 3600
	 * 
	 * 如果getType()返回TYPE_LOOP, 循环执行,两次之间延时getSecond()秒.
	 * @return
	 */
	long getSecond();
	
}
