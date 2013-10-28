package com.smart.server.clientinstall;

public class Blockinfo {
	/**
	 * 执行下载任务的thread. -1表示未分配。99999表示下载完成
	 */
	private int threadindex = 0;
	private int filestartpos;
	private int filesize;
	private boolean lock = false;

	public Blockinfo(int pos, int filesize) {
		this.filestartpos = pos;
		this.filesize = filesize;
	}

	public int getThreadindex() {
		return threadindex;
	}

	public void setThreadindex(int threadindex) {
		this.threadindex = threadindex;
	}

	public void resetFilestartpos(int pos, int size) {
		this.filestartpos = pos;
		this.filesize = size;
	}

	public int getFilestartpos() {
		return filestartpos;
	}

	public void setFinished() {
		finished=true;
	}

	boolean finished=false;
	public boolean isFinished() {
		return finished;
	}

	public int getFilesize() {
		return filesize;
	}

	public boolean isLock() {
		return lock;
	}

	public void lock() {
		this.lock = true;
	}

	public void releasLock() {
		this.lock = false;
	}

}
