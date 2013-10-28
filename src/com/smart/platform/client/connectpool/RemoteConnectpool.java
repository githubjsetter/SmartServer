package com.smart.platform.client.connectpool;

import org.apache.log4j.Category;

import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ServerResponse;

/**
 * 最多尝试三次,超时5秒,发送信息.
 * 
 * @author user
 * 
 */
public class RemoteConnectpool {
	String authstring;
	ClientRequest req;

	// 等10秒
	int timeout = 10000;
	String strurl;
	int MAXWORKER = 3;
	Sendworker workers[] = null;
	Object signobject = new Object();
	int retrievedsize = 0;
	int inflatedsize = 0;

	public static int msgseqid = 0;
	Category logger = Category.getInstance(RemoteConnectpool.class);

	public RemoteConnectpool(String strurl, ClientRequest req) {
		super();
		this.strurl = strurl;
		this.req = req;
		this.authstring = req.getAuthstring();
		// 分配msgseqid
		int thisseqid = msgseqid++;
		req.putContextvalue("msgid", req.getAuthstring() + "."
				+ String.valueOf(System.currentTimeMillis()) + "."
				+ String.valueOf(thisseqid));

		workers=new Sendworker[MAXWORKER];
		for (int i = 0; i < MAXWORKER; i++) {
			workers[i] = new Sendworker(i, strurl, req, signobject);
		}
	}

	public ServerResponse doSend() throws Exception {

		// 一个个发出.
		for (int i = 0; i < MAXWORKER; i++) {
			synchronized (signobject) {
				logger.debug("workerid=" + i + " start");
				workers[i].start();
				signobject.wait(timeout);
				logger.debug("开始检查状态");

				// 有完成的吗?
				for (int j = 0; j <= i; j++) {
					logger.debug("workerid=" + j + " 状态="
							+ workers[j].getRespStatus());
					if (workers[j].getRespStatus() == 1) {
						logger.debug("返回 workerid="+j+"的服务器响应");
						retrievedsize=workers[j].getRetrievedSize();
						inflatedsize=workers[j].getInflatSize();
						return workers[j].getSvrresp();
					}
				}
			}
		}

		logger.debug("线程都启动,还是没有状态返回,循环5次再等");

		// 如果还没有完成,只能等了
		for (int t = 0;t<100 ; t++) {
			synchronized (signobject) {
				signobject.wait(timeout);

				// 有完成的吗?
				for (int j = 0; j < MAXWORKER; j++) {
					logger.debug("workerid=" + j + " 状态="
							+ workers[j].getRespStatus());
					if (workers[j].getRespStatus() == 1) {
						logger.debug("返回 workerid="+j+"的服务器响应");
						retrievedsize=workers[j].getRetrievedSize();
						inflatedsize=workers[j].getInflatSize();
						return workers[j].getSvrresp();
					}
				}

				//有异常的吗
				for (int j = 0; j < MAXWORKER; j++) {
					logger.debug("workerid=" + j + " 状态="
							+ workers[j].getRespStatus());
					if (workers[j].getRespStatus() == 2) {
						logger.debug("扔出workerid="+j+"的异常");
						throw workers[j].getSvrexception();
					}
				}

			}
		}

		logger.debug("发送请求超时.请检查是否是网络中断");
		throw new Exception("发送请求超时.请检查是否是网络中断");
	}

	public int getRetrievedsize() {
		return retrievedsize;
	}

	public int getInflatedsize() {
		return inflatedsize;
	}
	
}
