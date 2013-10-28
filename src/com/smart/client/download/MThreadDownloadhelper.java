package com.smart.client.download;

import java.awt.Frame;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.apache.log4j.Category;

import com.smart.platform.communicate.BinfileCommand;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.NVPair;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.util.DefaultNPParam;
import com.smart.platform.util.SendHelper;
import com.smart.platform.util.StringUtil;
import com.smart.server.clientinstall.Blockinfo;
import com.smart.server.clientinstall.DownloadNotifyIF;

/**
 * 多线程下载
 * 
 * @author Administrator
 * 
 */
public class MThreadDownloadhelper {
	Category logger = Category.getInstance(MThreadDownloadhelper.class);
	String reqcommand;
	ParamCommand otherparamcmd;
	private boolean result = false;
	MthreadDownloadDlg notifydlg = null;

	public boolean download(Frame frm, String title, String reqcommand,
			ParamCommand otherparamcmd, File outdir) {
		this.reqcommand = reqcommand;
		this.otherparamcmd = otherparamcmd;
		this.outdir = outdir;

		notifydlg = new MthreadDownloadDlg(frm, title);
		notifydlg.pack();
		this.notifyer = notifydlg;

		Runnable r = new Runnable() {
			public void run() {
				result = doDownload();
			}
		};
		Thread t = new Thread(r);
		t.start();

		notifydlg.setVisible(true);
		notifydlg.dispose();
		notifydlg = null;
		return result;
	}

	String errormessage="";
	
	/**
	 * 多线程下载
	 * 
	 * @param reqcommand
	 * @param pcmd
	 * @param outfile
	 * @return
	 * @throws Exception
	 */
	protected boolean doDownload() {
		totaldownloadsize = 0;
		// 先下载得到大小。再分配。
		boolean connectok = false;
		filelength = 0;
		starttime = System.currentTimeMillis();

		for (int i = 0; i < maxretrycount; i++) {
			int respcode = -1;
			String respmsg = "";
			try {
				int startpos = 0;
				ServerResponse resp = sendRequest(startpos);
				if (!resp.getCommand().startsWith("+OK")){
					errormessage=resp.getCommand();
					continue;
				}
				errormessage="";
				ParamCommand resppcmd = (ParamCommand) resp.commandAt(1);
				String filename = resppcmd.getValue("filename");
				downloadfile = new File(outdir, filename);
				downloadfile.getParentFile().mkdirs();
				int len = Integer.parseInt(resppcmd.getValue("length"));
				filelength = (int) Long.parseLong(resppcmd
						.getValue("totallength"));

				log("filelength=" + filelength);
				BinfileCommand bfc = (BinfileCommand) resp.commandAt(2);
				createFile(startpos, bfc.getBindata(), len);
				notifyDownload(len);

				log("use blocksize=" + blocksize);
				connectok = true;
				break;
			} catch (Exception e) {
				log(respcode + " " + respmsg + " " + e.getMessage());
				// logger.error("error", e);
				// JOptionPane.showMessageDialog(null,e.getMessage());
			}
		}
		if (!connectok) {
			result=false;
			if (notifydlg != null) {
				notifydlg.setVisible(false);
			}
				errormessage+="连接失败";
			return false;
		}

		// 按blocksize产生。
		blocks = new ArrayList<Blockinfo>();
		int p;
		for (p = totaldownloadsize; p < filelength; p += blocksize) {
			int size = filelength - p;
			if (size > blocksize)
				size = blocksize;
			Blockinfo info = new Blockinfo(p, size);
			blocks.add(info);
		}

		log("blocks count=" + blocks.size());
		if(blocks.size()==0){
			result=true;
			log("下载完成");
			if (notifydlg != null) {
				notifydlg.setVisible(false);
			}
			return true;
		}
		synchronized (blocks) {
			// 生成线程
			workers = new Vector<Downloadworker>();
			for (int i = 0; i < MAXTHREAD; i++) {
				Downloadworker worker = null;
				worker = new Downloadworker(i + 1);
				workers.add(worker);
			}
			// 分平均分配。
			int blockcountperthread = blocks.size() / MAXTHREAD;
			if (blockcountperthread == 0) {
				// 一个就够了
				blockcountperthread = 1;
			}
			int index = 0;
			Enumeration<Downloadworker> en1 = workers.elements();
			while (en1.hasMoreElements() && index < blocks.size()) {
				Downloadworker worker = en1.nextElement();
				for (int k = 0; k < blockcountperthread; k++) {
					if (index + k > blocks.size() - 1)
						break;
					blocks.get(index + k).setThreadindex(
							worker.getThreadindex());
				}
				index += blockcountperthread;
			}
			// 最后的零头由最后一个处理
			Downloadworker lasterworker = workers.lastElement();
			for (; index < blocks.size(); index++) {
				blocks.get(index).setThreadindex(lasterworker.getThreadindex());

			}

			// 启动线程
			Enumeration<Downloadworker> en = workers.elements();
			while (en.hasMoreElements()) {
				Downloadworker worker = en.nextElement();
				worker.start();
			}
			// JOptionPane.showMessageDialog(null,"debug:thread started()");
			if (notifyer != null) {
				notifyer.notify(blocks);
			}

			// 启动下载
			try {
				blocks.notifyAll();
				blocks.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// 再分配任务
			for (;;) {
				Vector<Downloadworker> notbusiworkers = new Vector<Downloadworker>();
				en = workers.elements();
				while (en.hasMoreElements()) {
					Downloadworker worker = en.nextElement();
					if (!worker.isBusi() && worker.isRunning()) {
						notbusiworkers.add(worker);
					}
				}

				// int notbusicount = notbusiworkers.size();

				// 分配任务，找现在没有完成的任务，并进行分配。
				Vector<Emptyblockinfo> emptyinfos = new Vector<Emptyblockinfo>();
				int startindex = -1;
				int endindex = -1;
				for (int i = 0; i < blocks.size(); i++) {
					Blockinfo info = blocks.get(i);

					if (i == blocks.size() - 1 && !info.isFinished()) {
						if (startindex >= 0) {
							endindex = i;
							Emptyblockinfo emptyinfo = new Emptyblockinfo(
									startindex, endindex);
							emptyinfos.add(emptyinfo);
							startindex = -1;
						} else {
							startindex = i;
							endindex = i;
							Emptyblockinfo emptyinfo = new Emptyblockinfo(
									startindex, endindex);
							emptyinfos.add(emptyinfo);
							startindex = -1;
						}
					} else if (info.isFinished()) {
						if (startindex >= 0) {
							if (info.isFinished()) {
								endindex = i - 1;
							} else {
								endindex = i;
							}
							Emptyblockinfo emptyinfo = new Emptyblockinfo(
									startindex, endindex);
							emptyinfos.add(emptyinfo);
							startindex = -1;
						}
					} else {
						if (startindex == -1)
							startindex = i;
					}
				}

				// 现在已找出了所有没有完成的。
				// log("emptyinfos.size()=" + emptyinfos.size());
				if (emptyinfos.size() == 0) {
					// 全部下载完成了。
					if (notifyer != null) {
						notifyer.notify(blocks);
					}
					break;
				}

				// 如果有闲的线程，将任务重新分配。
				Enumeration<Emptyblockinfo> emptyen = emptyinfos.elements();
				while (emptyen.hasMoreElements() && notbusiworkers.size() > 0) {
					Emptyblockinfo emptyinfo = emptyen.nextElement();
					// 找到一个正在下载的块。
					int lastdownloading;
					for (lastdownloading = emptyinfo.endindex; lastdownloading > emptyinfo.startindex; lastdownloading--) {
						if (!blocks.get(lastdownloading).isFinished()
								&& blocks.get(lastdownloading).isLock()) {
							break;
						}
					}
					// 从正在下载的下一个开始
					lastdownloading++;
					int size = emptyinfo.endindex - lastdownloading + 1;
					if (size < 1) {
						// 如果没有下载的数据量太小，也不值得再分配了。
						continue;
					}
					Downloadworker worker = notbusiworkers.elementAt(0);
					notbusiworkers.remove(0);
					// 新分配
					int newsize = size / 2;
					int more = size % 2;
					int newstartindex = lastdownloading + newsize;
					int k;

					log("重新给线程" + worker.getThreadindex() + "新任务 block index="
							+ newstartindex + ",block count=" + newsize);
					for (k = 0; k < newsize; k++) {
						blocks.get(newstartindex + k).setThreadindex(
								worker.getThreadindex());
					}
					if (more != 0) {
						blocks.get(newstartindex + k).setThreadindex(
								worker.getThreadindex());
					}

				}

				try {
					blocks.notifyAll();
					blocks.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				// log("main thread got control");
			}

		}

		Enumeration<Downloadworker> en = workers.elements();
		while (en.hasMoreElements()) {
			Downloadworker worker = en.nextElement();
			worker.stopRun();
		}
		synchronized (blocks) {
			blocks.notifyAll();
		}

		result=true;
		log("下载完成");
		if (notifydlg != null) {
			notifydlg.setVisible(false);
		}
		return true;
	}

	public String getErrormessage() {
		return errormessage;
	}

	/**
	 * 线程数
	 */
	int MAXTHREAD = 10;
	/**
	 * 分配的块的最小单位。32k
	 */
	int blocksize = DefaultNPParam.binfileblocksize;
	int timeout = 10 * 1000;
	int maxretrycount = 10;

	ArrayList<Blockinfo> blocks = null;
	File downloadfile = null;
	File outdir = null;
	Vector<Downloadworker> workers = null;

	int totaldownloadsize;
	long starttime = 0;

	DownloadNotifyIF notifyer = null;

	Object writelockobject = new Object();
	private int filelength;

	public void stopDownload() {
		if (workers == null)
			return;
		Enumeration<Downloadworker> en = workers.elements();
		while (en.hasMoreElements()) {
			Downloadworker worker = en.nextElement();
			worker.stopRun();
		}
		synchronized (blocks) {
			blocks.notifyAll();
		}
	}

	public void notifyDownload(int size) {
		totaldownloadsize += size;

		StringBuffer sb = new StringBuffer();
		sb.append("速度"
				+ StringUtil.bytespeed2string(totaldownloadsize, System
						.currentTimeMillis()
						- starttime));
		sb.append(",已下载" + StringUtil.bytes2string(totaldownloadsize));
		log(sb.toString());

		if (notifyer != null) {
			notifyer.notify(filelength, totaldownloadsize, System
					.currentTimeMillis()
					- starttime);
			notifyer.notify(blocks);
		}
		// JOptionPane.showMessageDialog(null, "notifyDownload ok");

	}

	class Emptyblockinfo {
		Emptyblockinfo(int startindex, int endindex) {
			this.startindex = startindex;
			this.endindex = endindex;
		}

		int startindex;
		int endindex;
	}

	void createFile(int filepos, byte[] buf, int buflen) {
		synchronized (writelockobject) {
			FileOutputStream fout=null;
			try {
				// JOptionPane.showMessageDialog(null, "writeFile
				// file="+downloadfile.getAbsolutePath());
				
				fout = new FileOutputStream(downloadfile);
				fout.write(buf, 0, buflen);
				// JOptionPane.showMessageDialog(null, "write file
				// ok,notifydownload");
				// JOptionPane.showMessageDialog(null, "write file
				// ok,notifydownload ok");
			} catch (Exception e) {
				// logger.error("error", e);
				JOptionPane.showMessageDialog(null, e.getMessage());
			} finally {
				if (fout != null) {
					try {
						fout.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}
	
	/**
	 * 写文件不要重入
	 */

	void writeFile(int filepos, byte[] buf, int buflen) {
		RandomAccessFile rfile = null;
		synchronized (writelockobject) {
			try {
				// JOptionPane.showMessageDialog(null, "writeFile
				// file="+downloadfile.getAbsolutePath());
				rfile = new RandomAccessFile(downloadfile, "rw");
				rfile.seek(filepos);
				rfile.write(buf, 0, buflen);
				// JOptionPane.showMessageDialog(null, "write file
				// ok,notifydownload");
				// JOptionPane.showMessageDialog(null, "write file
				// ok,notifydownload ok");
			} catch (Exception e) {
				// logger.error("error", e);
				JOptionPane.showMessageDialog(null, e.getMessage());
			} finally {
				if (rfile != null) {
					try {
						rfile.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}

	/**
	 * 下载执行线程。
	 * 
	 * @author Administrator
	 * 
	 */
	class Downloadworker extends Thread {
		private int threadindex = 0;
		private boolean running = false;
		private boolean busi = false;

		Downloadworker(int threadindex) {
			boolean connectok = false;
			this.threadindex = threadindex;
		}

		public void stopRun() {
			running = false;

		}

		public boolean isRunning() {
			return running;
		}

		public boolean isBusi() {
			return busi;
		}

		public int getThreadindex() {
			return threadindex;
		}

		/**
		 * 下载线程
		 */
		public void run() {
			// 开始执行任务,循环等待

			// JOptionPane.showMessageDialog(null,"worker start");
			running = true;
			busi = true;
			try {
				while (running) {
					// 找任务
					int startblockindex = -1;
					while (running) {
						// JOptionPane.showMessageDialog(null,"debug,before

						// 这里不对blocks加锁
						// JOptionPane.showMessageDialog(null,"debug,in
						// log("thread "+threadindex+" begin
						// synchronized (blocks) ");
						synchronized (blocks) {
							for (int i = 0; i < blocks.size(); i++) {
								Blockinfo block = blocks.get(i);
								if (!block.isFinished()
										&& block.getThreadindex() == threadindex) {
									// 找到开始的block号了
									// log("thread "+threadindex+
									// " lock block index "+i);
									startblockindex = i;
									break;
								}
							}
							// JOptionPane.showMessageDialog(null,"debug,startblockindex="+startblockindex);
							if (startblockindex < 0) {
								// 说明没有找到要找的数据。等待。
								try {
									busi = false;
									// log("thread
									// "+threadindex+" begin wait");
									blocks.wait();
									// log("thread
									// "+threadindex+" end wait");
									busi = true;
								} catch (InterruptedException e) {
								}
								if (!running)
									return;
							} else {
								break;
							}
						}
					}
					if (!running)
						return;
					// 下载任务
					busi = true;
					// log("download thread " + threadindex
					// + " start download from index " + startblockindex);
					doDownload(startblockindex);
					synchronized (blocks) {
						// 下载完成后，发送通知。表明现在不忙了
						busi = false;
						blocks.notifyAll();
					}

					// logger
					// .debug("download thread " + threadindex
					// + " not busi now");
				}
			} finally {
				log("thread " + threadindex + ",exit");
			}
		}

		void doDownload(int startblockindex) {
			int i;
			try {
				for (i = startblockindex; running && i < blocks.size(); i++) {
					// 下载一块
					Blockinfo block = null;
					// 这里不要对blocks加锁
					block = blocks.get(i);
					// log("thread "+threadindex+" doDownload
					// synchronized (block) ");
					synchronized (block) {
						if (block.isLock() || block.isFinished()
								|| block.getThreadindex() != threadindex) {
							// 说明分配给别人了。
							return;
						}
						// 设置已开始处理
						block.lock();

					}
					// log("thread "+threadindex+" doDownload
					// begin reveiveOneblock blockindex="+i);
					if (!reveiveOneblock(block)) {
						// 发生了错误，要重新连接
						i--;
					}

				} // end for
				// 因为每次指定的Range不同，所以要关闭了重新开。
			} finally {
			}
			/*
			 * synchronized (blocks) { // 退出有可能是因为出了致命错了。将任务全仍给worker1 for (; i <
			 * blocks.size(); i++) { Blockinfo blockinfo = blocks.get(i); if
			 * (!blockinfo.isFinished() && blockinfo.getThreadindex() ==
			 * threadindex) { blockinfo.setThreadindex(1);// 分配给worker1 } }
			 * 
			 * blocks.notifyAll(); }
			 */
		}

		boolean reveiveOneblock(Blockinfo block) {
			// 进行下载，写文件

			// 下载，写文件

			// JOptionPane.showMessageDialog(null, "filepos="+filepos);

			// 接收数据。
			// JOptionPane.showMessageDialog(null, "wanted="+wanted);
			for (;;) {
				try {
					int startpos = block.getFilestartpos();

					ServerResponse resp = sendRequest(startpos);
					if (!resp.getCommand().startsWith("+OK"))
						continue;
					ParamCommand resppcmd = (ParamCommand) resp.commandAt(1);
					String filename = resppcmd.getValue("filename");
					downloadfile = new File(outdir, filename);
					downloadfile.getParentFile().mkdirs();
					int len = Integer.parseInt(resppcmd.getValue("length"));
					BinfileCommand bfc = (BinfileCommand) resp.commandAt(2);
					writeFile(startpos, bfc.getBindata(), len);
					break;

				} catch (Exception e) {
					block.releasLock();
					return false;
				}
			}
			// JOptionPane.showMessageDialog(null, "rded="+rded);
			// log("thread " + threadindex + " writer
			// file
			// ");
			// log("thread "+threadindex+" begin write file ");
			// JOptionPane.showMessageDialog(null, "writen");

			// 下载完成了
			// log("thread "+threadindex+" begin synchronized
			// (blocks) ");
			synchronized (blocks) {
				// JOptionPane.showMessageDialog(null, "setFinished");
				// log("thread " + threadindex
				// + " setFinished ");
				block.setFinished();
				block.releasLock();
				notifyDownload(block.getFilesize());
				/*
				 * logger.debug("download thread " + threadindex + " writern
				 * file pos " + block.getFilestartpos() + ",size=" +
				 * block.getFilesize());
				 */
				// JOptionPane.showMessageDialog(null, "notifyAll");
				// 设置busi为false，让主线程能进行再分配。
				blocks.notifyAll();
			}
			// log("thread "+threadindex+" setFinished ok");

			return true;
		}
	}

	public File getDownloadfile() {
		return downloadfile;
	}

	void log(String msg) {
		System.out.println(msg);
		if (notifyer != null) {
			notifyer.log(msg + "\n");
		}
	}

	Random random = new Random(System.currentTimeMillis());

	protected ServerResponse sendRequest(int startpos) throws Exception {
		ClientRequest req = new ClientRequest(reqcommand);
		ParamCommand pcmd = new ParamCommand();
		req.addCommand(pcmd);
		pcmd.addParam("startpos", String.valueOf(startpos));
		// 其它参数
		Enumeration<NVPair> en = otherparamcmd.getNvpairs().elements();
		while (en.hasMoreElements()) {
			NVPair nvp = en.nextElement();
			pcmd.addParam(nvp.getName(), nvp.getValue());
		}
		ServerResponse resp = SendHelper.sendRequest(req);

		// ///////////测试延时
		// Thread.sleep(random.nextInt(3000));

		// int mm=random.nextInt(10);
		// if(mm<4)
		// throw new Exception("下载一个块失败");
		// //////////////////////////////////
		return resp;
	}

	public static void main(String[] args) {
		try {
			DefaultNPParam.debug = 0;
			DefaultNPParam.develop = 0;
			DefaultNPParam.defaultappsvrurl = "http://192.9.200.1/npserver/clientrequest.do";

			String cmd = "npclient:downloadmodulefile";
			ParamCommand pcmd = new ParamCommand();
			pcmd.addParam("prodname", "npbusi");
			pcmd.addParam("modulename", "业务帐务");
			MThreadDownloadhelper dl = new MThreadDownloadhelper();
			boolean ret = dl.download(null, "download test", cmd, pcmd,
					new File("test"));
			System.out.println("down load result=" + ret);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
