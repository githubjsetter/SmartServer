package com.inca.npserver.clientinstall;

import java.util.ArrayList;

public interface DownloadNotifyIF {
	/**
	 * 通知下载进度。
	 * @param downloadedsize 下载字节
	 * @param usetimems 下载时间ms
	 */
	void notify(int totalsize,int downloadedsize,long usetimems);
	void notify(ArrayList<Blockinfo> blocks);
	void log(String logmsg);
}
