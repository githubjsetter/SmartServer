package com.inca.npserver.clientinstall;

import java.util.ArrayList;

public interface DownloadNotifyIF {
	/**
	 * ֪ͨ���ؽ��ȡ�
	 * @param downloadedsize �����ֽ�
	 * @param usetimems ����ʱ��ms
	 */
	void notify(int totalsize,int downloadedsize,long usetimems);
	void notify(ArrayList<Blockinfo> blocks);
	void log(String logmsg);
}
