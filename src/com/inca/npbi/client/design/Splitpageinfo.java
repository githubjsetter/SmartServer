package com.inca.npbi.client.design;

public class Splitpageinfo {
	/**
	 * ���ݿ�ʼ��
	 */
	public int startrow;
	/**
	 * ���ݽ�����
	 */
	public int endrow;

	public int getRowcount() {
		return endrow - startrow + 1;
	}
}