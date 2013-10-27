package com.inca.npserver.clientinstall;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JOptionPane;

//import javax.swing.JOptionPane;

//import org.apache.log4j.Category;

/**
 * ���߳�����
 * 
 * @author Administrator
 * 
 */
public class MultithreadDownloader {
	// Category logger = Category.getInstance(MultithreadDownloader.class);
	// Category logger = Category.getInstance(MultithreadDownloader.class);
	int MAXTHREAD = 5;
	/**
	 * ����Ŀ����С��λ��
	 */
	int blocksize = 1024 * 128;
	int timeout = 10 * 1000;
	int maxretrycount = 10;

	ArrayList<Blockinfo> blocks = null;
	private URL url;
	File outdir = null;
	File downloadfile = null;
	Vector<Downloadworker> workers = null;

	int totaldownloadsize;
	long starttime = 0;

	DownloadNotifyIF notifyer = null;
	String referurl = "";

	Object writelockobject = new Object();
	private int filelength;
	private String contenttype = "";

	public boolean download(URL url, String refer, File outdir,
			DownloadNotifyIF notifyer) {
		// JOptionPane.showMessageDialog(null,"debug:download begin");
		this.url = url;
		this.outdir = outdir;
		this.notifyer = notifyer;
		this.referurl = refer;
		totaldownloadsize = 0;
		// �����صõ���С���ٷ��䡣
		HttpURLConnection con = null;
		boolean connectok = false;
		filelength = 0;
		starttime = System.currentTimeMillis();

		for (int i = 0; i < maxretrycount; i++) {
			int respcode = -1;
			String respmsg = "";
			try {
				log("begin connect to " + this.url.toString());
				con = createConnection();
				con.setInstanceFollowRedirects(false);
				// log("begin connect");
				respcode = con.getResponseCode();
				respmsg = con.getResponseMessage();
				filelength = con.getContentLength();
				contenttype = con.getContentType();

				if (respcode >= 400) {
					log("��������:" + respcode + " " + respmsg);
					return false;
				} else if (respcode >= 300 && respcode < 400) {
					// ˵�����ض���
					String newlocaltion = con.getHeaderField("Location");
					if (newlocaltion == null || newlocaltion.length() == 0) {
						log("�ض����Ҳ����µ�URL");
						return false;
					} else {
						referurl = this.url.toString();
						this.url = new URL(newlocaltion);
						maxretrycount--;
						log("�ض���" + newlocaltion);
						continue;
					}
				}

				log("filelength=" + filelength);
				// ��������ļ���
				createOutfilename();
				if (filelength == -1) {
					System.out
							.println("Unknown file length,must be one thread mode");
					return downloadUnknownlength(con);
				}

				int testsize = filelength / MAXTHREAD;
				do {
					if (testsize < blocksize) {
						if (blocksize <= 32 * 1024)
							break;
						blocksize = blocksize >> 1;
					}
				} while (testsize < blocksize);
				log("use blocksize=" + blocksize);
				connectok = true;
				break;
			} catch (Exception e) {
				log(respcode + " " + respmsg + " " + e.getMessage());
				// logger.error("error", e);
				// JOptionPane.showMessageDialog(null,e.getMessage());
			}
		}
		if (!connectok)
			return false;

		// ��blocksize������
		blocks = new ArrayList<Blockinfo>();
		int p;
		for (p = 0; p < filelength; p += blocksize) {
			int size = filelength - p;
			if (size > blocksize)
				size = blocksize;
			Blockinfo info = new Blockinfo(p, size);
			blocks.add(info);
		}

		log("blocks count=" + blocks.size());
		synchronized (blocks) {
			// �����߳�
			workers = new Vector<Downloadworker>();
			for (int i = 0; i < MAXTHREAD; i++) {
				Downloadworker worker = null;
				if (i == 0) {
					worker = new Downloadworker(con, i + 1);
				} else {
					worker = new Downloadworker(i + 1);
				}
				workers.add(worker);
			}
			// ��ƽ�����䡣
			int blockcountperthread = blocks.size() / MAXTHREAD;
			if (blockcountperthread == 0) {
				// һ���͹���
				blockcountperthread = 1;
			}
			int index = 0;
			Enumeration<Downloadworker> en1 = workers.elements();
			while (en1.hasMoreElements()) {
				Downloadworker worker = en1.nextElement();
				for (int k = 0; k < blockcountperthread; k++) {
					if (index + k > blocks.size() - 1)
						break;
					blocks.get(index + k).setThreadindex(
							worker.getThreadindex());
				}
				index += blockcountperthread;
			}
			// ������ͷ�����һ������
			Downloadworker lasterworker = workers.lastElement();
			for (; index < blocks.size(); index++) {
				blocks.get(index).setThreadindex(lasterworker.getThreadindex());

			}

			// �����߳�
			Enumeration<Downloadworker> en = workers.elements();
			while (en.hasMoreElements()) {
				Downloadworker worker = en.nextElement();
				worker.start();
			}
			// JOptionPane.showMessageDialog(null,"debug:thread started()");
			if (notifyer != null) {
				notifyer.notify(blocks);
			}

			// ��������
			try {
				blocks.notifyAll();
				blocks.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// �ٷ�������
			for (;;) {
				Vector<Downloadworker> notbusiworkers = new Vector<Downloadworker>();
				en = workers.elements();
				while (en.hasMoreElements()) {
					Downloadworker worker = en.nextElement();
					if (!worker.isBusi() && worker.isRunning()) {
						notbusiworkers.add(worker);
					}
				}

				int notbusicount = notbusiworkers.size();

				// ��������������û����ɵ����񣬲����з��䡣
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

				// �������ҳ�������û����ɵġ�
				// log("emptyinfos.size()=" + emptyinfos.size());
				if (emptyinfos.size() == 0) {
					// ȫ����������ˡ�
					if (notifyer != null) {
						notifyer.notify(blocks);
					}
					break;
				}

				// ������е��̣߳����������·��䡣
				Enumeration<Emptyblockinfo> emptyen = emptyinfos.elements();
				while (emptyen.hasMoreElements() && notbusiworkers.size() > 0) {
					Emptyblockinfo emptyinfo = emptyen.nextElement();
					// �ҵ�һ���������صĿ顣
					int lastdownloading;
					for (lastdownloading = emptyinfo.endindex; lastdownloading > emptyinfo.startindex; lastdownloading--) {
						if (!blocks.get(lastdownloading).isFinished()
								&& blocks.get(lastdownloading).isLock()) {
							break;
						}
					}
					// ���������ص���һ����ʼ
					lastdownloading++;
					int size = emptyinfo.endindex - lastdownloading + 1;
					if (size < 3) {
						// ���û�����ص�������̫С��Ҳ��ֵ���ٷ����ˡ�
						continue;
					}
					Downloadworker worker = notbusiworkers.elementAt(0);
					notbusiworkers.remove(0);
					// �·���
					int newsize = size / 2;
					int more = size % 2;
					int newstartindex = lastdownloading + newsize;
					int k;

					log("���¸��߳�" + worker.getThreadindex() + "������ block index="
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

		log("�������");
		return true;
	}

	public void stopDownload(){
		if(workers==null)return;
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
		sb.append("�ٶ�"
				+ bytespeed2string(totaldownloadsize, System
						.currentTimeMillis()
						- starttime));
		sb.append(",������" + bytes2string(totaldownloadsize));
		log(sb.toString());

		if (notifyer != null) {
			notifyer.notify(filelength, totaldownloadsize, System
					.currentTimeMillis()
					- starttime);
			notifyer.notify(blocks);
		}
		// JOptionPane.showMessageDialog(null, "notifyDownload ok");

	}

	private void createOutfilename() {
		String s = url.toString();
		int p = s.lastIndexOf("/");
		String filename = s.substring(p + 1);
		if (filename.length() == 0) {
			filename = "temp";
		}
		if(filename.length()>30){
			//��ȥǰ��ġ�
			filename=filename.substring(filename.length()-30);
		}
		filename = filterFilename(filename);
		char c = filename.charAt(filename.length() - 4);
		if (c != '.' && !filename.endsWith(".torrent")) {
			// �Ӻ�׺
			filename += getFilepostfix();
		}
		if (!outdir.exists()) {
			outdir.mkdirs();
		}
		downloadfile = new File(outdir, filename);
		downloadfile.delete();
	}

	private String getFilepostfix() {
		String postfix = contenttype;
		int p = contenttype.lastIndexOf("-");
		if (p >= 0) {
			postfix = contenttype.substring(p + 1);
		}
		p = postfix.lastIndexOf("/");
		if (p >= 0) {
			postfix = postfix.substring(p + 1);
		}

		if (postfix.equals("msvideo")) {
			postfix = "avi";
		}else if(postfix.indexOf("torrent")>=0){
			postfix="torrent";
		}

		return "." + postfix;
	}

	String filterFilename(String filename) {
		filename = filename.replace('?', '_');
		filename = filename.replace('=', '_');
		return filename;
	}

	class Emptyblockinfo {
		Emptyblockinfo(int startindex, int endindex) {
			this.startindex = startindex;
			this.endindex = endindex;
		}

		int startindex;
		int endindex;
	}

	/**
	 * д�ļ���Ҫ����
	 */

	public void writeFile(int filepos, byte[] buf, int buflen) {
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
	 * ����ִ���̡߳�
	 * 
	 * @author Administrator
	 * 
	 */
	class Downloadworker extends Thread {
		HttpURLConnection con = null;
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

		Downloadworker(HttpURLConnection con, int threadindex) {
			this.con = con;
			this.threadindex = threadindex;
		}

		public boolean isBusi() {
			return busi;
		}

		public int getThreadindex() {
			return threadindex;
		}

		/**
		 * �����߳�
		 */
		public void run() {
			// ��ʼִ������,ѭ���ȴ�

			// JOptionPane.showMessageDialog(null,"worker start");
			running = true;
			busi = true;
			try {
				while (running) {
					// ������
					int startblockindex = -1;
					while (running) {
						// JOptionPane.showMessageDialog(null,"debug,before

						// ���ﲻ��blocks����
						// JOptionPane.showMessageDialog(null,"debug,in
						// log("thread "+threadindex+" begin
						// synchronized (blocks) ");
						synchronized (blocks) {
							for (int i = 0; i < blocks.size(); i++) {
								Blockinfo block = blocks.get(i);
								if (!block.isFinished()
										&& block.getThreadindex() == threadindex) {
									// �ҵ���ʼ��block����
									// log("thread "+threadindex+
									// " lock block index "+i);
									startblockindex = i;
									break;
								}
							}
							// JOptionPane.showMessageDialog(null,"debug,startblockindex="+startblockindex);
							if (startblockindex < 0) {
								// ˵��û���ҵ�Ҫ�ҵ����ݡ��ȴ���
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
					// ��������
					busi = true;
					// log("download thread " + threadindex
					// + " start download from index " + startblockindex);
					doDownload(startblockindex);
					synchronized (blocks) {
						// ������ɺ󣬷���֪ͨ���������ڲ�æ��
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
			InputStream in = null;
			int i;
			try {
				for (i = startblockindex; running && i < blocks.size(); i++) {
					// ����һ��
					Blockinfo block = null;
					// ���ﲻҪ��blocks����
					block = blocks.get(i);
					// log("thread "+threadindex+" doDownload
					// synchronized (block) ");
					synchronized (block) {
						if (block.isLock() || block.isFinished()
								|| block.getThreadindex() != threadindex) {
							// ˵������������ˡ�
							return;
						}
						// �����ѿ�ʼ����
						block.lock();

					}
					// log("thread "+threadindex+" doDownload
					// begin reveiveOneblock blockindex="+i);
					if (!reveiveOneblock(block, in)) {
						// �����˴���Ҫ��������
						try {
							if (in != null)
								in.close();
							in = null;
						} catch (IOException e) {
						}
						con.disconnect();
						con = null;
						i--;
					}

				} // end for
				// ��Ϊÿ��ָ����Range��ͬ������Ҫ�ر������¿���
			} finally {
				try {
					if (in != null)
						in.close();
					in = null;
				} catch (IOException e) {
				}
				if (con != null) {
					con.disconnect();
				}
				con = null;
			}
			synchronized (blocks) {
				// �˳��п�������Ϊ�����������ˡ�������ȫ�Ը�worker1
				for (; i < blocks.size(); i++) {
					Blockinfo blockinfo = blocks.get(i);
					if (!blockinfo.isFinished()
							&& blockinfo.getThreadindex() == threadindex) {
						blockinfo.setThreadindex(1);// �����worker1
					}
				}

				blocks.notifyAll();
			}
		}

		private byte[] buf = new byte[blocksize];

		boolean reveiveOneblock(Blockinfo block, InputStream in) {
			// �������أ�д�ļ�

			// ���أ�д�ļ�
			int filepos = block.getFilestartpos();
			// JOptionPane.showMessageDialog(null, "filepos="+filepos);

			// �������ݡ�
			int wanted = block.getFilesize();
			// JOptionPane.showMessageDialog(null, "wanted="+wanted);
			int totalreaded = 0;
			int rded = 0;
			int errorcode = 0;
			String errormsg = "";
			do {
				try {
					boolean setfilepos = false;
					if (con == null) {
						con = createConnection();
						con.addRequestProperty("Range", "bytes=" + filepos
								+ "-");
						setfilepos = true;
					}
					// ���÷�Χ
					if (in == null) {
						int respcode = con.getResponseCode();
						if (setfilepos && respcode != 206) {
							// �����������ʼλ�ã�����Ӧ����206

							log("resp code is " + respcode
									+ ",expect 206,thread " + threadindex
									+ " retry");
							Thread.sleep(3000);
							throw new Exception("retry");

						}

						String ctype = con.getContentType();
						if (!ctype.equals(contenttype)) {
							log("this contenttype=" + ctype + ",expect "
									+ contenttype + ",thread exit");
							// ˵����֧�ֶ��̡߳��˳����̡߳�
							block.releasLock();
							this.stopRun();
							return false;
						}
						in = con.getInputStream();
					}
					// log("thread " + threadindex
					// + ",totalreaded=" + totalreaded + ",wanted="
					// + wanted);
					rded = in.read(buf, totalreaded, wanted);
					// log("thread " + threadindex + ",rded=" + rded);
					if (rded <= 0) {
						// ����ʧ��
						throw new Exception("readed <= 0");
					}
					/*
					 * if(threadindex==1){ Thread.sleep(300); }
					 * if(threadindex==2){ Thread.sleep(600); }
					 */
					totalreaded += rded;
					wanted -= rded;
					if (wanted == 0) {
						break;
					}

				} catch (Exception e) {
					// logger.error("error", e);
					// JOptionPane.showMessageDialog(null,e.getMessage());
					if (e instanceof SocketTimeoutException) {
						errorcode = -1;
					} else {
						try {
							errorcode = con.getResponseCode();
						} catch (Exception ee) {
						}
						;
					}
					log("Threadindex=" + threadindex + ",filepos=" + filepos
							+ "," + errorcode + " " + errormsg + " "
							+ e.getMessage());
					// �������صĲ����ȱ���������
					if (totalreaded > 0) {
						int oldsize = block.getFilesize();
						int newsizesize = oldsize - totalreaded;
						writeFile(filepos, buf, totalreaded);
						if (newsizesize > 0) {
							block.resetFilestartpos(filepos + totalreaded,
									newsizesize);
						} else {
							// Ӧ����Զ���ᷢ���������
							block.setFinished();
						}
					}
					block.releasLock();
					// �����������ʧ���ˣ�Ҫ�����������ظú��������·��䡣
					return false;
				}
			} while (wanted > 0);
			// JOptionPane.showMessageDialog(null, "rded="+rded);
			// log("thread " + threadindex + " writer
			// file
			// ");
			// log("thread "+threadindex+" begin write file ");
			writeFile(filepos, buf, totalreaded);
			// JOptionPane.showMessageDialog(null, "writen");

			// ���������
			// log("thread "+threadindex+" begin synchronized
			// (blocks) ");
			synchronized (blocks) {
				// JOptionPane.showMessageDialog(null, "setFinished");
				// log("thread " + threadindex
				// + " setFinished ");
				block.setFinished();
				block.releasLock();
				notifyDownload(totalreaded);
				/*
				 * logger.debug("download thread " + threadindex + " writern
				 * file pos " + block.getFilestartpos() + ",size=" +
				 * block.getFilesize());
				 */
				// JOptionPane.showMessageDialog(null, "notifyAll");
				// ����busiΪfalse�������߳��ܽ����ٷ��䡣
				blocks.notifyAll();
			}
			// log("thread "+threadindex+" setFinished ok");

			return true;
		}
	}

	public static String bytespeed2string(int bytes, long ms) {
		StringBuffer sb = new StringBuffer();
		DecimalFormat fm = new DecimalFormat("0.0");
		double speed = (double) bytes / (ms / 1000.0);
		if (speed >= 1048576.0) {
			sb.append(fm.format(speed / 1048576.0) + "M");
		} else if (speed > 1024) {
			sb.append(fm.format(speed / 1024) + "K");
		} else {
			sb.append(fm.format(speed) + "B");
		}

		return sb.toString();
	}

	public static String bytes2string(int bytes) {
		StringBuffer sb = new StringBuffer();
		DecimalFormat fm = new DecimalFormat("0.0");
		double db = (double) bytes;
		if (db >= 1048576.0) {
			sb.append(fm.format(db / 1048576.0) + "M");
		} else if (db > 1024) {
			sb.append(fm.format(db / 1024) + "K");
		} else {
			sb.append(fm.format(db) + "B");
		}

		return sb.toString();
	}

	HttpURLConnection createConnection() throws Exception {
		HttpURLConnection con;
		con = (HttpURLConnection) url.openConnection();
		con.setConnectTimeout(timeout);
		con.setReadTimeout(timeout);
		con.addRequestProperty("User-Agent",
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT");
		con.addRequestProperty("Accept", "*/*");
		con.addRequestProperty("Cache-Control", "close");
		con.addRequestProperty("Pragma", "no-cache");

		if (referurl.length() > 0) {
			con.addRequestProperty("Referer", referurl);
		}

		return con;
	}

	boolean downloadUnknownlength(HttpURLConnection con) {
		InputStream in = null;
		byte[] buf = new byte[32 * 1024];
		try {
			in = con.getInputStream();
			int totalreaded = 0;
			for (;;) {
				int rd = in.read(buf);
				if (rd <= 0)
					break;
				writeFile(totalreaded, buf, rd);
				totalreaded += rd;
			}
			return true;
		} catch (Exception e) {
			try {
				log(con.getResponseCode() + " " + con.getResponseMessage()
						+ " " + e.getMessage());
			} catch (Exception e1) {
			}
			return false;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
				con.disconnect();
				con = null;
			}
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

	public static void main(String[] args) {
		File fs[]=new File("download").listFiles();
		for(int i=0;i<fs.length;i++){
/*			boolean ret=fs[i].delete();
			System.out.println(ret);
*/		}
		
		
		MultithreadDownloader dl = new MultithreadDownloader();
		try {
			// URL u=new
			// URL("http://218.247.157.239/jre-1_5_0_12-windows-i586-p.exe");
			// URL u = new
			// URL("http://218.247.157.239/npserver/npserver_c.zip");
			URL u = new URL(
					"http://51hot.tudou.com/flv/016/300/891/16300891.flv");
			File downloadfile = new File("download.zip");
			boolean ret = dl.download(u, "", downloadfile, null);
			System.out.println("down load result=" + ret);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
